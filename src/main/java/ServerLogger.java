import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

// Shared resource
class Buffer {
    String[] array;
    int index_first;
    int index_second;
    int full;
    int capacity;

    Buffer(int capacity) {
        this.full = this.index_first = this.index_second = 0;
        this.capacity = capacity;
        array = new String[capacity];
    }

    synchronized void put(String payload) {
        while(capacity == full) {
            try {
                wait();
            } catch(InterruptedException exc) {
                System.out.println(exc.getMessage());
            }
        }
        array[index_first] = payload;
        full++;
        index_first = (index_first+1) % capacity;
        notify();
    }

    synchronized String get() {
        while(full == 0) {
            try {
                wait();
            } catch(InterruptedException exc) {
                System.out.println(exc.getMessage());
            }
        }
        full--;
        int idx = index_second;
        index_second = (index_second + 1) % capacity;
        notify();
        return array[idx];
    }
}

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

        DatagramPacket responsePacket = new DatagramPacket(responseString, responseSize);
        socket.receive(responsePacket);
        String responseMessage = new String(responseString, 0, responsePacket.getLength());
        System.out.println(responseMessage);
        System.out.println("Ack: "+acknowledgement);

        /*
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if()
            }
        }, 2000);
         */

        return false;
    }

    public void run() {
        while(true) {
            String ackMessage = Integer.toHexString(buffer.index_second);
            if(ackMessage.length() == 1) ackMessage = "0" + ackMessage;
            String response = ackMessage + buffer.get();
            System.out.println(response);
            // udp send and wait for acknowledgement
            try {
                while (udp_send(response,Integer.valueOf(ackMessage, 16))) {
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

public class ServerLogger {
    public static void main(String[] args) {
        // Test
        Buffer buffer = new Buffer(17);
        Producer producer = new Producer(buffer);
        UDPClient client = new UDPClient(buffer);
    }
}
