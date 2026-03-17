package decentralized;

import common.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue; 
import java.util.concurrent.Semaphore;

/**
 * Classe che rappresenta i nodi nell'algoritmo di mutua esclusione decentralizzato
 */
public class DecentralizedNode extends AbstractNode implements MutualExclusionAlgorithm {
	
	private volatile boolean isMajorityReached = false;
    private final Semaphore waitOk = new Semaphore(0);
    private int yesReceived = 0;
    private int noReceived = 0;
    public int numCoordinators = 0;
    private volatile boolean isInCriticSection = false;
    private boolean wantsToEnter = false;
    
    private Queue<Integer> yesID = new ConcurrentLinkedQueue<>(); 

    /**
     * Costruttore della classe.
     * @param id Identificativo unico del nodo.
     * @param network La lista di tutti i nodi della rete (processi e coordinatori).
     * @param numCoordinators Numero di coordinatori (repliche) da interrogare per il voto.
     */
    public DecentralizedNode(int id, List<AbstractNode> network, int numCoordinators) {
        super(id, network);
        this.numCoordinators = numCoordinators;
    }

    @Override
    public synchronized void receive(Message msg) {
        handleMessage(msg);
    }

    /**
     * Gestisce l'arrivo dei messaggi di voto dai coordinatori.
     * Implementa la logica di conteggio per determinare il raggiungimento della 
     * maggioranza (VOTE_YES) o l'impossibilità matematica di ottenerla (VOTE_NO).
     * @param msg Il messaggio ricevuto dalla rete.
     */
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.getType() == MessageType.VOTE_YES) {
            yesReceived++;
            yesID.add(msg.getSenderId()); 
        } else if (msg.getType() == MessageType.VOTE_NO) {
            noReceived++;
        }

        int majority = (numCoordinators / 2) + 1;

        if (yesReceived >= majority) {
            this.isMajorityReached = true;
            waitOk.release();
        } else if (noReceived >= majority || (yesReceived + noReceived == numCoordinators)) {
            this.isMajorityReached = false;
            waitOk.release();
        }
    }


    /**
     * Quando il nodo vuole entrare nella sezione critica, modifica delle variabili interne per tenere traccia del flusso.
     * Manda poi una richiesta a tutti i coordinatori, attendendo su un semaforo. Quando si arriva ad una maggioranza di sì o di no
     * si liberano i coordinatori e se si ha accesso il nodo entra nella sezione critica, altrimenti riprova dopo un tempo casuale.
     */
	@Override
	public void lock() {
		this.wantsToEnter = true;
	    boolean success = false;
	    while (!success) {
	    	waitOk.drainPermits();
            
            synchronized(this) {
    	        this.yesReceived = 0;
    	        this.noReceived = 0;
    	        this.yesID.clear();
    	        this.isMajorityReached = false;
            }
	        
	        for (int i = 0; i < this.numCoordinators; i++) {
	            send(this.network.get(i).id, new Message(this.id, MessageType.REQUEST));
	        }
            
	        try {
				this.waitOk.acquire();
			} catch (InterruptedException e) { e.printStackTrace(); }
	        
	        if (isMajorityReached) {
	            success = true;
	            this.isInCriticSection = true;
	        } else {
	        	System.out.println("Nodo " + id + ": Maggioranza non raggiunta. Rilascio...");
	        	while(!yesID.isEmpty()) {
	            	Integer currentID = this.yesID.poll();
                    if(currentID != null)
		                send(currentID, new Message(this.id, MessageType.RELEASE));
	            }
	            try {
	            	Thread.sleep((long) (Math.random() * 500));
	            } catch (InterruptedException e) { e.printStackTrace(); }
	        }
	    }
	}

	/*
	 * Quando un nodo vuole uscire dalla sezione critica modifica un insieme di variabili interne e libera i coordinatori, segnalandolgli 
	 * la sua intenzione.
	 */
	@Override
	public void unlock() {
		this.isInCriticSection = false;
		this.wantsToEnter = false;
		while(!yesID.isEmpty()) {
        	Integer currentID = this.yesID.poll();
            if(currentID != null)
                send(currentID, new Message(this.id, MessageType.RELEASE));
        }
	}
	
	@Override
	public void run() {
	    try {
	        Thread.sleep(2000);
	        while (true) {
	            Thread.sleep((long) (Math.random() * 5000));
	            lock();
	            System.out.println(">>> [SC] Nodo " + id + " ha ottenuto la maggioranza!");
	            Thread.sleep(2000);
	            unlock();
	            System.out.println("<<< [OUT] Nodo " + id + " ha rilasciato le repliche.");
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}
}