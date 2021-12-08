package prodcons.v2;

import prodcons.Message;

public class Consumer extends Thread{
    
    private ProdConsBuffer buffer;
    Message m;
    
    public Consumer(ProdConsBuffer buffer){
        this.buffer = buffer;
    }
    
    public void run(){
        try {
            m = buffer.get();
        } catch (InterruptedException ex) {
            ex.getMessage();
        } 
    }
}
