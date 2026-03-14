package P3;

import java.util.Collections;

public class BackeryLock{

    private int tareasEjecutadas = 0;
    private static int contador = 0;

    /** Arreglo para establecer solicitud de turno */
    volatile boolean[] flag = { false , false , false , false };

    /** Arreglo para indicar numero de turno asignado */
    volatile int[] label = { 0 , 0 , 0 , 0 };

    public static int maximo( int[] array ){
        int res = Integer.MIN_VALUE;
        for (int i=0; i<array.length; i++) {
            if( res < array[i] ){
                res = array[i];
            }
        }
        return res;
    }

    /**
     * Necesitamos que cada hilo tenga un id unico, para ello declararemos
     * una variable local para cada hilo. 
    */
    private static int idCounter = 0;
    public static final ThreadLocal<Integer> threadId = ThreadLocal.withInitial(() -> {
        synchronized ( BackeryLock.class ) { //Magia
            return idCounter++;
        }
    });

    /**
     * Metodo auxiliar con que 
     * @param k
     * @param i
     * @return
     */
    private boolean evaluaTupla( int k , int i ){
        if( label[k] == label[i] ){
            if( i < k ){
                return false;
            }
        } else if( label[i] < label[k] ){
            return false;
        }
        return true;
    }

    public void lock(){
        System.out.println("BEGIN LOCK");
        int i = threadId.get();
        System.out.println("Este hilo tiene la ID "+i);

        flag[i] = true; // Este hilo pide permiso de pasar
        
        label[i] = maximo(label)+1; // Este hilo toma boleto (max+1)
        for (int k=0; k<label.length; k++) { // Este hilo se compara al resto
            System.out.println("El hilo con que "+i+" se compara tiene ID "+k);
            while( k != i &&  flag[k] && evaluaTupla( k , i ) ){ /* No hace Nada */ }
        }
    }

    public static int increment(){
        return contador++;
    }

    public static int getContador() {
        return contador;
    }

    public void unlock(){
        System.out.println("UNLOCK");
        flag[threadId.get()] = false;
    }
}