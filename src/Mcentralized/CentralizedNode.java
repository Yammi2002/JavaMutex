package Mcentralized;

import common.*;

import java.util.List;
import java.util.concurrent.Semaphore;

public class CentralizedNode extends AbstractNode implements MutualExclusionAlgorithm {
    private final int coordinatorId;
    private final Semaphore waitOk = new Semaphore(0);

    public CentralizedNode(int id, List<AbstractNode> network, int coordinatorId) {
        super(id, network);
        this.coordinatorId = coordinatorId;
    }

    @Override
    public void lock() {
        System.out.println("Nodo " + id + " richiede accesso al coordinatore " + coordinatorId);
        send(coordinatorId, new Message(id, MessageType.REQUEST));
        try {
            waitOk.acquire(); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void unlock() {
        System.out.println("Nodo " + id + " rilascia la risorsa.");
        send(coordinatorId, new Message(id, MessageType.RELEASE));
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.getType() == MessageType.OK) {
            System.out.println("Nodo " + id + " ha ricevuto l'OK!");
            waitOk.release(); 
        }
    }

    @Override
    public void receive(Message msg) {
        handleMessage(msg);
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(1000); 
            
            for (int i = 0; i < 2; i++) {
                System.out.println("[LOG] Nodo " + id + " sta facendo calcoli locali...");
                Thread.sleep((long) (Math.random() * 2000));
                
                this.lock(); 
                
                System.out.println(">>> [SC] Nodo " + id + " è dentro la Sezione Critica!");
                Thread.sleep(3000); 
                
                this.unlock(); 
                System.out.println("<<< [OUT] Nodo " + id + " è uscito.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
