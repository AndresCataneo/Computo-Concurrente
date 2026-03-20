package P3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Programa principal que funciona como banco de pruebas para el candado DoblePeterson.
 * Utiliza un pool de hilos para procesar 400 tareas concurrentes que intentan
 * incrementar un contador compartido.
 * * @author Andres Rodrigo Cataneo Tortolero
 * @version 1.0
 */
public class LockDoblePeterson {

    private static int[] tareasPorHilo = new int[4];

    /**
     * Tarea que será ejecutada por los hilos concurrentes.
     * Protege el incremento del contador utilizando el candado proporcionado
     * y registra estadísticamente qué hilo realizó el trabajo.
     * * @param lock  - El candado DoblePeterson a utilizar para la sincronización.
     * @param counter - El objeto contador ingenuo a incrementar.
     */
    private static void task(DoblePeterson lock, CounterNaive counter) {
        lock.lock();
        try {
            counter.increment();
            int id = DoblePeterson.threadId.get();
            tareasPorHilo[id]++;
        } finally {
            lock.unlock();
        }
    }
    /**
     * Método principal que inicializa el pool de 4 hilos, envía las 400 tareas
     * e imprime los resultados finales y la distribución de trabajo.
     * * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        DoblePeterson lock = new DoblePeterson();
        CounterNaive counter = new CounterNaive();
        
        ExecutorService executor = Executors.newFixedThreadPool(4); 

        for (int i = 0; i < 400; i++) {
            executor.execute(() -> task(lock, counter));
        }

        executor.shutdown();
        try {
            // Esperamos a que terminen todas las tareas
            Thread.sleep(500);
            
            System.out.println("Valor final del contador: " + counter.getValue() + " de 400");
            System.out.println("\n--- Distribución de tareas ---");
            
            for (int i = 0; i < 4; i++) {
                System.out.println("Hilo " + i + " ejecutó: " + tareasPorHilo[i] + " tareas");
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
