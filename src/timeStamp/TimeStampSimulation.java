package timeStamp;

import common.AbstractNode;
import java.util.ArrayList;
import java.util.List;

public class TimeStampSimulation {
    public static void main(String[] args) {
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