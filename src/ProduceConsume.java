import java.io.FileInputStream;
import java.io.IOException;

public class ProduceConsume extends ProducerConsumerModel {
    int[] input; // referinta la o zona de memorie unde se va incarca fisierul
    FileInputStream fileHandler; // handler pentru citire din fisier
    // o anumita bucata de fisier trebuie plasata in input[] incepand cu un anume index
    int inputIndex;
    // trebuie sa stim cand producatorul nu mai trebuie sa produca
    int haltProducer;
    // trebuie sa stim cand consumatorul nu mai consuma
    int haltConsumer;
    // e posibil sa mai ramana o ramasita din imagine
    int remainingInputSize;
    // trebuie stiut momentul in care bufferul cu care preluam bucati de dimensiune
    // marimeFisier / 4 se umple, respectiv se goleste
    boolean bufferFilled;

    public ProduceConsume(int bufferSize, int[] input, FileInputStream fileHandler) {
        super(bufferSize);
        this.input = input;
        this.fileHandler = fileHandler;
        this.inputIndex = 0;
        this.haltProducer = 0;
        this.haltConsumer = 0;
        this.remainingInputSize = 0;
        this.bufferFilled = false;
    }

    @Override
    void produce() throws InterruptedException, IOException {
        while (haltProducer == 0) {
            synchronized (this)
            {
                // Trebuie asteptat sa se goleasca bufferul inainte ca producatorul sa produca
                while (bufferFilled) {
                    wait();
                }
                // Daca a mai ramas de citit mai mult decat bufferul permite, atunci
                // umplem tot bufferul si se asteapta urmatoarea ocazie de umplere
                // Altfel, inseamna ca am preluat ultima bucata si oprim producatorul
                if (fileHandler.available() > 0) {
                    if (fileHandler.available() >= bufferSize) {
                        for (int j = 0; j < bufferSize; j++) {
                            buffer[j] = fileHandler.read();
                        }
                    } else {
                        int j = 0;
                        while(fileHandler.available() > 0) {
                            buffer[j] = fileHandler.read();
                            j++;
                        }
                        remainingInputSize = j;
                        haltProducer = 1;
                    }
                } else {
                    haltProducer = 1;
                }
                // Explicitare actiune producator
                System.out.println("Producatorul a preluat o portiune din fisierul de intrare");
                bufferFilled = true;
                notify();
                Thread.sleep(1000);
            }
        }
    }

    @Override
    void consume() throws InterruptedException {
        while (haltConsumer == 0) {
            synchronized (this)
            {
                // Se asteapta ca buffer-ul sa se umple
                while (!bufferFilled) {
                    wait();
                }
                // Daca producatorul nu s-a oprit, atunci inseamna ca bufferul e plin
                // Altfel, se preia in memorie si ultima bucata din fisier
                if (haltProducer == 0) {
                    for (int j = 0; j < bufferSize; j++) {
                        input[inputIndex] = buffer[j];
                        inputIndex++;
                    }
                } else {
                    for (int j = 0; j < remainingInputSize; j++) {
                        input[inputIndex] = buffer[j];
                        inputIndex++;
                    }
                    haltConsumer = 1;
                }
                // Explicitare actiune consumator
                System.out.println("Consumatorul a incarcat in memorie bucata incepand de la index=" + inputIndex);
                bufferFilled = false;
                notify();
                Thread.sleep(1000);
            }
        }
    }
}
