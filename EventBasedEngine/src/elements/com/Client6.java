package elements.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Random;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class Client6 extends PApplet{

	SocketChannel client;

	TimeLine clientTimeline= new TimeLine();

	GameObj c;
	String serverName = "localhost";
	int port = 2223;
	int eveport=2224;
	LinkedList<GameObj>retrieveList= new LinkedList<>();
	LinkedList<EventHandler> eventList2= new LinkedList<>();
	LinkedList<EventHandler> serveventList= new LinkedList<>();

	//output variables
	ByteArrayOutputStream bos;
	ObjectOutput out = null;
	byte[] youtBytes= null;
	ByteBuffer buffer;
	
	//private static Object monitor= new Object();

	public void setup() 
	{
		this.size(800, 400);
		this.frameRate(60);

		//initiate output variables
		try {
			initializeOutputVariables();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//coneccion al servidor
		try {
			conectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			sendListOfSerializableObjects(serializableObjectsList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----------------Se cierra--------------++++++++++++++*********");
	}
	public void driveKeyboard()
	{
		/*EventHandler ab= new EventHandler();
		 ab.RegisterEvent(c, currentTimeMillis(), EventTypesEnum.KEYBOARD_PRESS_RIGHT);
		 ab.setPriority(1);
		 eventList2.add(ab);*/

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

	}
	//@SuppressWarnings("unused")
	private void conectToServer() throws IOException
	{
		InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
		client = SocketChannel.open(hostAddress);
		System.out.println("Conectado");
		//socket= new Socket(serverName, port);
		//out= new ObjectOutputStream(socket.getOutputStream());
		//in= new ObjectInputStream(socket.getInputStream());
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
	//@SuppressWarnings("finally")
	private LinkedList<GameObj> readList()
	{
		Object ob=null;
		LinkedList<GameObj>lis=null;
		ByteBuffer bb=ByteBuffer.allocate(1000000);
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
					//LinkedList<GameObj>lis=(LinkedList<GameObj>)ob;
					lis=(LinkedList<GameObj>)ob;
					//return lis;
				}
				catch(Exception e)
				{
					System.out.println("No es una lista");
					//return null;
				}

			} catch (Exception e) {
				System.out.println("problemas al convertir");
				System.out.println("ByteBuffer:  "+bb.toString());
				e.printStackTrace();
			}
			finally
			{
				try {
					if(in != null)
						in.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lis;
		//return null;
	}
	private void sendListOfSerializableObjects(LinkedList<Serializable>list) throws IOException
	{
		/*out.writeObject(list);
		out.flush();
		youtBytes= bos.toByteArray();
		buffer = ByteBuffer.wrap(youtBytes);
		client.write(buffer);
		*/
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

	private void initializeOutputVariables() throws IOException
	{
	
		bos = new ByteArrayOutputStream();
		out = new ObjectOutputStream(bos);
		//byte[] youtBytes= null;
	}
	private void sendObjects(Object obj) throws IOException
	{
		/*out.writeObject(obj);
		out.flush();
		youtBytes= bos.toByteArray();
		buffer = ByteBuffer.wrap(youtBytes);
		client.write(buffer);
		*/
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


	public static int currentTimeMillis() {
		return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}


}

