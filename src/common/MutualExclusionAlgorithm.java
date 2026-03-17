package common;

/**
 * Interfaccia che contiene i metodi usati negli algoritmi di mutua esclusione
 */
public interface MutualExclusionAlgorithm {
    void lock(); 
    
    void unlock();
    
    void handleMessage(Message msg);
}
