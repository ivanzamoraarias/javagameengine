package elements.com;
import java.io.Serializable;


public class EventHandler implements Comparable<EventHandler>, Serializable{
	

	EventTypesEnum type;
	int timeStamp;
	int subdivisions;
	int priority;
	int age;
	
	int ID= this.hashCode();
	public int IDobject;
	
	Thread eventProcess;
	serializableRunnable eventP;
	//Runnable eventR;
	float[] posInfo;
	
	public EventHandler() {
		timeStamp=0;
		subdivisions=0;
		priority=0;
		age=0;
	}

	EventHandler(int ts, int prio) {
		timeStamp=ts;
		priority=prio;
		
	}
	EventHandler(int ts, int subd, int prio, int age) {
		
	}
	public void setTimeStamp(int i)
	{
		timeStamp=i;
	}
	public void setPriority(int i)
	{
		priority=i;
	}
	public void setIDHandler(int i)
	{
		//ID
	}
	public void RegisterEvent(serializableRunnable st)
	{
		eventP=st;
	}

	public void RegisterEvent(GameObj c, EventTypesEnum et, float x, float y)
	{
		IDobject=c.ID;
		 type= EventTypesEnum.AUTOMATIC_PATTERN;
		 posInfo= new float[2];
		 posInfo[0]=x;
		 posInfo[1]=y;
	}
	
	public void RegisterEvent(GameObj c, EventTypesEnum et, float xi, float yi , float xf, float yf)
	{
		
	}
	
	public void RegisterEvent(GameObj c, int time, EventTypesEnum et)
	{
		IDobject= c.IDclient;
		type= et;
		timeStamp=time;
	}
	public GameObj doEvent(GameObj c)
	{
		if(IDobject == c.IDclient)
		{
			if(type.equals(EventTypesEnum.KEYBOARD_PRESS_LEFT))
			{
				c.moveComponent.leftMove=true;
			}
			if(type.equals(EventTypesEnum.KEYBOARD_PRESS_RIGHT))
			{
				 c.moveComponent.rightMove=true;
			}
			if(type.equals(EventTypesEnum.KEYBOARD_PRESS_SPACEBAR))
			{
				c.moveComponent.jumping=true;
				 c.moveComponent.up=true;
			}
		}
		else if(IDobject== c.ID)
		{
			if(type.equals(EventTypesEnum.AUTOMATIC_PATTERN))
			{
				//System.out.println(c.ID+" antes pos "+ c.position[0]+ " "+ c.position[1]);
				c.position[0] = c.position[0] + posInfo[0];
				c.position[1] = c.position[1] + posInfo[1];
			}
		}
		
		return c;
		
	}
	public void runEvent()
	{
		//seventProcess= new Thread(eventP);
		try
		{
			eventProcess= new Thread(eventP);
			eventProcess.start();
		}
		catch(Error er)
		{
			System.out.println("Problem with the thread"+er);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String s=""+this.IDobject+" "+this.type ;
		//return super.toString();
		return s;
	}

	@Override
	public int compareTo(EventHandler o) {
		// TODO Auto-generated method stub
		
		if(this.timeStamp!=0 && o.timeStamp!=0)
		{
			if(o.timeStamp== this.timeStamp)
				return 0;
				
				if(this.timeStamp<o.timeStamp)
					return -1;
				
				if(this.timeStamp>o.timeStamp)
					return 1;
		}
		else
		{
			if(o.priority== this.priority)
				return 0;
			if(this.priority>o.priority)
				return 1;
			if(this.priority<o.priority)
				return -1;
		}
		
		
		return 0;
	}
	

}
