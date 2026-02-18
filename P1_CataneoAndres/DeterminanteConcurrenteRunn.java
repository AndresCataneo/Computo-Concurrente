package P1_CataneoAndres;
/**
 * Programa que implementa el cacluclo de determinante de una matriz de manera
 * concurrente utilizando la interfaz Runnable
 * 
 * @author: Andres Rodrigo Cataneo Tortolero
 * @version: 1.0
 */
public class DeterminanteConcurrenteRunn implements Runnable {
    static int matriz_prueba[][] = { { 1, 2, 2 }, { 1, 0, -2 }, { 3, -1, 1 }};
    int num1, num2, num3, partial;

    /**
     * Constructor para la clase DeterminanteConcurrenteRunn, que recibe tres números enteros como parámetros.
     * Estas variables representan una diagonal de la matriz de 3x3.
     * @param - num1 Primer número entero, correspondiente a un elemento de la matriz.
     * @param - num2 Segundo número entero, correspondiente a un elemento de la matriz.
     * @param - num3 Tercer número entero, correspondiente a un elemento de la matriz.
     */
    public DeterminanteConcurrenteRunn(int num1, int num2, int num3) {
        this.num1 = num1;
        this.num2 = num2;
        this.num3 = num3;
    }

    /**
     * Método estático que calcula el determinante de una matriz de 3x3 utilizando hilos para realizar los cálculos de las diagonales de manera concurrente.
     * @param matriz - Matriz de 3x3 de la cual se desea calcular el determinante.
     * @return result - El valor entero del determinante calculado a partir de la matriz dada.  
     */
    public static int determinanteMatriz3x3(int matriz[][]) {
        DeterminanteConcurrenteRunn thr1 = new DeterminanteConcurrenteRunn(matriz[0][0], matriz[1][1], matriz[2][2]);
        DeterminanteConcurrenteRunn thr2 = new DeterminanteConcurrenteRunn(matriz[1][0], matriz[2][1], matriz[0][2]);
        DeterminanteConcurrenteRunn thr3 = new DeterminanteConcurrenteRunn(matriz[2][0], matriz[0][1], matriz[1][2]);
        DeterminanteConcurrenteRunn thr4 = new DeterminanteConcurrenteRunn(matriz[2][0], matriz[1][1], matriz[0][2]);
        DeterminanteConcurrenteRunn thr5 = new DeterminanteConcurrenteRunn(matriz[1][0], matriz[0][1], matriz[2][2]);
        DeterminanteConcurrenteRunn thr6 = new DeterminanteConcurrenteRunn(matriz[0][0], matriz[2][1], matriz[1][2]);

        Thread t1 = new Thread(thr1);
        Thread t2 = new Thread(thr2);
        Thread t3 = new Thread(thr3);
        Thread t4 = new Thread(thr4);
        Thread t5 = new Thread(thr5);
        Thread t6 = new Thread(thr6);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            t6.join();
        } catch (InterruptedException e) {}
        int result = thr1.partial + thr2.partial + thr3.partial - thr4.partial - thr5.partial - thr6.partial;
        return result;
    }

    /**
     * Método run que se ejecuta cuando el hilo es iniciado donde se calcula el producto de los tres números enteros (num1, num2, num3).
     */
    @Override
    public void run() {
        this.partial = this.num1 * this.num2 * this.num3;
    }


    public static void main(String[] args) {
        
        long startTime = System.nanoTime();
        int determinante = determinanteMatriz3x3(matriz_prueba);
        long endTime = System.nanoTime();
        System.out.println("Program took " +
                (endTime - startTime) + "ns, result: " + determinante) ;
    }
}
