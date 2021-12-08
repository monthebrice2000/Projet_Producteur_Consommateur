package prodcons.v5;

import static java.lang.Thread.sleep;

import java.util.ArrayList;
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
    
    //Nombre de messages disponibles 
    private int nmsg = 0;
    //Total messages produits
    private int totmsg = 0;  
    //Nombre de messages consommés
    private int msgcons = 0;
    //Producteurs et consommateurs achevés
    private int nbConsDone = 0;
    
    @Override
    public void put(Message m) throws InterruptedException {
    	
    	notFull.acquire();
    	
    	mutex_prod.acquire();
    	
    	try {
    		//Production du message
    		
    		//On stoppe le producteur s'il tous les messages ont été consommés
    		if(nbConsDone == nbCons) {
    			mutex_prod.release();
    			notFull.release();
    			Thread.currentThread().stop();
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
               
        return null;
    }


	@Override
	public Message[] get(int k) throws InterruptedException {
		
		ArrayList <Message> messages = new ArrayList<Message>();
		Message[] m = new Message[k];
		
		//On teste le cas où k > bufSz
		int i = k / bufSz;
		
		for (int j=1; j<=i; j++) {
			
			notEmpty.acquire(bufSz);
			
			mutex_cons.acquire();
			
			try
			{
				for(int l=1; l<=bufSz; l++) {
					messages.add(buffer[out]);
					sleep(consTime);
					out = (out+1) % bufSz;
					msgcons++;
				}
				System.out.println(bufSz + " message(s) consommé(s), messages disponibles : " + nmsg() + ", total produit : "+totmsg);
				notFull.release(bufSz);
			}
			finally
			{
				mutex_cons.release();
			}			
		}
		
		int r = k % bufSz;
		
		notEmpty.acquire(r);
		
		mutex_cons.acquire();
		
		try
		{	
			if (r > 0) 
			{
				for(int j=1; j<=r; j++) {
					messages.add(buffer[out]);
					sleep(consTime);
					out = (out+1) % bufSz;
					msgcons++;
				}
				System.out.println(r + " message(s) consommé(s), messages disponibles : " + nmsg() + ", total produit : "+totmsg);
				notFull.release(r);
			}
		}
		finally
		{
			mutex_cons.release();
		}
		
		//Le thread a terminé sa consommation si k >= bufSz
		nbConsDone++; 
		
		messages.toArray(m);
				
		return (m);
		
	}
	
	@Override
    public int nmsg() {
        return (totmsg - msgcons);
    }

    @Override
    public int totmsg() {
        return totmsg;
    }
}
