package elements.com;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import processing.core.PApplet;

/*  
 * BIBLIOGRAPHY:
[1]creating dialogs in the client recording system: 
https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
[2] Enumeration for the EventTypesEnum was based on: 
https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html
[3] the server monitor is based in the answer from stack overflow forum: 
http://stackoverflow.com/questions/289434/how-to-make-a-java-thread-wait-for-another-threads-output
 * */
public class clientTest3 extends PApplet{

	TimeLine clientTimeline= new TimeLine();

	GameObj c;
	String serverName = "localhost";
    int port = 2223;
    int eveport=2224;
    LinkedList<GameObj>retrieveList= new LinkedList<>();
    LinkedList<EventHandler> eventList= new LinkedList<>();
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
		this.size(400, 400);
		this.frameRate(30);
		record=false;
		Random rand = new Random();
		c= new GameObj(this,50,this.height-50,50,50);
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
		if(endRepC.get()==true && endRepS.get()==true)
		{
			endPermit.set(true);
			doServer.set(true);
			replayb.set(false);
			endRepC.set(false);
			endRepS.set(false);
			record=false;
			replay=false;
			savedPos=null;
			savedPosMap= null;
			recordingMap.clear();
			System.out.println("return to the server");
			
		}
		 clientTimeline.calculateTic();	
		 clientTimeline.setFinishTime(currentTimeMillis());
		 c.setLocalTimeline(clientTimeline);
		 c.moveComponent.Timedelta= (float)clientTimeline.ticSize;
		 background(24,20,100);
		 fill(255,0,0);
		 driveKeyboard();
		 
		 //if(record== false && replay==false)
		 if(doServer.get()==true)
		 {
			 makeConectiontoSever();
			 if(!retrieveList.isEmpty())
			 {
				 for(GameObj a: retrieveList)
				 { 
					 //System.out.println("posx "+a.posX+" posy "+a.posY);
					 if(a.IDclient== c.IDclient)
						 c=a;
					 if(a.drawableComponent!=null)
						 a.updateInClient(this);
					 // a.drawObject(this);
				 }
			 }
		 }
		// if(record== true) 
		if( doServer.get()==false)
		 {
			// System.out.println("Entro al modo sin conexion");
			 playwithouthconectionAndRecord();
			 //first=true;
		 }
		
			
		
		
	 }
	 @SuppressWarnings("serial")
	 public void driveKeyboard()
	 {
		 if(this.keyPressed)
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
				 if(record)
				 {
					 recTimeline.addTimeStamp(tim);
					 clientManager.addEventHandler(a); 
				 }
				 
			 
			 }
			 
			 if(keyCode == RIGHT)
			 {
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
				 a.setPriority(1);
				 eventList.add(a);
				 if(record)
				 {
					 recTimeline.addTimeStamp(tim);
					 clientManager.addEventHandler(a); 
				 }
				 

			 }
			 if(this.key == ' ' && c.moveComponent.jumping != true)
				{
				 //System.out.println(currentTimeMillis());
				 EventHandler a= new EventHandler();
				 a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_SPACEBAR);
				 a.setPriority(1);
				 eventList.add(a);
				 if(record)
				 {
					 recTimeline.addTimeStamp(tim);
					 clientManager.addEventHandler(a); 
				 }
				 
					
					
				}
		
		 }
	 }
	 public void keyPressed() 
	 {
		 if(this.key=='r')
		 {
			
			 Object[] options = {"Yes",
	                    "No",
	                    "Cancel"};
			 int n = JOptionPane.showOptionDialog(frame,
					 "Would you like to record your gameplay? ",
							 "Recording Menu",
							 JOptionPane.YES_NO_CANCEL_OPTION,
							 JOptionPane.QUESTION_MESSAGE,
							 null,
							 options,
							 options[2]);
			//System.out.println("n "+n);
			if(n==0)
			{	
				recTimeline= new RecordTimeLine();
				servRecTimeline= new RecordTimeLine();
				record=true;
				doServer.set(false);
			}
		 }
		 if(this.key == 's' && record== true)
		 {
			 replay=true;
			 record=false;
			 eventList.toString();
			 Object[] options = {"1/2","1","2"};
			 int n = JOptionPane.showOptionDialog(frame,
					 "Would you like to record your gameplay? "
							 + "with that ham?",
							 "A Silly Question",
							 JOptionPane.YES_NO_CANCEL_OPTION,
							 JOptionPane.QUESTION_MESSAGE,
							 null,
							 options,
							 options[2]);
			 System.out.println("n "+n);
			 switch (n) {
			 	case 0:
			 		replayfactor=(float) 0.5;
				break;
				
			 	case 1:
					replayfactor=1;
				break;
				
			 	case 2:
					replayfactor=2;
				break;
					
			 	case -1:
				break;
				
			 	default:
				break;
				
			}
			
			 
			 for(Integer i: recordingMap.keySet())
			 {
				 if(recordingMap.get(i).moveComponent!= null )
					 recordingMap.get(i).moveComponent.stopsendingevents=true;
			 }
			 serverManager.alloudToAddEvents.set(false);
			 serverManager.deleteEntireQue();
			 for(Integer ds: savedPosMap.keySet() )
			 {
				 recordingMap.get(ds).position=savedPosMap.get(ds);
				 recordingMap.get(ds).updateInServer();
				 recordingMap.get(ds).updateInClient(this);
			 }
			clientManager.deleteEntireQue();
			recordingMap.get(c.IDclient).position[0]=savedPos[0];
			recordingMap.get(c.IDclient).position[1]=savedPos[1];
			recordingMap.get(c.IDclient).updateInServer();
			recordingMap.get(c.IDclient).updateInClient(this);
			System.out.println("Position after "+recordingMap.get(c.IDclient).position[0]+
					" "+recordingMap.get(c.IDclient).position[1]);
			replayb.set(true);
			for(EventHandler frd: eventList)
			{
				clientManager.addEventHandler(frd);
			}
			eventList.clear();
			System.out.println("Client manager queue "+clientManager.QueueEvent.toString());
			
			for(EventHandler frd: serveventList)
			{
				serverManager.ForceaddEventHandler(frd);
			}
			
			serveventList.clear();
			System.out.println("Server manager queue "+serverManager.QueueEvent.toString());
			
		 }
	 }
	 
	 @SuppressWarnings("unchecked")
	public void makeConectiontoSever()
	 {
			try
		      {

		         Socket socketConnection = new Socket(serverName, port);
		         

		         ObjectOutputStream clientOutputStream = new
		            ObjectOutputStream(socketConnection.getOutputStream());
		         ObjectInputStream clientInputStream = new 
		            ObjectInputStream(socketConnection.getInputStream());

		         clientOutputStream.writeObject(c);

		        // c= (GameObject)clientInputStream.readObject();
		         try{
		        	 retrieveList.clear();
		        	 retrieveList.addAll((LinkedList<GameObj>)clientInputStream.readObject());
		       // retrieveList=(LinkedList<GameObject>)clientInputStream.readObject();
		         }
		         catch(Exception e){
		        	 System.out.println(e);
		         }

		         clientOutputStream.close();
		         clientInputStream.close();
				
		        
		      }
			catch(Exception e){	}
			
			try{
			
					Socket socketConnection = new Socket(serverName, eveport);
		         

					ObjectOutputStream clientOutputStream = new
					ObjectOutputStream(socketConnection.getOutputStream());
					ObjectInputStream clientInputStream = new 
		            ObjectInputStream(socketConnection.getInputStream());

					
					clientOutputStream.writeObject(eventList);
					
					 clientOutputStream.close();
			         clientInputStream.close();
		
			}
			catch(Exception e)
		      {
		    	
		    	  
		      }

			eventList.clear();
		 
	 }
	 
	public void playwithouthconectionAndRecord()
	{
		//first step
		if(clientManager== null)
		{
			clientManager= new ClientEventManager();
			Thread man= new Thread(clientManager);
			man.start();
		}
		if(serverManager== null)
		{
			serverManager= new ServerEventManager();
			Thread man= new Thread(serverManager);
			man.start();
		}
		if(savedPos==null)
		{
			savedPos= new float[2];
			savedPos[0]= c.position[0];
			savedPos[1]= c.position[1];
		}
		if(savedPosMap== null)
		{
			savedPosMap= new ConcurrentHashMap<>();
			
		}
		if(recordingMap.isEmpty())
		{
			for(GameObj i: retrieveList)
			{	
				if(i.IDclient!=0)
				recordingMap.put(i.IDclient, i);
				else
					{
					//System.out.println("positisi que se guarda: "+"ID "+i.ID+" "+ i.position[0]+ " "+i.position[1]);
					savedPosMap.put(i.ID, i.position);
					recordingMap.putIfAbsent(i.ID, i);
					}
			}
		}
		
		//loop step
		
			for(Integer key : recordingMap.keySet())
			{
				if(recordingMap.get(key).objectEvent!= null)
				{
					int tim=currentTimeMillis();
					servRecTimeline.addTimeStamp(tim);
					EventHandler sds= recordingMap.get(key).extractEvent();
					serverManager.addEventHandler(sds);
					serveventList.add(sds);
				
				}
				recordingMap.get(key).updateInServer();
				if(recordingMap.get(key).drawableComponent != null)
				{
					recordingMap.get(key).updateInClient(this);
				}
			}
		
		
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
