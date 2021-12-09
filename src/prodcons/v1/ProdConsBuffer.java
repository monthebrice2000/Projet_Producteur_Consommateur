package prodcons.v1;

import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.Properties;
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
    
    private Message[] buffer = new Message[bufSz];
    private int notEmpty = 0;
    private int notFull = bufSz;
    private int in = 0;
    private int out = 0;
    
    //Nombre de messages consommés
    private int msgcons = 0;
    //Total messages produits
    private int totmsg = 0;    
    
    @Override
    public synchronized void put(Message m) throws InterruptedException {
        while (notFull == 0){
            try{
                wait();
            }
            catch(InterruptedException e){
                throw new InterruptedException("Un des Threads " + m.getIdProd() + " a été intérrompu");
            }
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
    public synchronized Message get() throws InterruptedException {
        while(notEmpty == 0){
            try{
                wait();
            }
            catch(InterruptedException e){
                throw new InterruptedException("Un des consommateurs a été intérrompu");
            }
        }
        //Consommation du message 
        Message m = buffer[out];
        sleep(consTime);
        out = (out+1) % bufSz;
        msgcons++;
        notEmpty--;
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

	@Override
	public Message[] get(int k) throws InterruptedException {
		return null;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}  
}
