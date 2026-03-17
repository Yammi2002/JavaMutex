package decentralized;

import common.*;
import java.util.List;

/**
 * Classe che rappresenta i nodi coordinatori nell'algoritmo di mutua esclusione decentralizzato
 */
public class DecentralizedCoordinator extends AbstractNode {
    private boolean isBusy = false;

    /**
     * Costruttore della classe
     * @param id, identificativo del nodo
     * @param network, la rete con i nodi della rete
     */
    public DecentralizedCoordinator(int id, List<AbstractNode> network) {
        super(id, network);
    }

    @Override
    public synchronized void receive(Message msg) {
        handleMessage(msg);
    }

    /**
     * Quando il coordinatore riceve un messaggio, nel caso fosse una richiesta e la risorsa fosse dispobibile, invia al nodo
     * un messaggio di tipo VOTE_YES, altrimenti VOTE_NO. Nel caso il messaggio fosse di rilascio, aggiorna le sue variabili interne
     * indicando che la risorsa ora è disponibile.
     * @param msg
     */
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