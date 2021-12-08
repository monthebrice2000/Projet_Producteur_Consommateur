package prodcons.v6;

import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Semaphore mutex_pros = new Semaphore(1);
    private int in = 0;
    private int out = 0;
    
    //Nombre de messages disponibles 
    private int nmsg = 0;
    //Total messages produits
    private int totmsg = 0;
    //Nombre de messages consommés
    private int msgcons = 0;
    //Le nombre de consommateurs restants
    private int nbConsRestants = nbCons;
    
    @Override
    public void put(Message m) throws InterruptedException {
    	
    }
    
    @Override
	public void put(Message m, int n) throws InterruptedException 
	{
		notFull.acquire(n);
		
		mutex_prod.acquire();
				
    	try {
    		//Il reste moins de n consommateurs
    		if(nbConsRestants < n) {
    			notFull.release(n);
    			mutex_prod.release();
    			Thread.currentThread().stop();
    		}
    		
    		//Le producteur signale que n threads vont consommer son message
    		nbConsRestants = nbConsRestants - n ;
    		
    		for (int i=1; i<=n; i++)
    		{
    			buffer[in] = m;
        	    sleep(prodTime);
        	    in = (in + 1) % bufSz;
        	    totmsg++;
    		}
    		
    		System.out.println(n + " message(s) produit(s) par le Thread : " + m.getIdProd());
    	}
    	finally 
    	{
    		mutex_prod.release();
    	}
    	
    	//Le thread s'ajoute à la liste de ceux qui attendent la consommation des n exemplaires du message
    	m.addThread();
    	  
    	notEmpty.release(n);
    	
    	//Le producteur attend la consommation des n exemplaires du message produit
    	m.S.acquire();
    	
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
            msgcons ++;
            //Le Thread s'ignale qu'il attend la consommation de tous les exemplaires message
            m.addThread();
            System.out.println("Un message consommé, messages disponibles : " + nmsg() + ", total produit : "+totmsg);           
    	}
    	finally {
    		mutex_cons.release();
    	}
    	
    	//Si c'est le dernier thread à consommer les exemplaires du message
    	if (m.getNbExmpl() == (m.getNbThreads() - 1))
    	{
    		//On réveille le producteur et les autres consommateurs
    		System.out.println("**********************************************************");
    		System.out.println("Les " +m.getNbExmpl()+ " exemplaire(s) ont été consommé(s)\n");
    		m.S.release(m.getNbExmpl());
    		notFull.release(m.getNbExmpl());
    	}
    	//Sinon, le thread attend la consommation des n messages. 
    	else
    	{
    		m.S.acquire();
    	}
    	               
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
