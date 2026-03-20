package P3;
/**
 * Implementación del algoritmo de exclusión mutua Double Peterson para 4 hilos.
 * Utiliza una estructura de árbol con tres candados Peterson:
 * dos en la base y uno en la raíz. 
 * Para evitar problemas de visibilidad con el Modelo de Memoria de Java
 * respecto al arreglo de flags, se implementan como variables booleanas individuales.
 * Cada hilo tiene un ID lógico único (0 a 3) asignado mediante ThreadLocal para garantizar 
 * la correcta identificación en los candados.
 * * @author Andres Rodrigo Cataneo Tortolero
 * @version 1.0
 */

public class DoblePeterson {

    // Candado Base 1 
    private volatile boolean flag0_0 = false; // Bandera del hilo 0
    private volatile boolean flag0_1 = false; // Bandera del hilo 1
    private volatile int victim0;           

    // Candado Base 2 
    private volatile boolean flag1_0 = false; // Bandera del hilo 2
    private volatile boolean flag1_1 = false; // Bandera del hilo 3
    private volatile int victim1;            

    // Candado Final  
    private volatile boolean flag2_0 = false; // Bandera del ganador de la base 0
    private volatile boolean flag2_1 = false; // Bandera del ganador de la base 1
    private volatile int victim2;          

    // Asignamos un ID  único del 0 al 3 a cada hilo 
    private static int idCounter = 0;
    /**
    * Asignador de identificadores lógicos (0 al 3) vinculados permanentemente
    * a cada hilo físico del sistema para evitar colisiones de ID durante
    * la ejecución de múltiples tareas en un ExecutorService.
    */
    public static final ThreadLocal<Integer> threadId = ThreadLocal.withInitial(() -> {
        synchronized (DoblePeterson.class) {
            return idCounter++;
        }
    });

    /**
    * Solicita acceso a la sección crítica. 
    * El hilo compite primero en su candado base correspondiente y 
    * si gana, avanza para competir por el candado final.
    */
    public void lock() {
        int id = threadId.get(); 

        // Competencia en el candado base correspondiente
        if (id == 0) {
            flag0_0 = true;
            victim0 = 0;
            while (flag0_1 && victim0 == 0) {} 
        } 
        else if (id == 1) {
            flag0_1 = true;
            victim0 = 1;
            while (flag0_0 && victim0 == 1) {} 
        } 
        else if (id == 2) {
            flag1_0 = true;
            victim1 = 0;
            while (flag1_1 && victim1 == 0) {} 
        } 
        else if (id == 3) {
            flag1_1 = true;
            victim1 = 1;
            while (flag1_0 && victim1 == 1) {} 
        }

        // Competencia en el candado final 
        if (id == 0 || id == 1) { 
            flag2_0 = true;
            victim2 = 0;
            while (flag2_1 && victim2 == 0) {} 
        } 
        else if (id == 2 || id == 3) { 
            flag2_1 = true;
            victim2 = 1;
            while (flag2_0 && victim2 == 1) {} // Espera activa
        }
    }
    /**
    * Libera el acceso a la sección crítica.
    * La liberación se realiza en orden estricto de arriba hacia abajo:
    * primero se baja la bandera del candado final y luego la del candado base.
    */
    public void unlock() {
        int id = threadId.get();

        // Candado final
        if (id == 0 || id == 1) {
            flag2_0 = false;
        } else if (id == 2 || id == 3) {
            flag2_1 = false;
        }
        
        // Candado Base 0 o Base 1
        if (id == 0 || id == 1) { 
            if (id == 0) {
                flag0_0 = false;
            } else {
                flag0_1 = false;
            }
        } else if (id == 2 || id == 3) { 
            if (id == 2) {
                flag1_0 = false;
            } else {
                flag1_1 = false;
            }
        }
    }
}