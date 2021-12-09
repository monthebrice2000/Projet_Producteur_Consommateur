package prodcons.v4;


import com.sun.source.doctree.ThrowsTree;
import prodcons.IProdConsBuffer;
import prodcons.Message;
import prodcons.XMLParameters;

import java.util.concurrent.Semaphore;
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
    private Lock mutex_prod_lock = new ReentrantLock();
    private Condition condition_prod_lock = mutex_prod_lock.newCondition();
    private Lock mutex_cons_lock = new ReentrantLock( );
    private Condition condition_cons_lock = mutex_prod_lock.newCondition();
    private int in = 0;
    private int out = 0;
    
    //Total messages produits
    private int totmsg = 0;  
    //Nombre de messages consommés
    private int msgcons =0;
    //Nonbre de threads ayant terminé la consommation d'un message
    private int nbConsDone = 0;
    private int nbProdDone = 0;
    private int waitings_prods = 0;
    private int waitings_cons = 0;
    
    @Override
    public void put(Message m) throws InterruptedException {
        mutex_prod_lock.lock();
        try {
            while( nbConsDone<nbCons && notFull == 0 ){

                System.out.println("Thread producteur en attente");
                if ( waitings_cons > 0 ){
                    waitings_prods ++;
                    condition_cons_lock.signalAll();

                }
                mutex_prod_lock.unlock();
                condition_prod_lock.wait();
                mutex_prod_lock.lock();
                System.out.println("l s'est reveillé");
                waitings_prods--;

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
            throw new InterruptedException( "le thread est interrompu ");
        }finally {
            System.out.println("Producteur quitté");
            mutex_prod_lock.unlock();
        }


    }
    
    @Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

    @Override
    public Message get() throws InterruptedException {
    	mutex_prod_lock.lock();
    	Message m = null;
    	try {
    	    while ( notEmpty == 0 ){

                System.out.println("Thread consommateur en attente+++");
                if ( waitings_prods > 0 ){
                    condition_prod_lock.signalAll();
                    waitings_cons ++;

                }
                mutex_prod_lock.unlock();
                condition_cons_lock.wait();
                mutex_prod_lock.lock();
                System.out.println("l s'est reveillé");
                waitings_cons--;

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
    	    exc.getMessage();
        }finally {
    	    System.out.println("Consommateur quitté");
            mutex_prod_lock.unlock();
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
