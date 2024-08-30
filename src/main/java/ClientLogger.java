import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientLogger {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(8080);
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
    }
}
