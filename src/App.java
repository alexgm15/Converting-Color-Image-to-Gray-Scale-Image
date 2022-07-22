import java.io.*;
import java.util.Scanner;

public class App {
    public static int[] readImg(String bitMapURL) throws IOException, InterruptedException {
        File f = new File(bitMapURL);
        FileInputStream in = new FileInputStream(f);
        // Se salveaza lungimea fisierului imagine in variablia input
        int[] input = new int[(int)f.length()];
//        int i = 0;

//        while (in.available() > 0) {
//            input[i] = in.read();
//            i++;
//        }
        ProduceConsume pc = new ProduceConsume((int)(f.length() / 4), input, in);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.produce();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pc.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        return input;
     }
    
    public static int[] processImg(int[] input) {
        
        int[] processedImg = new int[(int)input.length];
        
        //Identificarea adresei de la care incep pixelii imaginii
        int address = 0;
        address = input[10];
        
        //Imaginea procesata va avea aceleasi headere precum imaginea originala
        for (int k = 0; k < address; k++)
        {
            processedImg[k] = input[k];
        }
        
        //Convertirea propriu-zisa a imaginii
        for (int k = address; k < input.length; k+=3)
        {
            int blue = input[k];
            int green = input[k+1];
            int red = input[k+2];
              
            int weight = (int)(red * 0.299 + green * 0.587 + blue * 0.114);
            
            processedImg[k] = weight;
            processedImg[k+1] = weight;
            processedImg[k+2] = weight;
             
        }
        
        return processedImg;
    }
    
    //Salvarea rezultatului conversiei
    public static void writeImg(int[] processedImg, String bitmapURL1) throws IOException{
        //Se introduce de la tastatura un nume pentru fisierul obtinut
        
        FileOutputStream fout;
        fout = new FileOutputStream(bitmapURL1);
        for (int j = 0; j < processedImg.length; j++) {
            fout.write(((byte)processedImg[j]));
        }
     }

    public static void main(String[] args) throws IOException{
        
        //Se citeste de la tastatura numele imaginii de convertit
        long start0 = System.currentTimeMillis();
        Scanner name = new Scanner(System.in);
        System.out.println("Introduceti numele/calea imaginii de convertit: ");
        String s;
        s = name.nextLine();
        long TypeInputTime = System.currentTimeMillis()-start0;
        
        //Incarcarea imaginii originale
        long start = System.currentTimeMillis();
        int[] in = new int[0];
        try {
            in = readImg(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long ReadingTime = System.currentTimeMillis()-start;
        
        //Procesarea imaginii
        long start1 = System.currentTimeMillis();
        int[] processImg = processImg(in);
        long ProcessingTime = System.currentTimeMillis()-start1;
        
        //Se introduce de la tastatura numele imaginii convertite
        long start3 = System.currentTimeMillis();
        Scanner name1 = new Scanner(System.in);
        System.out.println("Introduceti numele/calea imaginii convertite: ");
        String r;
        r = name1.nextLine();
        long TypeOutputTime = System.currentTimeMillis()-start3;
        
        //Salvarea imaginii rezultate
        long start2 = System.currentTimeMillis();
        writeImg(processImg, r);
        long WritingTime = System.currentTimeMillis()-start2;
        
        System.out.println("Timp de introducere nume imagine de la tastatura (milisecunde):" + TypeInputTime);
        System.out.println("Timp de citire al imaginii si al dimensiunii acesteia (milisecunde):" + ReadingTime);
        System.out.println("Timp de procesare al imaginii (milisecunde):" + ProcessingTime);
        System.out.println("Timp de scriere al imaginii (milisecunde):" + WritingTime);
        System.out.println("Timp de introducere nume imagine rezultata (milisecunde):" + TypeOutputTime);
        
     }
}

