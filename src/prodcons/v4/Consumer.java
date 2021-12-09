package prodcons.v4;

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
            System.out.println( ex.getMessage() );
        } 
    }
}
