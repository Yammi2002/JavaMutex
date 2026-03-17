package common;

import java.util.List;

public abstract class AbstractNode extends Thread {
    public final int id;
    protected final List<AbstractNode> network; 
    
    public AbstractNode(int id, List<AbstractNode> network) {
        this.id = id;
        this.network = network;
    }

    protected void send(int receiverId, Message msg) {
        network.get(receiverId).receive(msg);
    }

    public abstract void receive(Message msg);
}