package prodcons.v4;


import prodcons.IProdConsBuffer;
import prodcons.Message;
import prodcons.XMLParameters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;


public class ProdConsBuffer implements IProdConsBuffer{
    
    //Load the parameters from the XML file; 
    int bufSz = XMLParameters.bufSz;
    int prodTime = XMLParameters.prodTime; 
    int consTime = XMLParameters.consTime;
    int nbCons = XMLParameters.nCons;
    int nbProd = XMLParameters.nProd;
    
    //Initialisation
    private Message[] buffer = new Message[bufSz];
    private int notEmpty = 0;
    private int notFull = bufSz;
    private Lock mutex = new ReentrantLock();
    private Condition condition_prod_lock = mutex.newCondition();
    private Condition condition_cons_lock = mutex.newCondition();
    private int in = 0;
    private int out = 0;
    
    //Total messages produits
    private int totmsg = 0;  
    //Nombre de messages consommés
    private int msgcons =0;
    //Nonbre de threads ayant terminé la consommation d'un message
    private int nbConsDone = 0;
    private int nbProdDone = 0;
    
    @Override
    public void put(Message m) throws InterruptedException {
        mutex.lock();

        try{
            if ( nbConsDone<nbCons && notFull == 0 ){
                condition_prod_lock.await();
            }

            if(nbConsDone == nbCons) {
                condition_prod_lock.signalAll();
                //mutex_prod_lock.unlock();
                Thread.currentThread().interrupt();
                throw new InterruptedException( "Un message produit par le Thread : " + m.getIdProd()+ " est interrompu par inssufisance de consommateur");
            }

            buffer[in] = m;
            sleep(prodTime);
            in = (in + 1) % bufSz;
            notFull --;
            notEmpty ++ ;
            totmsg++;
            nbProdDone ++;
            System.out.println("Un message produit par le Thread : " + m.getIdProd());


            condition_cons_lock.signalAll();
        }catch ( InterruptedException exc ){
            throw new InterruptedException( exc.getMessage() );
        }finally {
            mutex.unlock();
        }




    }
    
    @Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public Message get() throws InterruptedException {
    	mutex.lock();
    	Message m = null;

    	try{
            if ( notEmpty == 0 ){
                condition_cons_lock.await();
            }

            if( nmsg() <= 0) {
                condition_cons_lock.signalAll();
                //mutex_prod_lock.unlock();
                Thread.currentThread().interrupt();
                throw new InterruptedException( "Un consommateur est interrompu par inssufisance de producteur");
            }

            m = buffer[out];
            sleep(consTime);
            out = (out+1) % bufSz;
            msgcons++;
            notEmpty--;
            nbConsDone++;
            notFull++;


            System.out.println("Un message consommé, messages disponibles : " + nmsg() + ", total produit : "+totmsg);

            condition_prod_lock.signalAll();
        }catch ( InterruptedException exc ){
    	    throw new InterruptedException( exc.getMessage() );
        }finally {
            mutex.unlock();
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
