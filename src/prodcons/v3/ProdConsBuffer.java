package prodcons.v3;


import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;
import prodcons.IProdConsBuffer;
import prodcons.XMLParameters;
import prodcons.Message;


public class ProdConsBuffer implements IProdConsBuffer{
    
    //Load the parameters from the XML file; 
    int bufSz = XMLParameters.bufSz;
    int prodTime = XMLParameters.prodTime; 
    int consTime = XMLParameters.consTime;
    int nbCons = XMLParameters.nCons;
    
    //Initialisation
    private Message[] buffer = new Message[bufSz];
    private Semaphore notEmpty = new Semaphore(0);
    private Semaphore notFull = new Semaphore(bufSz);
    private Semaphore mutex_prod = new Semaphore(1);
    private Semaphore mutex_cons = new Semaphore(1);
    private int in = 0;
    private int out = 0;
    
    //Total messages produits
    private int totmsg = 0;  
    //Nombre de messages consommés
    private int msgcons =0;
    //Nonbre de threads ayant terminé la consommation d'un message
    private int nbConsDone = 0;
    
    @Override
    public void put(Message m) throws InterruptedException {
    	
    	notFull.acquire();
    	
    	mutex_prod.acquire();
    	
    	try {
    		//Production du message    		
    		if(nbConsDone == nbCons) {
    			mutex_prod.release();
    			notFull.release();
    			Thread.currentThread().interrupt();
    		}
    		
    		buffer[in] = m;
    	    sleep(prodTime);
    	    in = (in + 1) % bufSz;
    	    totmsg++;
    	    System.out.println("Un message produit par le Thread : " + m.getIdProd());
    	}
    	finally {
    		mutex_prod.release();
    	}
    	  
    	notEmpty.release();
    }
    
    @Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public Message get() throws InterruptedException {
    	
    	Message m = null;
    	
    	notEmpty.acquire();
    	
    	mutex_cons.acquire();
    	try {
    		//Consommation du message 
            m = buffer[out];
            sleep(consTime);
            out = (out+1) % bufSz;
            msgcons++;
            System.out.println("Un message consommé, messages disponibles : " + nmsg() + ", total produit : "+totmsg); 
            //Le thread signale qu'il à consommer un message 
    		nbConsDone++;
    	}
    	finally {
    		mutex_cons.release();
    	}
    	
    	notFull.release();
               
        return m;
    }

    @Override
    public int nmsg() {
        return (totmsg - msgcons);
    }

    @Override
    public int totmsg() {
        return totmsg;
    }

	@Override
	public Message[] get(int k) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}
