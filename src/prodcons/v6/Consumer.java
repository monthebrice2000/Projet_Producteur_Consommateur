package prodcons.v6;

import prodcons.Message;

public class Consumer extends Thread{
    
    private ProdConsBuffer buffer;
    
    public Consumer(ProdConsBuffer buffer){
        this.buffer = buffer;
    }
    
    public void run(){
        try {
            Message m = buffer.get();
        } catch (InterruptedException ex) {
            ex.getMessage();
        } 
    }
}
