package Mcentralized;

import common.*;
import java.util.*;

public class CentralizedCoordinator extends AbstractNode {
    private boolean resourceFree = true;
    private final Queue<Integer> requestQueue = new LinkedList<>();

    public CentralizedCoordinator(int id, List<AbstractNode> network) {
        super(id, network);
    }

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