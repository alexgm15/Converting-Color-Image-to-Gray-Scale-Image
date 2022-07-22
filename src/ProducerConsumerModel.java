import java.io.IOException;

// Interfata pentru modelul producator-consumator
public abstract class ProducerConsumerModel {
    int bufferSize;
    int[] buffer;

    public ProducerConsumerModel(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new int[bufferSize];
    }

    abstract void produce() throws InterruptedException, IOException;
    abstract void consume() throws InterruptedException;
}
