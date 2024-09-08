import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class LogFile implements Runnable {
    Thread thread;
    String filename;
    Buffer buffer;
    FileOutputStream fileOutputStream;

    LogFile(String filename, Buffer buffer) throws FileNotFoundException {
        this.thread = new Thread(this, "LogFile");
        this.filename = filename;
        this.buffer = buffer;
        this.thread.start();
        this.fileOutputStream = new FileOutputStream(this.filename);
    }

    public void run() {
        while(true) {
            String message = buffer.get();
            byte[] toFile = new byte[message.length()+1];
            byte[] messageByte = message.getBytes();
            int i = 0;
            for(i=0;i<message.length();i++) toFile[i] = messageByte[i];
            toFile[i] = '\n';
            try {
                this.fileOutputStream.write(toFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class UDPServer implements Runnable {
    Thread thread;
    Buffer buffer;

    UDPServer(Buffer buffer) {
        this.buffer = buffer;
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
                buffer.put(request);
                // System.out.println(request);

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
        UDPServer udpServer = new UDPServer(buffer);
        LogFile file = new LogFile("logfile.txt", buffer);
    }
}
