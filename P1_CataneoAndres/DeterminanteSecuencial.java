package P1_CataneoAndres;

public class DeterminanteSecuencial {
    /**
     * Metodo para calcular el determinante de una matriz 3x3
     * @param matriz Un arreglo bidimensional de numeros enteros.
     * @return un entero que representa el determinante de la matriz.
     * 
     */
    public static int determinante(int [][] matriz){

        //variables auxiliares:
        int diag1,diag2,diag3,diag4,diag5,diag6; 
        
        //multiplicaciones de cada diagonal.
        diag1=matriz[0][0]* matriz[1][1]* matriz[2][2];
        diag2=matriz[1][0]* matriz[2][1]* matriz[0][2];
        diag3=matriz[2][0]* matriz[0][1]* matriz[1][2];

        //multiplicaciones de la diagonal invertida
        diag4=matriz[2][0]* matriz[1][1]* matriz[0][2];
        diag5=matriz[1][0]* matriz[0][1]* matriz[2][2];
        diag6=matriz[0][0]* matriz[2][1]* matriz[1][2];
        
        //determinante
        return diag1+diag2+diag3 - (diag4+diag5+diag6);
    }



    public static void main(String[] args) {

        int matriz_prueba[][] = { { 1, 2, 2 }, { 1, 0, -2 }, { 3, -1, 1 }};
        long startTime = System.nanoTime();
        int determinante_prueba=determinante(matriz_prueba);
        long endTime = System.nanoTime();

        System.out.println("Program took " +
                (endTime - startTime) + "ns, result: " + determinante_prueba) ;

    }

}
