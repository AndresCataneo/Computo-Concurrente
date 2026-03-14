package P3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import P3.BackeryLock;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException  {
        
        BackeryLock lock = new BackeryLock();
        List<Future<Integer>> futuros = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int i = 0; i <400; i++) {
            futuros.add( pool.submit( () -> {
                lock.lock();
                final int temp = BackeryLock.increment();
                lock.unlock();
                return temp;
            }) );
        }

        pool.shutdown();

        try{
            // Utiliaremos un bucle para recorrer la lista de futuros		
            for (int i = 0; i < futuros.size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
                while( !futuros.get(i).isDone()){
                }
            }
        }catch(Exception e){
            System.out.print( e );
        }

        System.out.println("El contador llega a: "+ BackeryLock.getContador() );
        int[] tareas = lock.getConteoTareas();
        int temp = 0;
        System.out.println("Los hilos han ejectuado X tareas individualmente");
        for (int i = 0; i<tareas.length ; i++) {
            temp += tareas[i];
            System.out.println("\tHilo "+i+": "+tareas[i]+" tareas");
        }
        System.out.println("Que en total son "+temp+" tareas.");
    }
}