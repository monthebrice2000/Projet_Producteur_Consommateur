package prodcons.v5;

import java.util.Random;

import prodcons.Message;
import prodcons.XMLParameters;

public class Consumer extends Thread{
    
    private ProdConsBuffer buffer;
    
    public Consumer(ProdConsBuffer buffer){
        this.buffer = buffer;
    }
    
    public void run(){
    	
    	//Chargement des paramètres à partir du fichier XML
    	int bufSz = XMLParameters.bufSz;
    	
    	//Génération d'un nombre de messages aléatoires à consommer
    	Random rand = new Random (); 
        int k = 1 + rand.nextInt(bufSz * 2);
        
        try {
            Message[] m = buffer.get(k);
        } catch (InterruptedException ex) {
            ex.getMessage();
        } 
    }
}
