package P3;

import java.util.Collections;
import java.util.concurrent.locks.Lock;

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

    private boolean evaluaTupla( int labelK , int k , int labelI , int i ){
        if( label[k] == label[i] ){
            if( k > i ){
                return false;
            }
        } else if( label[k] > label[i] ){
            return false;
        }
        return true;
    }

    public void lock(){

        int i = (int)Thread.currentThread().getId()-35;
        System.out.println("Este hilo tiene la ID "+i);

        flag[i] = true;
        // doorway = Maximo + 1
        label[i] = maximo(label)+1;
        for (int k=0; k<label.length; k++) {
            System.out.println("El hilo a comparar tiene la ID "+k);
            while( k != i &&  flag[k] && evaluaTupla( label[k] , k , label[i] , i) ){ /* No hace Nada */ }
        }
    }

    public static int increment(){
        return contador++;
    }

    public static int getContador() {
        return contador;
    }

    public void unlock(){
        flag[(int)Thread.currentThread().getId()-35] = false;
    }
}