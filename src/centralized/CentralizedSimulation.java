package centralized;

import common.AbstractNode;
import java.util.ArrayList;
import java.util.List;

public class CentralizedSimulation {
    public static void main(String[] args) {
        // La nostra "rete" virtuale
        List<AbstractNode> network = new ArrayList<>();
        
        int numNodes = 3; 
        int coordinatorId = 0; 

        CentralizedCoordinator coordinator = new CentralizedCoordinator(coordinatorId, network);
        network.add(coordinator);

        for (int i = 1; i <= numNodes; i++) {
            CentralizedNode node = new CentralizedNode(i, network, coordinatorId);
            network.add(node);
        }

        System.out.println("--- Inizio Simulazione Centralizzata ---");
        for (AbstractNode n : network) {
            n.start();
        }
    }
}
