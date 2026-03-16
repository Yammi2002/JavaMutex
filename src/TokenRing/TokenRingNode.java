package TokenRing;

import java.util.List;
import java.util.concurrent.Semaphore;
import common.AbstractNode;
import common.Message;
import common.MessageType;
import common.MutualExclusionAlgorithm;

public class TokenRingNode extends AbstractNode implements MutualExclusionAlgorithm {

    private boolean wantsToEnter = false;
    private boolean hasToken = false;
    private final Semaphore waitToken = new Semaphore(0);
    
    public TokenRingNode(int id, List<AbstractNode> network) {
        super(id, network);
    }
    
    public void setInitialToken(boolean hasIt) {
        this.hasToken = hasIt;
    }

    private int getNextNodeId() {
        return (this.id + 1) % network.size();
    }

    @Override
    public void lock() {
        this.wantsToEnter = true;
        System.out.println("Nodo " + id + " in attesa del token...");
        
        if (!this.hasToken) {
            try {
                this.waitToken.acquire(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlock() {
        this.wantsToEnter = false;
        this.hasToken = false;
        int next = getNextNodeId();
        System.out.println("Nodo " + id + " passa il token al nodo " + next); 
        send(next, new Message(id, MessageType.TOKEN));
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.getType() == MessageType.TOKEN) {
            this.hasToken = true;
            
            if (this.wantsToEnter) {
                waitToken.release();
            } else {
                new Thread(() -> {
                    try {
                        Thread.sleep(10); 
                        this.unlock();
                    } catch (InterruptedException e) {}
                }).start();
            }
        }
    }

    @Override
    public void receive(Message msg) {
        handleMessage(msg);
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(2000); 
            
            if (id == 0 && hasToken && !wantsToEnter) {
                System.out.println("Nodo 0 avvia la circolazione iniziale.");
                unlock(); 
            }

            while (true) {
                Thread.sleep((long) (Math.random() * 5000)); 
                
                lock();
                System.out.println(">>> [SC] Nodo " + id + " è dentro la Sezione Critica!");
                Thread.sleep(2000); 
                unlock();
                System.out.println("<<< [OUT] Nodo " + id + " è uscito.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}