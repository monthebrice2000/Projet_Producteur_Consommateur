package prodcons.v1;

import java.util.Random;
import prodcons.Message;
import prodcons.XMLParameters;

public class Producer extends Thread{

    private ProdConsBuffer buffer;
    
    public Producer(ProdConsBuffer buffer){
        this.buffer = buffer;
    }
    
    public void run(){
        //Load the parameters from the XML file
        int minProd = XMLParameters.minProd;
        int maxProd = XMLParameters.maxProd;
        
        //Get a random number of messages
        Random rand = new Random ();         
        int nbmsg = minProd + rand.nextInt(maxProd - minProd +1);
        
        for (int i=1; i<=nbmsg; i++){
            try {
                buffer.put(new Message(this));
                //System.out.println("Un message produit par le thread : "+this.getId());
            } catch (InterruptedException ex) {
                ex.getMessage();
            }
        }
    }
    
}
