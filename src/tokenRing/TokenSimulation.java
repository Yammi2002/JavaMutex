package tokenRing;

import common.AbstractNode;
import java.util.ArrayList;
import java.util.List;

public class TokenSimulation {
    public static void main(String[] args) {
        List<AbstractNode> network = new ArrayList<>();
        int numNodes = 4; 

        for (int i = 0; i < numNodes; i++) {
            network.add(new TokenRingNode(i, network));
        }

        ((TokenRingNode) network.get(0)).setInitialToken(true);

        System.out.println("--- Inizio Simulazione Token Ring ---");
        System.out.println("L'anello viene avviato assegnando il token al processo 0 [cite: 109, 111]");

        for (AbstractNode n : network) {
            n.start();
        }
    }
}