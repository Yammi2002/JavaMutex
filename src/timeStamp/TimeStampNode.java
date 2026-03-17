package timeStamp;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import common.AbstractNode;
import common.Message;
import common.MessageType;
import common.MutualExclusionAlgorithm;

/**
 * Classe che rappresenta i nodi nell'algoritmo di mutua eslusione basato su timestamps
 */
public class TimeStampNode extends AbstractNode implements MutualExclusionAlgorithm {
	private Queue<Integer> queue;
	private int receivedOkCount = 0;
	private boolean wantsToEnter = false;
	private boolean isInCriticSection = false;
    private final Semaphore waitOk = new Semaphore(0);
    private long myRequestTimestamp = 0;


    /**
     * Costruttore della classe
     * @param id, identificativo del nodo
     * @param network, lista dei nodi della rete
     */
	public TimeStampNode(int id, List<AbstractNode> network) {
		super(id, network);
	}

	/**
	 * Quando un nodo vuole entrare nella sezione critica, lo segnala a tutti gli altri nodi della rete, indicando anche il timestamp
	 * corrente. Successivamente si mette in attesa su un semaforo.
	 */
	@Override
	public void lock() {
	    this.wantsToEnter = true;
	    this.receivedOkCount = 0;
	    this.myRequestTimestamp = System.currentTimeMillis();

	    System.out.println("Nodo " + id + " invia richieste a tutti...");
	    for (AbstractNode node : network) {
	        if (node.id != this.id) {
	            send(node.id, new Message(this.id, MessageType.REQUEST, this.myRequestTimestamp));
	        }
	    }

	    try {
	        waitOk.acquire(); 
	        this.isInCriticSection = true; 
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

	/*
	 * Quando un nodo vuole liberare la propria sezione critica aggiorna delle variabili interne e segnala i nodi in attesa.
	 */
	@Override
	public void unlock() {
	    System.out.println("Nodo " + id + " esce dalla sezione critica.");
	    this.isInCriticSection = false;
	    this.wantsToEnter = false;

	    while (!this.queue.isEmpty()) {
	        int targetId = this.queue.poll();
	        send(targetId, new Message(this.id, MessageType.OK));
	    }
	}

	@Override
	public void receive(Message msg) {
	    handleMessage(msg);
	}

	/**
     * Gestisce i messaggi in arrivo secondo la logica di Ricart e Agrawala.
     * * Il metodo distingue due scenari:
     * 1. Messaggio OK: Incrementa il contatore dei consensi. Al raggiungimento di n-1 
     * risposte, sblocca l'accesso alla sezione critica.
     * 2. Messaggio REQUEST: Valuta la precedenza basandosi sul timestamp della richiesta. 
     * Se il nodo locale ha una priorità maggiore (è già in SC o ha chiesto l'accesso 
     * con un timestamp inferiore), posticipa la risposta inserendo il mittente in coda. 
     * Altrimenti, invia immediatamente il messaggio di OK.
     * * @param msg Il messaggio (REQUEST o OK) ricevuto dagli altri nodi della rete.
     */
	@Override
	public void handleMessage(Message msg) {
	    if (msg.getType() == MessageType.OK) {
	        this.receivedOkCount++;
	        if (this.receivedOkCount == (network.size() - 1)) {
	            waitOk.release(); 
	        }
	    } 
	    else if (msg.getType() == MessageType.REQUEST) {
	        long receivedTimestamp = msg.getTimestamp();
	        int senderId = msg.getSenderId();

	        boolean iHavePrecedence = isInCriticSection || 
	            (wantsToEnter && (myRequestTimestamp < receivedTimestamp || 
	                             (myRequestTimestamp == receivedTimestamp && id < senderId)));

	        if (iHavePrecedence) {
	            this.queue.add(senderId);
	        } else {
	            send(senderId, new Message(this.id, MessageType.OK));
	        }
	    }
	}
	
	@Override
	public void run() {
	    this.queue = new java.util.LinkedList<>();

	    try {
	        Thread.sleep(2000); 

	        while (true) {
	            System.out.println("[LOG] Nodo " + id + " esegue operazioni locali...");
	            Thread.sleep((long) (Math.random() * 5000)); 

	            lock(); 

	            System.out.println(">>> [SC] Nodo " + id + " è in Sezione Critica! (TS: " + myRequestTimestamp + ")");
	            Thread.sleep(2000); 

	            unlock(); 
	            System.out.println("<<< [OUT] Nodo " + id + " è uscito.");
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

}
