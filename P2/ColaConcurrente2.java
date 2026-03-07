package P2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Clase ColaConcurrente
 * 
 * Clase diseñada para resolver una cola de manera concurrente sin utilizar candados 
 * y tampoco Syncronized.
 * 
 */
public class ColaConcurrente2 {

    public static void main(String[] args)  throws InterruptedException, ExecutionException{
        
        //Declaramos la cola que utilizaran los hilos.
        ColaSecuencial cola= new ColaSecuencial();
     
        //Declaramos nuestro candado.
        Lock lock = new ReentrantLock();

        //Lista para poder guardar los resultados de los submit.
        List<Future<String>> futures = new ArrayList<Future<String>>();

        //Necesitamos declarar la pool de hilos (en este caso sera de 4 hilos).
        ExecutorService pool = Executors.newFixedThreadPool(4);

        //Realizaremos un bucle for para anexar algunos items a la cola.
        for (int i = 0; i <= 1000; i++){
            
                String val=Integer.toString(i);
                //en esta parte los hilos intenetaran encolar al mismo tiempo.
                pool.submit(()->{ lock.lock();
                    try{cola.enq(val);
                        
                    }finally{
                        lock.unlock();
                    } });
        }

        try{
			Thread.sleep(1000);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}

        //Eliminamos los 1000 elementos
        for (int i = 0; i <= 1000; i++){

               Future<String> future = pool.submit(()->{ lock.lock();
                    try{return cola.deq();
                        
                    }finally{
                        lock.unlock();
                    } });
                futures.add(future);
        }

        //Hacemos shutdown a la pool pues ya no la ocuparemos.
        pool.shutdown();

        //Utilizaremos Future para saber si se hicieron varios deq al mismo elemento.
        try{
            //Utiliaremos un bucle para recorrer la lista de futuros		
			for (int i = 0; i < futures.size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
	            while(!futures.get(i).isDone());
                //Obtenemos el elemento que 'elimino' de la cola.
	            String result = futures.get(i).get();
	            System.out.println("Result: "+result);
            }
		}catch(InterruptedException e) {
			System.out.println(e);
		}
        
        try{
			Thread.sleep(1000);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}
       
    }    
}
