package decentralized;

import common.AbstractNode;
import java.util.ArrayList;
import java.util.List;

public class DecentralizedSimulation {
    public static void main(String[] args) {
        List<AbstractNode> network = new ArrayList<>();
        
        int m = 5; 
        int n = 3;

        System.out.println("--- Inizio Simulazione Decentralizzata ---");
        System.out.println("Configurazione: " + m + " Coordinatori e " + n + " Nodi.");
        System.out.println("Maggioranza richiesta: " + ((m / 2) + 1) + " voti.");
        System.out.println("------------------------------------------");

        for (int i = 0; i < m; i++) {
            network.add(new DecentralizedCoordinator(i, network));
        }

        for (int i = 0; i < n; i++) {
            network.add(new DecentralizedNode(m + i, network, m));
        }

        for (AbstractNode node : network) {
            node.start();
        }
    }
}