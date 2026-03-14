package P3;

/**
 * Clase que representa un contador simple sin mecanismos de sincronización.
 * * @author Andres Rodrigo Cataneo Tortolero
 * @version 1.0
 */
public class CounterNaive {
    private int value = 0;
    
    /**
     * Incrementa el valor del contador en uno. 
     */
    public void increment() {
        value++;
    }
    
    /**
     * Obtiene el valor actual del contador.
     * * @return value (int) - El valor entero actual.
     */
    public int getValue() {
        return value;
    }
}
