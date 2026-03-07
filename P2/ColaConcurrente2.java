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
public class ColaConcurrente2 extends ColaSecuencial {

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


    /**
     * {@link Lock} de tipo {@link ReentrantLock} con que se aplica candado bloqueante
     * a la funcionalidad de la {@link ColaConcurrente2}
     */
    private Lock lock;
    /** @return {@link ReentrantLock} el candado que da turnos a los hilos para operar */
    public Lock getLock() {
        return lock;
    }

    /** Constructor por defecto, crea 4 hilos en espera */
    public ColaConcurrente2(){
        this( 4 );
    }
    /** 
     * Constructor con argumento {@code int} correspondiente a la cantidad de hilos
     * Se asegura que tenga cuanto menos un hilo asignado
     */
    public ColaConcurrente2( int threads ){
        this.lock = new ReentrantLock();
        this.futures = new ArrayList<>();
        this.threadPool = (threads<2)?
            Executors.newSingleThreadExecutor() :
            Executors.newFixedThreadPool(threads) ;
    }

    public static void main(String[] args){
        
        //Declaramos la cola que utilizara los hilos.
        ColaConcurrente2 cola = new ColaConcurrente2();
        int espera = 1000;
        int elementos = 1000;

        //Realizaremos un bucle for para anexar algunos items a la cola.
        for (int i = 0; i <= elementos; i++){
            String val=Integer.toString(i);
            //en esta parte los hilos intenetaran encolar al mismo tiempo.
            cola.getPool().submit(()->{
                cola.getLock().lock();
                try{
                    cola.enq(val);
                }finally{
                    cola.getLock().unlock();
                }
            });
        }

        try{
			Thread.sleep(espera);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}

        //Eliminamos los 1000 elementos
        for (int i = 0; i <= elementos; i++){
            Future<String> future = cola.getPool().submit( () -> {
                cola.getLock().lock();
                try{
                    return cola.deq();
                }finally{
                    cola.getLock().unlock();
                }
            });
            cola.getFutures().add(future);
        }

        //Hacemos shutdown a la pool pues ya no la ocuparemos.
        cola.getPool().shutdown();

        //Utilizaremos Future para saber si se hicieron varios deq al mismo elemento.
        try{
            //Utiliaremos un bucle para recorrer la lista de futuros		
			for (int i = 0; i < cola.getFutures().size(); i++) {
                //Si la tarea aun no ha terminado obligaremos a esperar a que termine.
	            while(! cola.getFutures().get(i).isDone());
                //Obtenemos el elemento que 'elimino' de la cola.
	            String result = cola.getFutures().get(i).get();
	            System.out.println("Result: "+result);
            }
		}catch(InterruptedException e) {
			System.out.println(e);
		}catch(ExecutionException e){
            System.out.println(e);
        }
        
        try{
			Thread.sleep(espera);// Delay para esperar que todas las tareas terminen
		}catch(InterruptedException e) {
			System.out.println(e);
		}
    }
}
