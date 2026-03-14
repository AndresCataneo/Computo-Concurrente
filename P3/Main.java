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
            //Utiliaremos un bucle para recorrer la lista de futuros		
			for (int i = 0; i < futuros.size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
	            while(! futuros.get(i).isDone());
                //Obtenemos el elemento que 'elimino' de la cola.
	            Integer result = futuros.get(i).get();
	            System.out.println("Result: "+result.toString());
            }
		}catch(InterruptedException e) {
			System.out.println(e);
		}catch(ExecutionException e){
            System.out.println(e);
        }

        System.out.println("El contador llega a: "+ BackeryLock.getContador() );
    }
}
