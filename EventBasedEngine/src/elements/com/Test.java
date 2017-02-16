package elements.com;

import java.net.InetAddress;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import elements.com.serverTest3.EventManagerT;
import processing.core.PApplet;

public class Test extends PApplet{
	
	private float gameWorldHeigh=400;
	private float gameWorldWidth=400;
	private static int IDclient=(int) (Math.random()*100);// id aleatorio para cada cliente
	
	ConcurrentHashMap<Integer, GameObj> GameObjectsMap= new ConcurrentHashMap<>();
	
	///EventManagerT EventManager;
	LinkedBlockingQueue<EventHandler> EventHandelers= new LinkedBlockingQueue<>();
	
	private Thread manager=new Thread(new EventManagerT());
	
	int count=0;
	//by default object ID: 0 is the local player
	public void setup()
	{
		this.size((int)gameWorldWidth, (int)gameWorldHeigh);
		this.frameRate(30);
		manager.start();
	}
	
	public void draw()//main loop
	{
		background(24,20,100);
		if(count==0)
		{
			gameWorldCreation();
		}
		for (Entry<Integer, GameObj> entry : GameObjectsMap.entrySet())
		{
			if(entry.getValue().drawableComponent!=null)
				entry.getValue().updateInClient(this);
		}
		/*for (Entry<Integer, GameObj> entry : GameObjectsMap.entrySet())
		{
			entry.getValue().updateInServer();
        	if(entry.getValue().objectEvent!=null)
        	EventManager.addEventHandler(entry.getValue().extractEvent());	
		}*/
		count++;
		 
	}
	public void gameWorldCreation()
	{
		Random rand = new Random();
		GameObj c=new GameObj(this,50,this.height-50,50,50);
		c.setColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		c.setIDclient(this.hashCode());
		c.enableDrawable();
		c.enableMovable();
		//c.moveComponent.EnableKeyboardControl(true);
		c.moveComponent.EnableKeyboardControlTime(true);
		c.enableCollidable();
		c.collidableComponent.enableCollidableBeweenObjects();
		c.enableCollidableWithBoundaries();
		GameObjectsMap.put(	IDclient, c);
		
		// static platform
		GameObj platform1= new GameObj(gameWorldWidth, gameWorldHeigh, 
				280, (float)(gameWorldHeigh*0.75),80,30);
		platform1.setColor(255, 0, 0);
		platform1.enableDrawable();
		platform1.enableCollidable();
		platform1.collidableComponent.enableCollidableBeweenObjects();
		GameObjectsMap.put(platform1.ID, platform1);
		
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
		GameObjectsMap.put(Movingplatform.ID, Movingplatform);
		
		//dead zone
		GameObj deadZone= new GameObj(gameWorldWidth, gameWorldHeigh, 
				300, (float)(gameWorldHeigh*0.95),80,30);
		 //deadZone.setColor(0, 0, 255);
		 //deadZone.enableDrawable();
		 deadZone.enableCollidableAsDeadZone();
		 deadZone.collidableasdeadzone.enaleDeadZoneBehavior();
		 
		//dead zone1
		 GameObj deadZone1= new GameObj(gameWorldWidth, gameWorldHeigh, 
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
		
		GameObjectsMap.put(swapPoint.ID, swapPoint);
		GameObjectsMap.put(swapPoint1.ID, swapPoint1);
		GameObjectsMap.put(swapPoint2.ID, swapPoint2);
		GameObjectsMap.put(deadZone.ID, deadZone);
		GameObjectsMap.put(deadZone1.ID, deadZone1);
		
	}
	public void keyPressed()
	{
		System.out.println(keyCode);
		if(keyCode == LEFT)
		{
			EventHandler a= new EventHandler();
			 a.RegisterEvent(GameObjectsMap.get(IDclient), currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_LEFT);
			 a.setPriority(1);
			 EventHandelers.add(a);
		}
		
		if(keyCode == RIGHT)
		{
			EventHandler a= new EventHandler();
			 a.RegisterEvent(GameObjectsMap.get(IDclient), currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
			 a.setPriority(1);
			 EventHandelers.add(a);
		}
		
		if(this.key == ' ' && GameObjectsMap.get(0).moveComponent.jumping != true)
		{
		 //System.out.println(currentTimeMillis());
		 EventHandler a= new EventHandler();
		 a.RegisterEvent(GameObjectsMap.get(IDclient), currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_SPACEBAR);
		 a.setPriority(1);
		 EventHandelers.add(a);	
		}
	}
	
	public static int currentTimeMillis() {
	    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}
	
	public class EventManagerT implements Runnable {

		PriorityBlockingQueue<EventHandler>QueueEvent= new PriorityBlockingQueue<>();
		int count=0;
		long start;
		long end;
		
		@Override
		public void run() {
			
			try
			{
				
				while(true)
				{
					
					if(loopFrameOk())
					{
						addHandlerEntries();
						
						for(int i=0;i< Math.random()*QueueEvent.size()/2;i++)
						if(!QueueEvent.isEmpty())
						{
							EventHandler ev= QueueEvent.peek();
						
							GameObj obj= QueueEvent.take().doEvent(GameObjectsMap.get(ev.IDobject));
							if(obj.IDclient!=0)
								GameObjectsMap.replace(obj.IDclient, obj);
							else 
								GameObjectsMap.replace(obj.ID, obj);
						}
					}
					count ++;
					
					
				}
			}
			catch(InterruptedException ex)
			{
				System.out.println("Error en el Loop del Event Manager");
				ex.printStackTrace();
				
			}
			
		}
		private boolean loopFrameOk()
		{
			if(count==0)
				start = System.currentTimeMillis();
			
			end = System.currentTimeMillis();
			if((float)(end-start)/1000 >= (float)0.33)
			{
				count=0;
				return true;
			}
			return false;
		}
		public void addEventHandler(EventHandler e)
		{
			QueueEvent.put(e);
		}
		public void addHandlerEntries()
		{
			for (Entry<Integer, GameObj> entry : GameObjectsMap.entrySet())
			{
				entry.getValue().updateInServer();
	        	if(entry.getValue().objectEvent!=null)
	        	this.addEventHandler(entry.getValue().extractEvent());	
			}
			
		}

	}


}
