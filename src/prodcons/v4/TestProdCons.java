package prodcons.v4;

import prodcons.XMLParameters;

import java.util.ArrayList;
import java.util.Collections;

public class TestProdCons {

    public static void main(String[] args) {
        
        //Loading paramters from XML file
        XMLParameters.getParameters(); 
        int nProd = XMLParameters.nProd;
        int nCons = XMLParameters.nCons;
        
        ProdConsBuffer buffer = new ProdConsBuffer();
        
        ArrayList <Thread> threads = new ArrayList <Thread>();
    
        for (int i=1; i<= nProd; i++){
            Producer prodThread = new Producer(buffer);
            threads.add(prodThread);
            //prodThread.start();
        }
        for(int i=1; i<= nCons; i++){
            Consumer consThread = new Consumer(buffer);
            threads.add(consThread);
            //consThread.start();
        }
        
        //Les threads sont ordonnés aléatoirement
        Collections.shuffle(threads);
        
        long start = System.currentTimeMillis();
        
        for( Thread t : threads ){
            t.start();
        }
        
        for(Thread t : threads ) {
        	try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("\n**************************************************************");
        System.out.println("Programme terminé");
        System.out.println("Temps d'éxecution : " + (end-start) + " ms");
        
    }   
}
