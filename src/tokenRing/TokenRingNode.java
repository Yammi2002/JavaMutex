package tokenRing;

import java.util.List;
import java.util.concurrent.Semaphore;
import common.AbstractNode;
import common.Message;
import common.MessageType;
import common.MutualExclusionAlgorithm;

/**
 * Classe che rappresenta i nodi nell'algoritmo di mutua esclusione basato su token ring
 */
public class TokenRingNode extends AbstractNode implements MutualExclusionAlgorithm {

    private boolean wantsToEnter = false;
    private boolean hasToken = false;
    private final Semaphore waitToken = new Semaphore(0);
    
    /**
     * Costruttore della classe
     * @param id, identificativo del nodo
     * @param network, rete dei nodi
     */
    public TokenRingNode(int id, List<AbstractNode> network) {
        super(id, network);
    }
    
    /*
     * Metodo per settare il token inizialmente.
     * @param hasIt, booleano per determinare se il nodo possiede il token.
     */
    public void setInitialToken(boolean hasIt) {
        this.hasToken = hasIt;
    }

    /*
     * Metodo per determinare l'id del nodo successivo nella catena.
     */
    private int getNextNodeId() {
        return (this.id + 1) % network.size();
    }

    /*
     * Quando un nodo vuole entrare nella sezione critica aggiorna delle variabili interne e se non ha il token si mette in attesa.
     */
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

    /*
     * Quando un nodo vuole uscire dalla sezione critica aggiorna delle varibili interne e rilascia il token al nodo successivo, segnalandolo.
     */
    @Override
    public void unlock() {
        this.wantsToEnter = false;
        this.hasToken = false;
        int next = getNextNodeId();
        System.out.println("Nodo " + id + " passa il token al nodo " + next); 
        send(next, new Message(id, MessageType.TOKEN));
    }

    /*
     * Quando riceve un messaggio con il token, se vuole entrare nella sezione critica lo fa, altrimenti passa il token al nodo successivo.
     */
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