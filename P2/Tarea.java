package practica02;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

/**
 * Clase que representa una tarea a ejecutarse de manera concurrente.
 * Implementa la interfaz Runnable y utiliza un Semáforo y un Candado (Lock)
 * para gestionar el acceso a un servidor con capacidad limitada y aplicar
 * restricciones de exclusión mutua para ciertas tareas.
 * @author: Andres Rodrigo Cataneo Tortolero
 * @version: 1.0
 */
public class Tarea implements Runnable {
    int tiempoTarea;
    int task;
    final Semaphore smphre;
    Lock lock;

    /**
     * Constructor para la clase Tarea.
     * @param i Identificador numérico de la tarea (se usa para determinar el tipo de hilo).
     * @param smphre Semáforo compartido para limitar el acceso de hilos al servidor.
     * @param lock Candado compartido para garantizar la exclusión mutua entre los hilos 0 y 2.
     */
    public Tarea(int i, Semaphore smphre, Lock lock) {
        this.task = i;
        this.smphre = smphre;
        this.lock = lock;
    }

    /**
     * Método run que se ejecuta cuando el hilo es despachado por el ExecutorService.
     * Gestiona de forma segura la adquisición del candado (para hilos 0 y 2), el ingreso
     * al semáforo (máximo 3 tareas simultáneas) y simula el trabajo en nanosegundos.
     */
    @Override
    public void run() {
        int value = task % 6;
        boolean quieroCandado = (value == 0 || value == 2);

        // Se pide afuera del semáforo para no desperdiciar lugares del servidor
        if (quieroCandado) {
            lock.lock();
        }

        try {
            smphre.acquire();

            try {
                // --- Seccion critica del servidor ---
                System.out.println("Running Thread " + value + " task: " + this.task);

                switch (value) {
                    case 0, 2:
                        this.tiempoTarea = 500;
                        break;
                    case 1:
                        this.tiempoTarea = 2000;
                        break;
                    default:
                        this.tiempoTarea = 3000;
                }

                // Trabajo en nanosegundos
                long inicio = System.nanoTime();
                while (System.nanoTime() - inicio < this.tiempoTarea) {
                    // Busy-wait para simular la ejecución
                }

                System.out.println("Running Thread " + value + " time: " + this.tiempoTarea + "ns");

            } finally {
                // Liberación del semáforo
                smphre.release();
            }

        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            // Liberación del candado si se había pedido por los hilos 0 o 2
            if (quieroCandado) {
                lock.unlock();
            }
        }
    }
}
