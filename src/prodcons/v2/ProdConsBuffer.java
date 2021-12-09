package prodcons.v2;

import static java.lang.Thread.sleep;
import prodcons.IProdConsBuffer;
import prodcons.XMLParameters;
import prodcons.Message;


public class ProdConsBuffer implements IProdConsBuffer{
    
    //Load the parameters from the XML file; 
    int bufSz = XMLParameters.bufSz;
    int prodTime = XMLParameters.prodTime; 
    int consTime = XMLParameters.consTime;
    int nbProd = XMLParameters.nProd;
    int nbCons = XMLParameters.nCons;
    
    private Message[] buffer = new Message[bufSz];
    private int notEmpty = 0;
    private int notFull = bufSz;
    private int in = 0;
    private int out = 0;
    

    //Total messages produits
    private int totmsg = 0;
    //Nombre de messages consommés
    private int msgcons = 0;
    //Nombre de producteurs ayant terminés
    private int nbProdDone = 0;
    //Nombre de threads ayant consommé un message
    private int nbConsDone = 0;
    
    @Override
    public synchronized void put(Message m) throws InterruptedException {
    	
        while ( nbConsDone<nbCons && notFull == 0 ){
            try{
                wait();
            }
            catch(InterruptedException e){
                e.getMessage();
            }
        }

        if(nbConsDone == nbCons) {
            notifyAll();
            Thread.currentThread().interrupt();
            throw new InterruptedException( "Un message produit par le Thread : " + m.getIdProd()+ " est interrompu par inssufisance de consommateur");
        }

        
        //Production du message 
        buffer[in] = m;
        sleep(prodTime);
        in = (in + 1) % bufSz;
        totmsg++;
        notFull--;
        notEmpty++;
        System.out.println("Un message produit par le Thread : " + m.getIdProd());
         
        notifyAll();
    }
    
    @Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public synchronized Message get() throws InterruptedException {
        
        while(notEmpty == 0){
            try{
                if(nbProdDone == nbProd){
                	notifyAll();
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("Le consommateur ne peut pas consommer par inssufisance de message");
                }
                wait();
            }
            catch(InterruptedException e){
                e.getMessage();
            }
        }
        //Consommation du message 
        Message m = buffer[out];
        sleep(consTime);
        out = (out+1) % bufSz;
        msgcons++;
        notEmpty--;
        nbConsDone++;
        notFull++;
        
        System.out.println("Un message consommé, messages disponibles : " + nmsg() + ", total produit : "+totmsg);
        
        notifyAll();
       
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
    
    public synchronized void producerDone(){
        nbProdDone++;
        notifyAll();
    }

	@Override
	public Message[] get(int k) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
}
