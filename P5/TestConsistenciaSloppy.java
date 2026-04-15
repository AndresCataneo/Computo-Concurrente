package P5;
import java.util.concurrent.CountDownLatch;

/**
 * Programa de pruebas para evaluar el comportamiento de la consistencia de memoria
 * en una implementación modificada de Sloppy Counter.
 * Utiliza múltiples hilos escritores concurrentes coordinados mediante un CountDownLatch
 * para demostrar empíricamente por qué la estructura mantiene una Consistencia Eventual
 * y no alcanza la Consistencia Secuencial, a pesar de forzar volcados de memoria durante
 * la invocación del método fetch().
 * @author Andrés Rodrigo Cataneo Tortolero
 * @version 1.0
 */
public class TestConsistenciaSloppy {
    static final SloppyCounterVolcado contador = new SloppyCounterVolcado();  
    static final int numHilosEsc = 5; 
    static final CountDownLatch latchTerminaron = new CountDownLatch(numHilosEsc); // Bandera. Esperará a que N hilos terminen.
    

    /**
     * Método principal que representa el punto de entrada de la prueba. 
     * Configura el entorno de sincronización, lanza un hilo lector que entra en estado de espera, 
     * y despacha los hilos escritores que manipulan el contador. Al finalizar todos los escritores, 
     * el lector se despierta e intenta leer el estado global para verificar la consistencia.
     * @param args Argumentos de la línea de comandos
     * @throws InterruptedException Si el hilo principal o el hilo lector son interrumpidos durante la espera
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("-".repeat(25));

        // Creacion e inicio del hilo lector
        Thread hiloLector = new Thread(() -> {
            System.out.println("Hilo Lector: Esperando a que TODOS los escritores terminen...");
            try {
                latchTerminaron.await(); 
                
                System.out.println("Hilo Lector: Todos terminaron. Leyendo el total de incrementos.");
                long total = contador.fetch(); 
                
                System.out.println("Hilo Lector: El total leído es -> " + total + "/" + (numHilosEsc * 150));
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        hiloLector.start();

        Thread.sleep(200); 

        // Creacion e inicio de los hilos escritores
        Thread[] escritores = new Thread[numHilosEsc];
        for (int i = 0; i < numHilosEsc; i++) {
            final int id = i + 1;
            escritores[i] = new Thread(() -> {
                System.out.println("Hilo " + id + ": Haciendo 100 incrementos...");
                for (int j = 0; j < 100; j++) {
                    contador.increment();
                }
                System.out.println("Hilo " + id + ": Terminó.");
                // Restamos 1 al contador global para avisar que este hilo acabó
                latchTerminaron.countDown(); 
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
