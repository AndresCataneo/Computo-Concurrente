package P2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Programa principal (Scheduler) que orquesta la ejecución concurrente de múltiples tareas
 * utilizando un Pool de hilos (ExecutorService). Inicializa y comparte las herramientas
 * de sincronización globales para controlar el acceso al servidor.
 * * @author: Andres Rodrigo Cataneo Tortolero
 * @version: 1.0
 */
public class Scheduler {
    
    // Solo tres a la vez (con justicia para garantizar FIFO)
    static Semaphore smphre = new Semaphore(3, true);
    
    // Candado para exclusión mutua entre hilo 0 y 2 (con justicia)
    static Lock candadoExclusivo = new ReentrantLock(true);

    /**
     * Método principal que inicializa un pool de 6 hilos y envía múltiples
     * instancias de Tarea para ser procesadas de manera concurrente.
     * * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args){
                
        ExecutorService executorTarea = Executors.newFixedThreadPool(6);

        for(int i = 0; i < 26; i++) {
            // Se usa el semáforo y el candado compartidos en cada tarea
            executorTarea.execute(new Tarea(i, smphre, candadoExclusivo));
        }

        // Apagado ordenado del pool una vez enviadas todas las tareas
        executorTarea.shutdown();
    }
}
