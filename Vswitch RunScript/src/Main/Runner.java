package Main;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		int maxswitches = Integer.parseInt(args[0]);
		int maxswitches = 100;
		int switchesPerThread = 10;
		
		File vSwitchRunner = new File("vSwitchRunner.jar");
		
		Timer timer = null;
		Process p = null;
		
		for (int i=1; i <= maxswitches; i++) {
			try {
				System.out.println("Session No: " + i);
				timer = new Timer(true);
				InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread());
				timer.schedule(interrupter, 66 /*seconds*/ * 1000 /*milliseconds per second*/);
				System.out.println("java -jar "+ vSwitchRunner.getPath() + " " + i + " " + switchesPerThread);
				p =  Runtime.getRuntime().exec("java -jar "+ vSwitchRunner.getPath() + " " + i + " " + switchesPerThread);
				p.waitFor();
				java.io.InputStream is=p.getInputStream();
			    byte b[]=new byte[is.available()];
			    is.read(b,0,b.length);
			    System.out.println(new String(b));
			    p.destroy();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
				System.out.println("interrupt!");
				p.destroy();
			}
			finally
	        {
	            timer.cancel();     // If the process returns within the timeout period, we have to stop the interrupter
	                                // so that it does not unexpectedly interrupt some other code later.

	            Thread.interrupted();   // We need to clear the interrupt flag on the current thread just in case
	                                    // interrupter executed after waitFor had already returned but before timer.cancel
	                                    // took effect.
	                                    //
	                                    // Oh, and there's also Sun bug 6420270 to worry about here.
	        }
		}
		
		

	}

}

/**
* Just a simple TimerTask that interrupts the specified thread when run.
*/
class InterruptTimerTask
       extends TimerTask
{

   private Thread thread;

   public InterruptTimerTask(Thread t)
   {
       this.thread = t;
   }

   public void run()
   {
       thread.interrupt();
   }

}
