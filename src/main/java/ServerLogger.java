
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
        notify();
        int temp = index_second;
        index_second = (index_second + 1) % capacity;
        return array[temp];
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

    public void run() {
        while(true) {
            String response = buffer.get();
            System.out.println(response);
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
                Thread.sleep(100);
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
        Buffer buffer = new Buffer(10);
        Producer producer = new Producer(buffer);
        UDPClient client = new UDPClient(buffer);
    }
}
