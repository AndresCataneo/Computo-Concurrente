package P5;

import java.util.concurrent.CountDownLatch;

public class TestConsistenciaShavit {
    static final int profundidad = 7;
    static final int numHilosEscritura = 5; 
    static final int incrementos = 101010;
    static final ShavitTreeCounter contador = new ShavitTreeCounter( profundidad );
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("-".repeat(25));
        
        // Creacion e inicio del hilo lector
        Thread hiloLector = new Thread(() -> {
            try {
                long flag = -1;

                while (flag == -1 ) {
                    flag = contador.fetch(); 
                    System.out.println("Hilo Lector: El total leído es -> " + flag + "/" + (numHilosEscritura * incrementos));
                }
                
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        hiloLector.start();

        // Creacion e inicio de los hilos escritores
        Thread[] escritores = new Thread[numHilosEscritura];
        for (int i = 0; i < numHilosEscritura; i++) {
            final int id = i + 1;
            escritores[i] = new Thread(() -> {
                System.out.println("Hilo " + id + ": Haciendo "+incrementos+" incrementos...");
                for (int j = 0; j < incrementos; j++) {
                    contador.increment();
                }
                System.out.println("Hilo " + id + ": Terminó.");
                // Restamos 1 al contador global para avisar que este hilo acabó
            });
            escritores[i].start();
        }

        for (Thread t : escritores) {
            t.join();
        }
        hiloLector.join();
        
        System.out.println("-".repeat(25));
    }
}
