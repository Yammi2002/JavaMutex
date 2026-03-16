package common;

import java.io.Serializable;

public class Message implements Serializable {
    private final int senderId;
    private final MessageType type;
    private final long timestamp; 
    
    public Message(int senderId, MessageType type, long timestamp) {
        this.senderId = senderId;
        this.type = type;
        this.timestamp = timestamp;
    }
    
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
