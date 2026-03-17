package decentralized;

import common.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class DecentralizedNode extends AbstractNode implements MutualExclusionAlgorithm {
	
	private boolean isMajorityReached = false;
    private final Semaphore waitOk = new Semaphore(0);
    private int yesReceived = 0;
    private int noReceived = 0;
    public int coordinatorIndex = 0;
    private boolean isInCriticSection = false;
    private Queue<Integer> yesID;

    public DecentralizedNode(int id, List<AbstractNode> network) {
        super(id, network);
    }

    @Override
    public synchronized void receive(Message msg) {
        handleMessage(msg);
    }

    public void handleMessage(Message msg) {
        
    }

    @Override
    public void run() {
        System.out.println("Coordinatore Replica " + id + " attivo.");
        // Resta in attesa di messaggi gestiti da receive()
    }

	@Override
	public void lock() {
	    boolean success = false;
	    while (!success) {
	        this.yesReceived = 0;
	        this.noReceived = 0;
	        this.yesID.clear();
	        this.isMajorityReached = false;
	        
	        for (int i = 0; i < this.network.size() - this.coordinatorIndex; i++) {
	            send(this.network.get(i).id, new Message(this.id, MessageType.REQUEST));
	        }
	        try {
				this.waitOk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        
	        if (isMajorityReached) {
	            success = true;
	            this.isMajorityReached = true;
	            this.isInCriticSection = true;
	        } else {
	        	System.out.println("Nodo " + id + ": Maggioranza non raggiunta. Rilascio e riprovo...");
	        	while(!yesID.isEmpty()) {
	            	int currentID = this.yesID.poll();
		            send(currentID, new Message(this.id, MessageType.RELEASE));

	            }
	            try {
	            	Thread.sleep((long) (Math.random() * 500));
	            	} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	    }
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		
	}
}