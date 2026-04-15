package P5;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación modificada de un Sloppy Counter (Contador Perezoso) diseñada para explorar
 * los límites entre la Consistencia Eventual y la Consistencia Secuencial.
 * Esta versión asigna memoria privada a cada hilo para maximizar la eficiencia y evitar la contención,
 * pero altera el comportamiento clásico de lectura obligando al hilo invocador 
 * a volcar su estado local al contador global antes de retornar el total.
 * * @author Andrés Rodrigo Cataneo Tortolero
 * @version 1.0
 */
public class SloppyCounterVolcado {
    private final AtomicLong globalCount = new AtomicLong(0);
    private final int threshold = 150;
    private final ThreadLocal<Long> localCount = ThreadLocal.withInitial(() -> 0L);

    /**
     * Metodo que registra un incremento en la memoria local del hilo que invoca el método.
     * Si el valor acumulado en dicha memoria local alcanza o supera el límite establecido (threshold),
     * los datos se suman atómicamente al contador global y el contador local se reinicia a cero.
     */
    public void increment() {
        long current = localCount.get() + 1;
        if (current >= threshold) {
            globalCount.addAndGet(current);
            localCount.set(0L);
        } else {
            localCount.set(current);
        }
    }

    /**
     * Metodo que recupera el valor total aproximado del contador. 
     * A diferencia de la implementación original, esta variante obliga al hilo invocador 
     * a vaciar sus propios incrementos rezagados al contador global antes de realizar la lectura.
     * Debido a la naturaleza de ThreadLocal, esta acción no puede forzar el volcado del estado 
     * de otros hilos, manteniendo la estructura como Eventualmente Consistente.
     * * @return El valor acumulado en el contador global tras el volcado local del hilo invocador.
     */
    public long fetch() {
        long currentLocal = localCount.get();
        if (currentLocal > 0) {
            globalCount.addAndGet(currentLocal);
            localCount.set(0L);
        }
        return globalCount.get();
    }
}
