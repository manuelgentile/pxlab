package it.cnr.itd.pxlab;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;

public class Snippet {
	public static void main (String [] args)
	    {
	        // JIT/hotspot warmup:
		
		
			Random r=new Random();
			
			
			for (int i=0;i<10;i++)
			{
				int nano=r.nextInt(200);
		
				long a=System.nanoTime();
				
				LockSupport.parkNanos(nano);
				
				long b=System.nanoTime();
				
				
				System.out.println("Atteso:"+nano+"\t Reale:"+(b-a));
			}
		/*
		 * 
	        for (int r = 0; r < 3000; ++ r) System.currentTimeMillis ();
	        long time = System.currentTimeMillis (), time_prev = time;
	        
	        
	        
	        
	        for (int i = 0; i < 5; ++ i)
	        {
	            // Busy wait until system time changes: 
	            while (time == time_prev)
	                time = System.currentTimeMillis ();
	            System.out.println ("delta = " + (time - time_prev) + " ms");
	            time_prev = time;
	        }*/
	    }
}

