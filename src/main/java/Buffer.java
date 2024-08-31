
// Shared resource
public class Buffer {
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

    int getIndex_second() {
        return this.index_second;
    }
}

