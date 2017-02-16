package elements.com;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import processing.core.PApplet;
import elements.com.clientTest3.ClientEventManager;
import elements.com.clientTest3.ServerEventManager;

public class ClientT4 extends PApplet {

	Socket socket = new Socket();
	ObjectOutputStream out;
	ObjectInputStream in;
	
	TimeLine clientTimeline= new TimeLine();

	GameObj c;
	String serverName = "localhost";
    int port = 2223;
    int eveport=2224;
    LinkedList<GameObj>retrieveList= new LinkedList<>();
    LinkedList<EventHandler> eventList2= new LinkedList<>();
    LinkedList<EventHandler> serveventList= new LinkedList<>();
    
    boolean record;
    boolean replay;
    AtomicBoolean replayb= new AtomicBoolean();
    RecordTimeLine recTimeline;
    RecordTimeLine servRecTimeline;
    AtomicBoolean doServer= new AtomicBoolean(true);
    float replayfactor=1;
    float []savedPos= null;
    //LinkedBlockingQueue<Map> savedPosLis;//t= new LinkedBlockingQueue<>();
    ConcurrentHashMap<Integer, float[]> savedPosMap;
    AtomicBoolean endPermit= new AtomicBoolean();
    AtomicBoolean endRepC= new AtomicBoolean();
    AtomicBoolean endRepS= new AtomicBoolean();

	
	private static Object monitor= new Object();
	ClientEventManager clientManager;// new ClientEventManager();
	ServerEventManager serverManager;
	LinkedBlockingQueue<GameObj> recordingList= new LinkedBlockingQueue<>();
	ConcurrentHashMap<Integer, GameObj> recordingMap= new ConcurrentHashMap<>();
	
	public void setup() 
	{
		this.size(800, 400);
		this.frameRate(30);
		
		//coneccion al servidor
		try {
			conectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		record=false;
		Random rand = new Random();
		c= new GameObj(this, 50, 100, 50, 50);
		//c= new GameObj(this,50,this.height-50,50,50);
		c.setColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		c.setIDclient(this.hashCode());
		
		c.enableDrawable();
		c.enableMovable();
		//c.moveComponent.EnableKeyboardControl(true);
		c.moveComponent.EnableKeyboardControlTime(true);
		c.enableCollidable();
		c.collidableComponent.enableCollidableBeweenObjects();
		c.enableCollidableWithBoundaries();
		
	}
	 public void draw() 
	 {
		 clientTimeline.calculateTic();	
		 clientTimeline.setFinishTime(currentTimeMillis());
		 c.setLocalTimeline(clientTimeline);
		 c.moveComponent.Timedelta= (float)clientTimeline.ticSize;
		 background(24,20,100);
		 fill(255,0,0);
		 driveKeyboard();
		 
		 System.out.println("2 tamano de eventlist "+eventList2.size());
		 //exchange objects and events with the server
		 try {
			exchangeDataWithServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!retrieveList.isEmpty())
		{
			for(GameObj a: retrieveList)
			{ 
				if(a.IDclient== c.IDclient)
					c=a;
				if(a.drawableComponent!=null)
					a.updateInClient(this);
					
			}
		}

	 }
	 @SuppressWarnings("serial")
	 public void driveKeyboard()
	 {
		 /*EventHandler al= new EventHandler();
		 al.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_LEFT);
		 al.setPriority(1);
		 eventList2.add(al);*/
		
		 if(this.keyPressed)
		 {
			 /*EventHandler al2= new EventHandler();
			 al2.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
			 al2.setPriority(1);
			 eventList2.add(al2);*/
			 if(keyCode == RIGHT)
			 {
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
				 a.setPriority(1);
				 eventList2.add(a);
			 }
			 if(keyCode == LEFT)
			 {
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_LEFT);
				 a.setPriority(1);
				 eventList2.add(a);
				 //System.out.println("Se agrego "+a.toString());
				 //System.out.println("Ahora EventList size "+eventList.size());
			 }
			 if(this.key == ' ' && c.moveComponent.jumping != true)
				{
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_SPACEBAR);
				 a.setPriority(1);
				 eventList2.add(a);
				}
		 }
		 System.out.println("tamano de eventlist"+eventList2.size());
		 /*if(this.keyPressed)
		 {
			 int tim=currentTimeMillis();
			// System.out.println("tecla "+this.key);
			 if(keyCode == LEFT)
			 {
			 
				 
				// c.moveComponent.leftMove=true;
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_LEFT);
				 a.setPriority(1);
				 eventList.add(a);
				
				 
			 
			 }
			 
			 if(keyCode == RIGHT)
			 {
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
				 a.setPriority(1);
				 eventList.add(a);
				 
				 

			 }
			 if(this.key == ' ' && c.moveComponent.jumping != true)
				{
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_SPACEBAR);
				 a.setPriority(1);
				 eventList.add(a);
				
					
					
				}
		
		 }*/
	 }
	 public void keyPressed() 
	 {
			/* if(keyCode == RIGHT)
			 {
				 System.out.println("Se aplasto la derecha");
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
				 a.setPriority(1);
				 eventList.add(a);
			
			 
			 }*/
		 
	 }
	 
	 
	 @SuppressWarnings("unused")
	private void conectToServer() throws IOException
	 {
		 socket= new Socket(serverName, port);
		 out= new ObjectOutputStream(socket.getOutputStream());
		 in= new ObjectInputStream(socket.getInputStream());
	 }
	 
	 @SuppressWarnings("unchecked")
	private void exchangeDataWithServer() throws Exception
	 {
		 out.writeObject(c);
		 
		 System.out.println("3 tamano de eventlist "+eventList2.size());
		 out.writeInt(eventList2.size());
		 
		 for(EventHandler i: eventList2)
		 {
			 out.writeObject(i);
		 }
		 out.writeObject("t");
		 
		 //out.writeObject(eventList2);
		 eventList2.clear();
		 
		 retrieveList.clear();
		 retrieveList.addAll((LinkedList<GameObj>)in.readObject());
		 System.out.println("Size llega "+retrieveList.size());
		 System.out.println("Se completo el intercambio buno o malo");
		 //out.writeObject(eventList);
		 //eventList.clear();
	 }
	
	 
	
	 
	public static int currentTimeMillis() {
		    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		}
	
	public class ClientEventManager implements Runnable {
		PriorityBlockingQueue<EventHandler>QueueEvent= new PriorityBlockingQueue<>();
		boolean ok;
		
		@Override
		public void run() 
		{
			try
			{
				int count=0;
				while(true)
				{
					
					
					if(!QueueEvent.isEmpty())
					{
						if(replayb.get()==true)
						{
							//System.out.println("Events in the queue"+QueueEvent.toString());
							if(count==0)
								{Thread.sleep(1000);count=1;}
							
						}
						try{
							EventHandler ev= QueueEvent.peek();
							GameObj obj= QueueEvent.take().doEvent(recordingMap.get(ev.IDobject));
							recordingMap.replace(obj.IDclient, obj);
							if(replayb.get()==true)
							{
								Integer temp1= recTimeline.RecordTimeList.take();
								Integer temp2= recTimeline.RecordTimeList.peek();
								if(temp1!=null && temp2!=null)
								{
									double alfa= (float)(temp2-temp1)*replayfactor;
									Long l= new Long((long)alfa);
									Thread.sleep(l);
								}
								else
								{
									endRepC.set(true);
									
								}
							}
							}
							catch(Exception edcs)
							{edcs.printStackTrace();}
					
						//count ++;
					
					}
					
				}
			}
			catch(Exception ex)
			{
				
			}
			
		}
		public void addEventHandler(EventHandler e)
		{
			QueueEvent.put(e);
		}
		public void deleteEntireQue()
		{
			QueueEvent.clear();
		}

	}

	
	public class ServerEventManager implements Runnable {
		PriorityBlockingQueue<EventHandler>QueueEvent= new PriorityBlockingQueue<>();
		AtomicBoolean alloudToAddEvents= new AtomicBoolean(true);
		AtomicBoolean runbool= new AtomicBoolean(true);
		@Override
		public void run() 
		{
			try
			{
				int count=0;
				//while(true)
				//while(runbool.get()==true)
				while(true)
				{
					
					//System.out.print("hilo runbool esta :v "+serverManager.runbool.get());
					if(!QueueEvent.isEmpty())
					{
						if(replayb.get()==true)
						{
							//System.out.println("Events in the queue"+QueueEvent.toString());
							if(count==0)
								{Thread.sleep(1000);count=1;}
							
						}
						try{
							EventHandler ev= QueueEvent.peek();
							GameObj obj= QueueEvent.take().doEvent(recordingMap.get(ev.IDobject));
							recordingMap.replace(obj.ID, obj);
							if(replayb.get()==true)
							{
								Integer temp1= servRecTimeline.RecordTimeList.take();
								Integer temp2= servRecTimeline.RecordTimeList.peek();
								if(temp1!=null && temp2!=null)
								{
									double alfa= (float)(temp2-temp1)*replayfactor;
									Long l= new Long((long)alfa);
									Thread.sleep(l);
								}
								else
								{
									endRepS.set(true);
									//endPermit.set(true);
									//doServer.set(true);
									//replayb.set(false);
								}
							}
							}
							catch(Exception edcs)
							{edcs.printStackTrace();}
					
						//count ++;
					
					}
					
				}
			}
			catch(Exception ex)
			{
				
			}
			
		}
		public void addEventHandler(EventHandler e)
		{
			if(alloudToAddEvents.get()==true)
			{
			try{
			QueueEvent.put(e);
			}
			catch(Exception esd)
			{
				esd.printStackTrace();
			}
			}
		}
		public void ForceaddEventHandler(EventHandler e)
		{
			
			QueueEvent.put(e);
			
		}
		public void deleteEntireQue()
		{
			QueueEvent.clear();
		}

	}

}
