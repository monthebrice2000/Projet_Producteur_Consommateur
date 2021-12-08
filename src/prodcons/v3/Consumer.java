package prodcons.v3;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
