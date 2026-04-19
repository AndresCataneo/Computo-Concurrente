package P5;

import java.util.concurrent.atomic.AtomicInteger ;
import java.util.concurrent.atomic.AtomicLong ;

/**
 * Implementación modificada de {@code ShavitTreeCounter} diseñada para ser capaz
 * de realizar lecturas linealizables y concurrentes del valor de contador (que mantenga
 * la consistencia, aún con la existencia de operaciones de escritura simultaneas ); en vez
 * de depender de la Consistencia Quiesciente (sin operaciones de escritura simultaneas)
 * 
 * @author Balderas Aguilar Uriel
 * @author Hernandez Rosas Luis Ernesto
 * 
 * @version 1.0
 */
public class ShavitTreeCounter {

    /**
     * Un {@link Nodo} será ya sea interno (balanceador), o hoja (contador).
     * Son inicializados por defecto con {@code balancer 0} y {@code count 0}
     */
    static class Node {
        boolean isLeaf;
        Node left , right;
        AtomicInteger balancer = new AtomicInteger(0); // Prisma de difraccion
        AtomicLong count = new AtomicLong(0); // Contador real

        Node(){ this . isLeaf = true; }
        Node( Node left , Node right ){
            this.isLeaf = false ;
            this.left = left ;
            this.right = right ;
        }
    }

    private final Node root;

    public ShavitTreeCounter( int depth ) {
        this.root = buildTree( depth );
    }

    private Node buildTree( int depth ) {
        if ( depth == 0 ) return new Node();
        return new Node( buildTree( depth - 1) , buildTree( depth - 1) );
    }

    public void increment() {
        Node current = root ;
        while (!current.isLeaf ) {
            int route = current.balancer.getAndIncrement() ;
            current = ( route % 2 == 0) ? current.left : current.right ;
        }
        current.count.incrementAndGet() ;
    }

    public long oldFetch(){
        return sumLeaves( root ); // CODIGO ORIGINAL
    }

    public long fetch() {
        // return sumLeaves( root ); // CODIGO ORIGINAL

        int index = 0;
        long[] doubleCollect = new long[2];
        doubleCollect[0] = sumLeaves(root);
        System.out.println("Primer Collect:"+doubleCollect[0]);
        boolean cambia = true;
        while( cambia ){
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
            
            
            // Intercala el indice de celda a modificar
            index = (index+1)%2;

            /*
            Se busca llenar el segundo collect en la primera iteracion.
            En el resto de iteraciones hay que sobreescribir el collect "viejo"
            */
            doubleCollect[index] = sumLeaves(root); 

            System.out.println("Collect mas Reciente: "+ doubleCollect[index]);

            // Si no son iguales, se necesita hacer un nuevo collect
            cambia = doubleCollect[0] != doubleCollect[1];
            System.out.println("Es "+ (cambia?"distinto":"igual") +" al anterior collect" ) ;
        }
        System.out.println("FIN de fetch con Double Collect");
        return doubleCollect[0];
    }

    private long sumLeaves( Node node ) {
        if ( node.isLeaf ) return node.count.get();
        return sumLeaves( node.left ) + sumLeaves( node.right );
    }
}