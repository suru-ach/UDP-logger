import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class UDPServer implements Runnable {
    Thread thread;

    UDPServer() {
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(8080);
            while(true) {
                byte[] response = new byte[1024];
                DatagramPacket packet = new DatagramPacket(response, 1024);
                socket.receive(packet);
                String request = new String(response, 0, packet.getLength());
                System.out.println(request);

                String message = request.substring(0,2);
                DatagramPacket messagePacket = new DatagramPacket(message.getBytes(), message.length(), packet.getAddress(), packet.getPort());
                socket.send(messagePacket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ServerLoggerUDP {
    public static void main(String[] args) throws IOException {

        Buffer buffer = new Buffer(11);
        UDPServer udpServer = new UDPServer();
    }
}
