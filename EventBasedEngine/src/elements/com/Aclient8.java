package elements.com;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Random;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;


import processing.core.PApplet;

public class Aclient8  extends PApplet{
	SocketChannel client;

	TimeLine clientTimeline= new TimeLine();

	GameObj c;
	String serverName = "localhost";
	int port = 2223;
	LinkedList<GameObj>retrieveList= new LinkedList<>();
	LinkedList<EventHandler> eventList2= new LinkedList<>();
	
	ByteBuffer bb;
	
	static int identificador=(int) System.currentTimeMillis();
	
	public void setup() 
	{
		Thread t = new Thread(new GetCPUload());
		t.start();
		Thread t2= new Thread(new GetHeapMemoryUsed());
		t2.start();
		this.size(800, 400);
		this.frameRate(60);

		//inicio del buffer
		bb=ByteBuffer.allocate(1000000);
		//coneccion al servidor
		try {
			conectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random rand = new Random();
		//identificador=rand.nextInt(100);
		c= new GameObj(this, 50, 100, 50, 50);
		//c= new GameObj(this,50,this.height-50,50,50);
		c.setColor(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
		c.setIDclient(this.hashCode());

		c.enableDrawable();
		c.enableMovable();
	
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
		//c.moveComponent.Timedelta=20;
		background(24,20,100);
		fill(255,0,0);
		driveKeyboard();

		System.out.println("2 tamano de eventlist "+eventList2.size());
		
		
		//exchange objects and events with the server
		long startTime1 = System.currentTimeMillis();
		try {
			//exchangeDataWithServer();
			selectorExchangeDataWithServer();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		long stopTime1 = System.currentTimeMillis();
		long elapsedTime = stopTime1 - startTime1;
		
		long startTime2 = System.currentTimeMillis();
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
		long stopTime2 = System.currentTimeMillis();
		long elapsedTime2 = stopTime2 - startTime2;
		
		System.out.println("-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-\n"+
				"Delta del gloop "+clientTimeline.ticSize+
				"\nTime selectorMethod"+ elapsedTime +
				"\nTime checkandupdate"+ elapsedTime2 );
	}
	public void stop()
	{
		String s= "quit";
		LinkedList<Serializable>serializableObjectsList= new LinkedList<>();
		serializableObjectsList.add(s);
		try {
			sendObjects(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----------------Se cierra--------------++++++++++++++*********");
	}
	private void conectToServer() throws IOException
	{
		InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
		client = SocketChannel.open(hostAddress);
		System.out.println("Conectado");
	}
	public void driveKeyboard()
	{
		if(this.keyPressed)
		{

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
			}
			if(this.key == ' ' && c.moveComponent.jumping != true)
			{
				EventHandler a= new EventHandler();
				a.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_SPACEBAR);
				a.setPriority(1);
				eventList2.add(a);
			}
		}
		System.out.println("tamano de eventlist"+eventList2.size());

	}
	private void selectorExchangeDataWithServer() throws IOException
	{
		// write to server
		LinkedList<Serializable>serializableObjectsList= new LinkedList<>();
		serializableObjectsList.add(c);
		serializableObjectsList.addAll(eventList2);
		sendListOfSerializableObjects(serializableObjectsList);

		eventList2.clear();

		// read from server
		retrieveList.clear();
		retrieveList.addAll(readList());
		
		//sendObjects("quit");


	}
	private void sendListOfSerializableObjects(LinkedList<Serializable>list) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] youtBytes= null;

		out= new ObjectOutputStream(bos);
		out.writeObject(list);
		out.flush();
		youtBytes= bos.toByteArray();
		ByteBuffer buffer = ByteBuffer.wrap(youtBytes);
		client.write(buffer);
	}
	private void sendObjects(Object obj) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] youtBytes= null;

		out= new ObjectOutputStream(bos);
		out.writeObject(obj);
		out.flush();
		youtBytes= bos.toByteArray();
		ByteBuffer buffer = ByteBuffer.wrap(youtBytes);
		client.write(buffer);
	}
	private LinkedList<GameObj> readList()
	{
		Object ob=null;
		LinkedList<GameObj>lis=null;
		
		try {
			client.read(bb);
			System.out.println("ByteBuffer:  "+bb.toString());
			ByteArrayInputStream bis = new ByteArrayInputStream(bb.array());
			ObjectInputStream in = null;

			try {
				in = new ObjectInputStream(bis);

				ob= in.readObject();
				try
				{

					lis=(LinkedList<GameObj>)ob;
				
				}
				catch(Exception e)
				{
					System.out.println("No es una lista");
				
				}

			} catch (Exception e) {
				System.out.println("problemas al convertir");
				System.out.println("ByteBuffer:  "+bb.toString());
				e.printStackTrace();
			}
			finally
			{
				bb.clear();
				try {
					if(in != null)
						in.close();

				} catch (IOException e) {
					e.printStackTrace();

				}


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lis;
		//return null;
	}
	public static int currentTimeMillis() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}
	public static class GetCPUload implements Runnable
	{
		
		public GetCPUload() {
			
		   
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while(true)
			{
				String s=LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute()+":"+
									LocalDateTime.now().getSecond()+":"+LocalDateTime.now().getNano();
				
				String sm= System.currentTimeMillis()+"";
				 //System.out.println(LocalDateTime.now().getHour());       // 7
				   // System.out.println(LocalDateTime.now().getMinute());     // 45
				    //System.out.println(LocalDateTime.now().getSecond());     // 32
				   // System.out.println(LocalDateTime.now().getNano());

				try(FileWriter fw = new FileWriter("/Users/ivanzamora/Desktop/clientCPUlocal"
						+identificador+ ".txt", true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
						
					    out.println(s+" , "+sm+" , "+ getProcessCpuLoad());
					    //more code
					    
					} catch (Exception e) {
					    //exception handling left as an exercise for the reader
						System.out.println("No funciona");
					}
				
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			}
		}
		
		public double getProcessCpuLoad() throws Exception {

		    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		    if (list.isEmpty())     return Double.NaN;

		    Attribute att = (Attribute)list.get(0);
		    Double value  = (Double)att.getValue();

		    // usually takes a couple of seconds before we get real values
		    if (value == -1.0)      return Double.NaN;
		    // returns a percentage value with 1 decimal point precision
		    return ((int)(value * 1000) / 10.0);
		}
		
	}
	public static class GetHeapMemoryUsed implements Runnable
	{
		public GetHeapMemoryUsed() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while(true)
			{
				String s=LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute()+":"+
									LocalDateTime.now().getSecond()+":"+LocalDateTime.now().getNano();
				
				String sm= System.currentTimeMillis()+"";
				 //System.out.println(LocalDateTime.now().getHour());       // 7
				   // System.out.println(LocalDateTime.now().getMinute());     // 45
				    //System.out.println(LocalDateTime.now().getSecond());     // 32
				   // System.out.println(LocalDateTime.now().getNano());

				try(FileWriter fw = new FileWriter("/Users/ivanzamora/Desktop/clientMemorylocal"
						+identificador+ ".txt", true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
						
					    out.println(s+" , "+sm+" , "+ getMemory());
					    //more code
					    
					} catch (Exception e) {
					    //exception handling left as an exercise for the reader
						System.out.println("No funciona");
					}
				
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			}
		}
		
		public double getMemory() throws Exception {

			double value=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		    return value;
		}
		
	}

}
