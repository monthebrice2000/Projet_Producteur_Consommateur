package prodcons;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Message 
{   
    private long idProd;
    public Semaphore S;
    //Nombre d'exemplaires du messages à produire
    private int nbExmpl;
    //Nombre de threads qui attendent la consommation de tous les exemplaires du message
    private int nbThreads = 0;
    
    public Message(Thread t, int n) {
        this.idProd = t.getId(); 
        this.nbExmpl = n;
        this.S = new Semaphore(0);
    }
     
    public Message(Thread t) {
    	this.idProd = t.getId();
    }
    
    public long getIdProd(){
        return this.idProd;
    }
    
    public int getNbExmpl() {
    	return this.nbExmpl;
    }
    
    public void addThread() {
    	this.nbThreads++;
    }
    
    public int getNbThreads() {
    	return this.nbThreads;
    }
}
