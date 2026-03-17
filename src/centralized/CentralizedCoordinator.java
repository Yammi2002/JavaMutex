package centralized;

import common.*;
import java.util.*;

/**
 * Classe che rappresenta un nodo coordinatore nell'algoritmo di mutua esclusione centralizzato
 */
public class CentralizedCoordinator extends AbstractNode {
    private boolean resourceFree = true;
    private final Queue<Integer> requestQueue = new LinkedList<>();

    public CentralizedCoordinator(int id, List<AbstractNode> network) {
        super(id, network);
    }

    /**
     * Metodo che definisce il comportamento del coordinatore in base al messaggio ricevuto.
     * Nel caso si riceva una richiesta, si cotnrolla che la risorsa sia libera, nel caso la si assegna rispondendo al messaggio.
     * Nel caso sia un messaggio di rilascio, si segna la risorsa come disponibile.
     * @param msg, il messaggio ricevuto, di tipo OK o RELEASE.
     */
    @Override
    public synchronized void receive(Message msg) {
        switch (msg.getType()) {
            case REQUEST:
                if (resourceFree) {
                    resourceFree = false;
                    send(msg.getSenderId(), new Message(id, MessageType.OK));
                } else {
                    requestQueue.add(msg.getSenderId());
                }
                break;

            case RELEASE:
                if (!requestQueue.isEmpty()) {
                    int nextId = requestQueue.poll();
                    send(nextId, new Message(id, MessageType.OK));
                } else {
                    resourceFree = true;
                }
                break;
        }
    }

    @Override
    public void run() {
        System.out.println("Coordinatore " + id + " attivo.");
    }
}