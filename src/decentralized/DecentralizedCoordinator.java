package decentralized;

import common.*;
import java.util.List;

public class DecentralizedCoordinator extends AbstractNode {
    private boolean isBusy = false;

    public DecentralizedCoordinator(int id, List<AbstractNode> network) {
        super(id, network);
    }

    @Override
    public synchronized void receive(Message msg) {
        handleMessage(msg);
    }

    public void handleMessage(Message msg) {
        switch (msg.getType()) {
            case REQUEST:
                if (!isBusy) {
                    isBusy = true;
                    System.out.println("COORDINATORE " + id + ": Voto SÌ per il Nodo " + msg.getSenderId());
                    send(msg.getSenderId(), new Message(id, MessageType.VOTE_YES));
                } else {
                    System.out.println("COORDINATORE " + id + ": Voto NO per il Nodo " + msg.getSenderId());
                    send(msg.getSenderId(), new Message(id, MessageType.VOTE_NO));
                }
                break;

            case RELEASE:
                System.out.println("COORDINATORE " + id + ": Risorsa rilasciata dal Nodo " + msg.getSenderId());
                isBusy = false;
                break;
                
            default:
                break;
        }
    }

    @Override
    public void run() {
        System.out.println("Coordinatore Replica " + id + " attivo.");
    }
}