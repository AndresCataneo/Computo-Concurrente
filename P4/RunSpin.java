package P4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Programa de pruebas (Benchmark) para medir el rendimiento de diversas
 * implementaciones de candados (Spinlocks) bajo condiciones de alta contención.
 * Esta versión simula una "tarea pesada" utilizando operaciones sobre una matriz
 * y llamadas al sistema de Entrada/Salida (I/O) para forzar la espera activa de los hilos.
 * @author Gilde Valeria Rodríguez (Código original)
 * @author Andrés Rodrigo Cataneo Tortolero (Modificaciones)
 * @version 2.0
 */
public class RunSpin {
    
    // 1. matriz compartida 
    static int[][] matriz = new int[10][10];
    static int counter = 0; 
    
    /**
     * Metodo que llena la matriz compartida de 10x10 asignando a cada celda
     * la suma de sus índices más el valor actual del contador global.
     */
    public static void asignacionEnMatriz() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matriz[i][j] = counter + i + j; 
            }
        }
    }

    /**
     * Metodo que recorre e imprime en la consola de manera secuencial todos los elementos 
     * de la matriz compartida, conservando su formato bidimensional.
     */
    public static void imprimirMatriz() {
        System.out.println("--- Matriz Actualizada con nuevo valor ---");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }
    
    /**
     * Metodo que define un candidato para la tarea atómica que ejecutarán los hilos concurrentes.
     * Garantiza el acceso exclusivo a los métodos de asignación e impresión
     * mediante la adquisición y liberación segura del candado proporcionado.
     * * @param lock La implementación específica de Lock que se pondrá a prueba.
     * @return El valor actual del contador global de tareas completadas.
     */
    private static int task(Lock lock) {
        try {
            lock.lock();
            // Sección crítica 
            //asignacionEnMatriz();
            //imprimirMatriz();
            
            counter++; 
        } finally {
            lock.unlock();      
        }
        return counter;
    }

    /**
     * Metodo principal que representa el punto de entrada del programa. Configura el entorno de ejecución, 
     * instancia la Pool de hilos, lanza las tareas concurrentes y mide
     * el tiempo total transcurrido desde el inicio hasta la finalización de todas.
     * @param args Argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
        
        int numberThreads = 4; 
        ExecutorService executor = Executors.newFixedThreadPool(numberThreads);

//        Lock lock = new TASLock();
//     Lock lock = new TTASLock();
      Lock lock = new BackoffLock();
//      Lock lock = new MCSLock();
//      Lock lock = new ALock(numberThreads);
//      Lock lock = new ReentrantLock();
//      Lock lock = new CLHLock();
        
        counter = 0;
        
        long startTime = System.nanoTime();
        
        for(int i = 0; i <1000; i++) {
            futures.add(executor.submit(() -> task(lock))); 
        }
        executor.shutdown();
        
        for (int i = 0; i < futures.size(); i++) {
            while(!futures.get(i).isDone()){}; // Comprobar que todas las tareas terminen
        }
        long endTime = System.nanoTime(); //Finish time
        
        System.out.println("\nProgram took " +
                (endTime - startTime)*0.000001 + "ms, Tareas completadas: " + counter); 
    }
}
