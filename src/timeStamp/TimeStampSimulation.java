package timeStamp;

import common.AbstractNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che simula un esecuzione dell'algoritmo di mutua esclusione basato su timestamps
 */
public class TimeStampSimulation {
    public static void main() {
        List<AbstractNode> network = new ArrayList<>();
        int numNodes = 3; 

        for (int i = 0; i < numNodes; i++) {
            network.add(new TimeStampNode(i, network));
        }

        System.out.println("--- Inizio Simulazione Distribuita ---");
        System.out.println("I processi useranno i timestamp per decidere la precedenza.");

        for (AbstractNode n : network) {
            n.start();
        }
    }
}