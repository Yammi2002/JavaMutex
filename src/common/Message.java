package common;

import java.io.Serializable;

/**
 * Classe che rappresenta i messaggi che verranno scambiati nella rete.
 * Usa un enum per comprendere tutti i tipi di messaggi necessari nelle varie implementazioni.
 */
public class Message implements Serializable {
    private final int senderId;
    private final MessageType type;
    private final long timestamp; 
    private static final long serialVersionUID = 1L;
    
    /**
     * Costruttore della classe
     * @param senderId, identificativo del mittente
     * @param type, tipo del messaggio
     * @param timestamp, utilizzato nell'algoritmo basato su timestamp
     */
    public Message(int senderId, MessageType type, long timestamp) {
        this.senderId = senderId;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    /**
     * Costuttore alternativo per gli algoritmi non basati su timestamp
     * @param senderId
     * @param type
     */
    public Message(int senderId, MessageType type) {
        this(senderId, type, System.currentTimeMillis());
    }
    
    public int getSenderId() {
        return senderId;
    }

    public MessageType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
