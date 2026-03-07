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
 * Clase diseñada para implementar una cola de manera concurrente sin utilizar Lock
 * y tampoco Syncronized.
 * 
 */
public class ColaConcurrente1 extends ColaSecuencial {

    /**
     * {@link List}a de {@link Future}s que seran usados para esperar el fin de la ejecucion concurrente
     * para reportar sus resultados de tipo {@link String}
     */
    private List<Future<String>> futures;
    /** @return {@link List}a que espera resultados de los hilos en ejecucion */
    public List<Future<String>> getFutures() {
        return futures;
    }


    /**
     * {@link @ExecutorService} pool de hilos previamente apartados para su uso concurrente. */
    private ExecutorService threadPool;
    /** @return {@link ExecutorService} los hilos en espera de ejecucion */
    public ExecutorService getPool() {
        return threadPool;
    }


    /** Constructor por defecto, crea 4 hilos en espera */
    public ColaConcurrente1(){
        this( 4 );
    }
    /** 
     * Constructor con argumento {@code int} correspondiente a la cantidad de hilos
     * Se asegura que tenga cuanto menos un hilo asignado
     */
    public ColaConcurrente1( int threads ){
        this.futures = new ArrayList<>();
        this.threadPool = (threads<2)?
            Executors.newSingleThreadExecutor() :
            Executors.newFixedThreadPool(threads) ;
    }

    public static void main(String[] args){

        ColaConcurrente1 cola = new ColaConcurrente1();
    
        //Realizaremos un bucle for para anexar algunos items a la cola.
        for (int i = 0; i <= 1000; i++){
            String val=Integer.toString(i);
            //en esta parte los hilos intenetaran encolar al mismo tiempo.
            cola.getPool().submit(()->cola.enq(val));
        }

        try{
            Thread.sleep(1000);// Delay para esperar que todas las tareas terminen
        }catch(InterruptedException e) {
            System.out.println(e);
        }

        //Realizaremos un bucle for para eliminar algunos items a la cola.
        for (int i = 0; i <= 1000; i++){
            cola.getFutures().add(cola.getPool().submit(()->cola.deq()));    
        }

        //Hacemos shutdown a la pool pues ya no la ocuparemos.
        cola.getPool().shutdown();

        //Utilizaremos Future para saber si se hicieron varios deq al mismo elemento o se hicieron a una cola vacia.
        try{	
            //Utiliaremos un bucle para recorrer la lista de futuros		
            for (int i = 0; i < cola.getFutures().size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
                while(!cola.getFutures().get(i).isDone());
                //Obtenemos el elemento que 'elimino' de la cola.
                String result = cola.getFutures().get(i).get();
                System.out.println(" Result: "+result);
        }
    }catch(InterruptedException e) {
            System.out.println(e);
        }catch(ExecutionException e){
            System.out.println(e);
        }
        
        try{
            Thread.sleep(1000);// Delay para esperar que todas las tareas terminen
        }catch(InterruptedException e) {
            System.out.println(e);
        }
    }
}