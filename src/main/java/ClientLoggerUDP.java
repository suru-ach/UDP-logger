import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

class UDPClient implements Runnable {
    Thread thread;
    Buffer buffer;

    UDPClient(Buffer buffer) {
        thread = new Thread(this, "UDPClient");
        this.buffer = buffer;
        thread.start();
    }

    public boolean udp_send(String message, int acknowledgement) throws IOException {
        final int port = 8080;
        final int responseSize = 1024;
        byte[] responseString = new byte[responseSize];

        DatagramSocket socket = new DatagramSocket();
        //Send data over
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getLocalHost(), port);
        socket.send(packet);

        ExecutorService service = Executors.newSingleThreadExecutor();

        final String[] responseMessage = {null};
        try {
            Runnable r = new Runnable() {
                @Override
                public void run(){
                    try {
                        DatagramPacket responsePacket = new DatagramPacket(responseString, responseSize);
                        socket.receive(responsePacket);
                        responseMessage[0] = new String(responseString, 0, responsePacket.getLength());
                        System.out.println(responseMessage[0]);
                        System.out.println("Ack: "+acknowledgement);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            Future<?> future = service.submit(r);
            future.get(2, TimeUnit.SECONDS);
        } catch(final RuntimeException | InterruptedException | ExecutionException exc) {
            throw new RuntimeException(exc);
        } catch(final TimeoutException exc) {
            return true;
        }
        // Validate response message
        System.out.println(responseMessage[0].equals(Integer.toHexString(acknowledgement)));
        // If valid return false
        return false;
    }

    public void run() {
        while(true) {
            String ackMessage = Integer.toHexString(buffer.getIndex_second());
            if(ackMessage.length() == 1) ackMessage = "0" + ackMessage;
            String response = ackMessage + buffer.get();
            System.out.println(response);
            // udp send and wait for acknowledgement
            try {
                boolean udp_ack = true;
                while (udp_ack) {
                    udp_ack = udp_send(response,Integer.valueOf(ackMessage, 16));
                    if(udp_ack == true) {
                        System.out.println("Failed to send.");
                    } else {
                        System.out.println("Successfully send.");
                    }
                }
            } catch(IOException exc) {
                System.out.println("Socket exception " + exc.getMessage());
                return;
            }
        }
    }
}

class Producer implements Runnable {
    Thread thread;
    Buffer buffer;

    Producer(Buffer buffer) {
        thread = new Thread(this, "Producer");
        this.buffer = buffer;
        thread.start();
    }

   public void run() {
        while(true) {
            try {
                // logic to write into

                // Test
                Thread.sleep(1000);
                String message = "Hello "+System.currentTimeMillis()+" "+this.thread.getName();
                buffer.put(message);
            } catch(InterruptedException exc) {
                System.err.println(exc.getMessage());
                return;
            }
        }
    }
}

public class ClientLoggerUDP {
    public static void main(String[] args) {
        // Test
        Buffer buffer = new Buffer(17);
        Producer producer = new Producer(buffer);
        UDPClient client = new UDPClient(buffer);
    }
}
