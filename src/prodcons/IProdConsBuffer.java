package prodcons;

public interface IProdConsBuffer 
{	
	/**
	* Put the message m in the prodcons buffer
	**/
    public void put(Message m) throws InterruptedException;
    
    /**
    * Put n instances of the message m in the prodcons buffer
    * The current thread is blocked until all
    * instances of the message have been consumed
    * Any consumer of m is also blocked until all the instances of
    * the message have been consumed
    **/
    public void put(Message m, int n) throws InterruptedException;
    
    /**
    * Retrieve a message from the buffer,
    * following a FIFO order (if M1 was put before M2, M&
    * will be get before M2)
    **/
    public Message get () throws InterruptedException;
    
    /**
    * Retrieve k consecutive messages from the prodcons buffer
    **/
    public Message[] get (int k) throws InterruptedException;
    
    public int nmsg();
    /**
    * Returns the total number of messages that have
    * been put in the buffer since its creation
    **/
    public int totmsg();
}
