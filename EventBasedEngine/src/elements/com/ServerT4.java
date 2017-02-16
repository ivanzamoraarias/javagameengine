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

import elements.com.serverTest3.EventManagerT;
import elements.com.serverTest3.SocketEventsThread;
import elements.com.serverTest3.SocketHandlerThread;

public class ServerT4 {

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
	
	ServerT4(int i, int j)
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
		
		
		
	}
	
	public static int currentTimeMillis() {
	    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		int port=2223;
		int eveport= 2224;
		ServerT4 application = new ServerT4(port, eveport); // create server
	   application.setWolrdSize(800, 400);
	   application.gameWorldCreation();
	   
	   application.runEventManager();
	   application.exchangeDataWithClient();
	      //application.refreshClient(); // run server application
	     //---                                                                                                                      application.refreshClientByRun();
	      //application.runEventManager();
	}
	
	/*
	 * [1] create a separate thread for every client
	 */
	public void exchangeDataWithClient() throws Exception
	{
		Socket socket = socketConnection.accept();
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		
		while(true)
		{
		GameObj Gameclient = null;
		 Gameclient = (GameObj)in.readObject();
    
       //Inserat logica
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
         
         //fin logica
         //extraccion de eventos
         int tamano= in.readInt();
         System.out.println("Size de la lsita--- "+tamano);
         
         String fin="";
         while(fin!="t")
         {
        	 Object o = in.readObject();
        	 try{
        		 String a=(String)o;
        		 if(a.equals("t"))
        		 {	
        			 fin=a;
        			 System.out.println("fin de llegada "+a);
        			 break;
        		 }
        	 }
        	 catch(Exception e){
        		 EventHandler a =(EventHandler)o;
        		 manager.addEventHandler(a);
        		 System.out.println("Se tiene un evento "+a.toString());
        	 }
        	 
         }
         System.out.println("-------");
         //LinkedList<EventHandler> newEvents = (LinkedList<EventHandler>)in.readObject();
         //System.out.println("Tamano de la lista que llega "+ newEvents.size());
         
         
         /*for(EventHandler a : newEvents)
		 {
        	 manager.addEventHandler(a);
		 }*/
         
         
         for (Integer key: clientsMap.keySet())
	     {
        	 clientsMap.get(key).updateInServer();
        	 if(clientsMap.get(key).objectEvent!=null)
		        	manager.addEventHandler(clientsMap.get(key).extractEvent());
	     }
         
         synchronized (monitor) {
				monitor.wait();;
			}
         //System.out.println("Size "+clientsMap.size());
        out.writeObject(new LinkedList<GameObj>(clientsMap.values()));
		}
	}
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
				//int count=1;
				while(true)
				{
					
					//for(int i=0;i<3;i++)
					//{
						//Thread.sleep(100);
					if(!QueueEvent.isEmpty())
					{
						System.out.println("Si esta llena la Queue");
						EventHandler ev= QueueEvent.peek();
						//System.err.println("peek "+ev.ID);
						
						GameObj obj= QueueEvent.take().doEvent(clientsMap.get(ev.IDobject));
						if(obj.IDclient!=0)
						{
							System.err.println("Antes x "+clientsMap.get(obj.IDclient).position[0]
									+" y "+clientsMap.get(obj.IDclient).position[1]);
							//clientsMap.get(obj.IDclient);
							clientsMap.replace(obj.IDclient, obj);
							//
							System.err.println("Despues x "+clientsMap.get(obj.IDclient).position[0]
									+" y "+clientsMap.get(obj.IDclient).position[1]);
						}
						else 
							clientsMap.replace(obj.ID, obj);
						
						
					}
					synchronized (monitor) {
						monitor.notify();
					}
					//}
					//count ++;	
				}
			}
			catch(InterruptedException ex)
			{
			}
			
		}
		public void addEventHandler(EventHandler e)
		{
			QueueEvent.put(e);
			//System.err.println("Se aggrego "+e.ID);
		}

	}
}
