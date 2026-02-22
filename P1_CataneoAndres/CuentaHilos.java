package P1_CataneoAndres;

public class CuentaHilos {
    public static void main(String[] args) {
        int hilos = Runtime.getRuntime().availableProcessors();
        System.out.println("Hilos disponibles: "+hilos+" hilos");
    }
}
