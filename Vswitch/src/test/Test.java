package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ofcprobe.util.Util;
import ofcprobe.vswitch.runner.OFSwitchRunner;
import ofcprobe.vswitch.trafficgen.TrafficGen;



	/**
	 * @param args
	 */
	class Test extends Thread
	{
		static class JoinerThread extends Thread
		  {
		    public int result;

		    @Override public void run()
		    {
		      result = 1;
		    }
		  }

		  public static void main( String[] args ) throws InterruptedException
		  {
		    JoinerThread t = new JoinerThread();
		    t.start();
		    t.join();
		    System.out.println( t.result );
		  }
	
////		// TODO Auto-generated method stub
//		int simulationTime = ((int) Math.floor(10000 / 1000)) + 15; 
//		ArrayList<Integer> temp1 = new ArrayList<Integer>();
//		Util.ensureSize(temp1, simulationTime);
//		System.out.println(temp1.size());
//		temp1.set(10, 1);
//		temp1.add(10,2);
//		System.out.println(temp1);
//		System.out.println(temp1.size());
//		cleanupList(temp1);
//			System.out.println(temp1);
//			System.out.println(temp1.size());
//		Vector<String> list = new Vector<String>(
//				Arrays.asList("0 1 2 3 4 5 6 7 8 9 10".split(" ")));
//		
//		System.out.println(list);
//		System.out.println(list.capacity());
//		list.add("11");
//		System.out.println(list);
//		System.out.println(list.capacity());
//		for (int i=0; i<15; i++)
//			list.add("x");
//		System.out.println(list);
//		System.out.println(list.capacity());
		
//		Date startDate = new Date();
//		try {
//			Thread.sleep(2900);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Date now = new Date();
//		
//		int intervall = Util.getIntervall(startDate, now);
//		System.out.println(startDate.getTime());
//		System.out.println(now.getTime());
//		System.out.println(intervall);
//		int ThreadCount = 8;
//		int switchCount = 19;
//		int switchesPerThread = (int) (switchCount / ThreadCount);
//		int rest= switchCount%ThreadCount;
//		System.out.println("Switches per Thread: " + switchesPerThread);
//		int configSwitches=switchesPerThread;
//		
//		
//		
//		List<OFSwitchRunner> switchRunners = new ArrayList<OFSwitchRunner>();
//		List<Thread> switchThreads = new ArrayList<Thread>();
//		Map<OFSwitchRunner,Thread> switchThreadMap = new HashMap<OFSwitchRunner, Thread>();
//		
//		int initializedSwitches = 0;
//		
//		if (switchCount < ThreadCount) {
//			ThreadCount = switchCount;
//		}
//		
//		for (int i = 0; i < ThreadCount; i++) {
//			int switchesLeft = switchCount - initializedSwitches;
//			System.out.println("Switches left: " + switchesLeft);				
//			if (i < rest ) {
//					configSwitches = switchesPerThread+1;
////					config.getRunnerConfig().setCountSwitches(switchesLeft+1);
//					System.out.println(i + ": " +configSwitches);
//					initializedSwitches+=(switchesPerThread+1);
//				
//			} else 
//			{
//				configSwitches = switchesPerThread;
//				initializedSwitches+=switchesPerThread;
//			}
//				
//			
//			System.out.println(initializedSwitches);
//		}
	

	
	private static void cleanupList(List<Integer> list) {
	ListIterator<Integer> it = list.listIterator(list.size());
	while (it.hasPrevious()) {
		if (it.previous()==0) {
			it.remove();
			
		} else break;
	}
}

}
