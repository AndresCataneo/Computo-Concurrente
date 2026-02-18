package P1_CataneoAndres;
/**
 * Programa que implementa el cacluclo de determinante de una matriz de manera concurrente utilizando 2 hilos, para comparar con el programa secuencial.
 * @author: Andres Rodrigo Cataneo Tortolero
 * @version: 1.0
 */
public class DeterminanteConcurrente2Hilos extends Thread{
    static int determinante;
    static int matriz_prueba[][] = { { 1, 2, 2 }, { 1, 0, -2 }, { 3, -1, 1 }};
    int [] diag1;
    int [] diag2;
    int [] diag3;
    int [] partial = new int[3];
    
    /**
     * Constructor para la clase DeterminanteConcurrente2Hilos, que recibe tres arreglos de enteros como parámetros.
     * Cada arreglo representa una diagonal de la matriz de 3x3.
     * @param diag1
     * @param diag2
     * @param diag3
     */
    public DeterminanteConcurrente2Hilos(int [] diag1, int [] diag2, int [] diag3) {
        this.diag1 = diag1;
        this.diag2 = diag2;
        this.diag3 = diag3;
    }
    
    /**
     * Método estático que calcula el determinante de una matriz de 3x3 utilizando hilos para realizar los cálculos de las diagonales de manera concurrente.
     * @param matriz - Matriz de 3x3 de la cual se desea calcular el determinante.
     * @return result - El valor entero del determinante calculado a partir de la matriz dada.
     */
    public static int determinanteMatriz3x3(int matriz[][]) {

        int [] diagonal1Ab = {matriz[0][0], matriz[1][1], matriz[2][2]};    
        int [] diagonal2Ab = {matriz[1][0], matriz[2][1], matriz[0][2]};
        int [] diagonal3Ab = {matriz[2][0], matriz[0][1], matriz[1][2]};

        int [] diagonal1Arr = {matriz[2][0], matriz[1][1], matriz[0][2]};
        int [] diagonal2Arr = {matriz[1][0], matriz[0][1], matriz[2][2]};
        int [] diagonal3Arr = {matriz[0][0], matriz[2][1], matriz[1][2]};
        
        //Tiene las diagonales hacia abajo
        DeterminanteConcurrente2Hilos thr1 = new DeterminanteConcurrente2Hilos(diagonal1Ab, diagonal2Ab, diagonal3Ab);
        
        //Tiene las diagonales hacia arriba
        DeterminanteConcurrente2Hilos thr2 = new DeterminanteConcurrente2Hilos(diagonal1Arr, diagonal2Arr, diagonal3Arr);
        thr1.start();
        thr2.start();

        try{
            thr1.join();
            thr2.join();
        }catch(InterruptedException e) {}
        int result = thr1.partial[0] + thr1.partial[1] + thr1.partial[2] - thr2.partial[0] - thr2.partial[1] - thr2.partial[2];
        return result;
    }

    /**
     * Método run que se ejecuta cuando se inicia el hilo donde calcula los productos de las diagonales.
     */
    @Override
    public void run(){
        this.partial[0] = this.diag1[0] * this.diag1[1] * this.diag1[2];
    
        this.partial[1] = this.diag2[0] * this.diag2[1] * this.diag2[2];
        
        this.partial[2] = this.diag3[0] * this.diag3[1] * this.diag3[2];
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        determinante = determinanteMatriz3x3(matriz_prueba);
        long endTime = System.nanoTime();
        System.out.println("Program took " +
                (endTime - startTime) + "ns, result: " + determinante) ;
    }

}
