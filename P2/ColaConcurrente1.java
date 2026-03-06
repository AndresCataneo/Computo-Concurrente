package P2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * Clase ColaConcurrente
 * 
 * Clase diseñada para resolver una cola de manera concurrente sin utilizar candados 
 * y tampoco Syncronized.
 * 
 */
public class ColaConcurrente1 {

    public static void main(String[] args)  throws InterruptedException, ExecutionException{
        
        //Declaramos la cola que utilizaran los hilos.
        ColaSecuencial cola= new ColaSecuencial();

        //Lista para poder guardar los resultados de los submit.
        List<Future<String>> futures = new ArrayList<Future<String>>();

        //Necesitamos declarar la pool de hilos (en este caso sera de 4 hilos).
        ExecutorService pool = Executors.newFixedThreadPool(4);

        //Realizaremos un bucle for para anexar algunos items a la cola.
        for (int i = 0; i <= 20; i++){
            String val=Integer.toString(i);
            //en esta parte los hilos intenetaran encolar al mismo tiempo.
            pool.submit(()->cola.enq(val));
            //En esta parte los hilos van a desencolar si el elemento ingresado es par.
            if (i%2==0){
                futures.add(pool.submit(()->cola.deq()));    
            }            
        }

        try{
			Thread.sleep(1800);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}

        //Mostramos en terminal como quedo la cola despues de enq 
        cola.print();

        //Hacemos shutdown a la pool pues ya no la ocuparemos.
        pool.shutdown();

        //Utilizaremos Future para saber si se hicieron varios deq al mismo elemento.
        try{	
            System.out.println("Prints para verificar si borro 2 veces o mas un elemento");
            //Utiliaremos un buvle para recorrer la lista de futuros		
			for (int i = 0; i < futures.size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
	            while(!futures.get(i).isDone());
                //Obtenemos el elemento que 'elimino' de la cola.
	            String result = futures.get(i).get();
	            System.out.printf("\n Result: "+result);
        }
		}catch(InterruptedException e) {
			System.out.println(e);
		}
        
        try{
			Thread.sleep(1800);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}

       
    }    
}
