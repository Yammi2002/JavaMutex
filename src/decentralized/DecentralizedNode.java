package decentralized;

import common.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue; 
import java.util.concurrent.Semaphore;

public class DecentralizedNode extends AbstractNode implements MutualExclusionAlgorithm {
	
	private volatile boolean isMajorityReached = false;
    private final Semaphore waitOk = new Semaphore(0);
    private int yesReceived = 0;
    private int noReceived = 0;
    public int numCoordinators = 0;
    private volatile boolean isInCriticSection = false;
    private boolean wantsToEnter = false;
    
    private Queue<Integer> yesID = new ConcurrentLinkedQueue<>(); 

    public DecentralizedNode(int id, List<AbstractNode> network, int numCoordinators) {
        super(id, network);
        this.numCoordinators = numCoordinators;
    }

    @Override
    public synchronized void receive(Message msg) {
        handleMessage(msg);
    }

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

    // ... (run() va benissimo così com'è)

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