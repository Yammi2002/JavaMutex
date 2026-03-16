package common;

public interface MutualExclusionAlgorithm {
    void lock(); 
    
    void unlock();
    
    void handleMessage(Message msg);
}
