package prodcons.v1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import prodcons.XMLParameters;

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
        
        //Les threads sont réordonnés aléatoirement dans la liste
        Collections.shuffle(threads);
        
        for( Thread t : threads ){
            t.start();
        }
    }   
}
