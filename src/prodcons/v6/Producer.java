package prodcons.v6;

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
        int bufSz = XMLParameters.bufSz;
        
        //Get a random number of messages to produce
        Random rand = new Random ();         
        int nbMessage = minProd + rand.nextInt(maxProd - minProd +1);
        
        //Nombre d'exemplaires à produire 
        int nbExmpl = 3;
        
        for (int i=1; i <= nbMessage; i++){
            try {
                buffer.put(new Message(this, nbExmpl), nbExmpl);
            	//buffer.put(new Message(this));
            } catch (InterruptedException ex) {
                ex.getMessage();
            }
        }
    }
}
