package elements.com;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


/*  
 * BIBLIOGRAPHY:
[1]creating dialogs in the client recording system: 
https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
[2] Enumeration for the EventTypesEnum was based on: 
https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
[3] the server monitor is based in the answer from stack overflow forum: 
http://stackoverflow.com/questions/289434/how-to-make-a-java-thread-wait-for-another-threads-output
 * */

public class serverTest3 {
	int loopnumber;
	TimeLine GlobalServerTime;
	
	ConcurrentHashMap<Integer, GameObj> clientsMap = new ConcurrentHashMap<>();
	LinkedBlockingQueue<EventHandler> serverevents= new LinkedBlockingQueue<>();
	EventManagerT manager;
	
	
	boolean Evbusy=false;
	private static Object monitor= new Object();
	
	
	
	int IDServer;
	int port;
	ServerSocket socketConnection;
	int portEvents;
	ServerSocket socketEvents;
	
	GameObj deadZone;
	GameObj deadZone1;
	
	//game world facts
	float gameWorldHeigh;
	float gameWorldWidth;
	
	boolean busy=false;
	
	serverTest3(int i, int j)
	{
		//manager=new EventManagerTest();
		//manager.run();
		runEventManager();
		loopnumber=0;
		port=i;
		portEvents=j;
		IDServer= this.hashCode();
		
		try {
			socketConnection = new ServerSocket(port);
			socketEvents = new  ServerSocket(portEvents);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runEventManager()
	{
		try{
		manager=new EventManagerT();
		//manager.run();
        Thread t = new Thread(manager);
        t.start();
		}
		catch(Exception ee)
		{
			
		}
	}
	
	public void setWolrdSize(float w, float h)
	{
		gameWorldHeigh=w;
		gameWorldHeigh=h;
	}
	public void gameWorldCreation()
	{
		// static platform
		GameObj platform1= new GameObj(gameWorldWidth, gameWorldHeigh, 
				280, (float)(gameWorldHeigh*0.75),80,30);
		platform1.setColor(255, 0, 0);
		platform1.enableDrawable();
		platform1.enableCollidable();
		platform1.collidableComponent.enableCollidableBeweenObjects();
		clientsMap.put(platform1.ID, platform1);
		
		//moving platform
		GameObj Movingplatform= new GameObj(gameWorldWidth, gameWorldHeigh, 
				(float)(gameWorldWidth*0.75), (float)(gameWorldHeigh*0.75),80,30);
		Movingplatform.setColor(0, 255, 0);
		Movingplatform.enableDrawable();
		Movingplatform.enableMovable();
		//Movingplatform.moveComponent.EnableAutomaticPatter(0, 0, 300, 0);
		Movingplatform.moveComponent.setVelosity((float)0.5, (float)0);
		Movingplatform.moveComponent.EnableAutomaticPatterEvent(0, 0, 300, 0);
		Movingplatform.enableCollidable();
		Movingplatform.collidableComponent.enableCollidableBeweenObjects();
		Movingplatform.setIdent(34);
		clientsMap.put(Movingplatform.ID, Movingplatform);
		
		//dead zone
		 deadZone= new GameObj(gameWorldWidth, gameWorldHeigh, 
				300, (float)(gameWorldHeigh*0.95),80,30);
		 //deadZone.setColor(0, 0, 255);
		 //deadZone.enableDrawable();
		 deadZone.enableCollidableAsDeadZone();
		 deadZone.collidableasdeadzone.enaleDeadZoneBehavior();
		 
		//dead zone1
		 deadZone1= new GameObj(gameWorldWidth, gameWorldHeigh, 
				200, 150,30,30);
		 //deadZone1.setColor(0, 0, 255);
		 //deadZone1.enableDrawable();
		 deadZone1.enableCollidableAsDeadZone();
		 deadZone1.collidableasdeadzone.enaleDeadZoneBehavior();
		
		//Swap Point
		GameObj swapPoint= new GameObj(gameWorldWidth, gameWorldHeigh, 
				100, 50,10,10);
		//swapPoint.setColor(5, 100, 100);
		//swapPoint.enableDrawable();
		
		deadZone.collidableasdeadzone.addSpawnPoints(swapPoint);
		deadZone1.collidableasdeadzone.addSpawnPoints(swapPoint);
		//Swap Point 1
		GameObj swapPoint1= new GameObj(gameWorldWidth, gameWorldHeigh, 
				200, 50,10,10);
		//swapPoint1.setColor(5, 100, 100);
		//swapPoint1.enableDrawable();
				
				deadZone.collidableasdeadzone.addSpawnPoints(swapPoint1);
				deadZone1.collidableasdeadzone.addSpawnPoints(swapPoint1);
		
		//Swap Point 2
		GameObj swapPoint2= new GameObj(gameWorldWidth, gameWorldHeigh, 
				50, 100,10,10);
		
		
		deadZone.collidableasdeadzone.addSpawnPoints(swapPoint2);
		deadZone1.collidableasdeadzone.addSpawnPoints(swapPoint2);
		
		clientsMap.put(swapPoint.ID, swapPoint);
		clientsMap.put(swapPoint1.ID, swapPoint1);
		clientsMap.put(swapPoint2.ID, swapPoint2);
		clientsMap.put(deadZone.ID, deadZone);
		clientsMap.put(deadZone1.ID, deadZone1);
		
	}
	
	public static int currentTimeMillis() {
	    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}
	
	public static void main(String[] args) throws IOException {
		int port=2223;
		int eveport= 2224;
		serverTest3 application = new serverTest3(port, eveport); // create server
	   application.setWolrdSize(400, 400);
	   application.gameWorldCreation();
	      //application.refreshClient(); // run server application
	      application.refreshClientByRun();
	      //application.runEventManager();
	}
	
	/*
	 * [1] create a separate thread for every client
	 */
	public void refreshClientByRun()
	{
		GlobalServerTime = new TimeLine();
		
		while(true)
		{
			GlobalServerTime.setActualTime(currentTimeMillis());
			Socket S;
			try {
				S = socketConnection.accept();
				SocketHandlerThread tr= new SocketHandlerThread(S);
				//tr.run();
				Thread t= new Thread(tr);
				t.start();
				
			} 
			catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Socket S2;
			try
			{
				S2= socketEvents.accept();
				SocketEventsThread tr2= new SocketEventsThread(S2);
				//tr2.run();
				Thread t2= new Thread(tr2);
				t2.start();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			loopnumber++;
		}
	}

	public class SocketHandlerThread implements Runnable
	{
		Socket so;
		SocketHandlerThread(Socket s)
		{
			so=s;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
				
			GameObj Gameclient = null;

		      try {

		   

		         ObjectInputStream serverInputStream = new    
		            ObjectInputStream(so.getInputStream());

		         ObjectOutputStream serverOutputStream = new 
		            ObjectOutputStream(so.getOutputStream());

		         Gameclient = (GameObj)serverInputStream.readObject();
		         clientsMap.get(deadZone.ID).collidableasdeadzone.addPlayers(Gameclient);
		         clientsMap.get(deadZone1.ID).collidableasdeadzone.addPlayers(Gameclient);
		        if(!clientsMap.containsKey(Gameclient.IDclient))
		        {
		 	
		        	if(Gameclient.collidableComponent!=null && Gameclient.collidableComponent.collidableBetweenObjects==true)
		        	{
		        		for (Integer key: clientsMap.keySet())
				        {
			        		if(clientsMap.get(key).collidableComponent!=null  &&
				        			clientsMap.get(key).collidableComponent.collidableBetweenObjects==true)
			        		{
			        			Gameclient.collidableComponent.addObjectsToInteract(clientsMap.get(key));
			        			clientsMap.get(key).collidableComponent.addObjectsToInteract(clientsMap.get(Gameclient.IDclient));
			        		}
				        }
		        	}
		    
		        	
		        	clientsMap.put(Gameclient.IDclient, Gameclient);
		        	//System.out.println("Just added to clientsMap: " + Gameclient.IDclient);
		        	
		        }
		        else
		        {
		   
		        	clientsMap.remove(Gameclient.IDclient);
		        	clientsMap.put(Gameclient.IDclient, Gameclient);
		        	
		        }
		        synchronized(monitor) {
		        	 //System.out.println("Notify SockThread");
					  monitor.notify();
					}
		        synchronized(monitor) {
	        		  try {
	        			//  System.out.println("wait SockThread");
	        		    monitor.wait();
	        		  } catch(InterruptedException e) {
	        		  }
	        		}
		        
		       GlobalServerTime.calculateTic();
		        GlobalServerTime.setActualTime(currentTimeMillis());
		        for (Integer key: clientsMap.keySet())
		        {
		        	/*if(clientsMap.get(key).IDclient==0 && clientsMap.get(key).moveComponent !=null)
		        	{
		        		clientsMap.get(key).setGlobalTimeLine(GlobalServerTime);
		        		clientsMap.get(key).moveComponent.Timedelta=(float)GlobalServerTime.ticSize;
		        	}*/
		        	clientsMap.get(key).updateInServer();
		        	if(clientsMap.get(key).objectEvent!=null)
		        	manager.addEventHandler(clientsMap.get(key).extractEvent());
		        	
		        	
		        }
		       
		      
		        serverOutputStream.writeObject(new LinkedList<GameObj>(clientsMap.values()));
		         serverInputStream.close();
		         serverOutputStream.close();
		        

		      }  catch(Exception e) {
		    	 
		    	  e.printStackTrace();
		      }
		      
		}
		
	}


	public class SocketEventsThread implements Runnable
	{
		Socket so;
	
		public SocketEventsThread( Socket s) {
			// TODO Auto-generated constructor stub
			so= s;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			LinkedList<EventHandler> newEvents= new LinkedList<>();
			 try {
				 ObjectInputStream serverInputStream = new    
						 ObjectInputStream(so.getInputStream());
				 ObjectOutputStream serverOutputStream = new
						 ObjectOutputStream(so.getOutputStream());

				 newEvents = (LinkedList<EventHandler>)serverInputStream.readObject();
				
				 for(EventHandler a : newEvents)
				 {
					 //serverevents.add(a);
					manager.addEventHandler(a);
					// addEventHandler(a);
				 }
				// serverOutputStream.writeObject(new LinkedList<GameObj>(clientsMap.values()));
				 serverInputStream.close();
				 serverOutputStream.close();
				 
			 }
			 catch(Exception e) {
		    	 
		    	  e.printStackTrace();
		      }
			
			
		}
		
	}


	//
	public class EventManagerT implements Runnable {

		PriorityBlockingQueue<EventHandler>QueueEvent= new PriorityBlockingQueue<>();
		
		@Override
		public void run() {
			try
			{
				int count=1;
				while(true)
				{
					synchronized(monitor) {
		        		  try {
		        		    monitor.wait();
		        		  } catch(InterruptedException e) {
		        		  }
		        		}
					for(int i=0;i<3;i++)
					if(!QueueEvent.isEmpty())
					{
						EventHandler ev= QueueEvent.peek();
						
						GameObj obj= QueueEvent.take().doEvent(clientsMap.get(ev.IDobject));
						if(obj.IDclient!=0)
						clientsMap.replace(obj.IDclient, obj);
						else 
							clientsMap.replace(obj.ID, obj);
					}
					synchronized(monitor) {
						  monitor.notify();
						}
					count ++;	
				}
			}
			catch(InterruptedException ex)
			{
			}
			
		}
		public void addEventHandler(EventHandler e)
		{
			QueueEvent.put(e);
		}

	}


}
