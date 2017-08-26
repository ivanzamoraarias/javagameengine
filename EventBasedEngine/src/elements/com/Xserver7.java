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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import elements.com.Aserver8.GetCPUload;
import elements.com.Aserver8.GetHeapMemoryUsed;


public class Xserver7 {
	ConcurrentHashMap<Integer, GameObj> clientsMap = new ConcurrentHashMap<>();
	private static Object monitor= new Object();
	EventManagerT manager;
	int port=2223;
	
	float gameWorldHeigh;
	float gameWorldWidth;
	
	public static void main(String[] args) throws Exception {
		
		Thread t = new Thread(new GetCPUload());
		t.start();
		Thread t2= new Thread(new GetHeapMemoryUsed());
		t2.start();
		Xserver7 application= new Xserver7();
		application.setWolrdSize(800, 400);
		application.gameWorldCreation();
		application.runEventManager();

		application.selectorExchangeDataWithClient();
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
	public void selectorExchangeDataWithClient() throws IOException
	{
		Selector selector = Selector.open();
		System.out.println("Selector open: " + selector.isOpen());

		// Get server socket channel and register with selector
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress hostAddress = new InetSocketAddress(port);
		serverSocket.bind(hostAddress);
		serverSocket.configureBlocking(false);
		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);
		ByteBuffer buffer = ByteBuffer.allocate(1000000);

		for (;;) 
		{
			long startTime = System.currentTimeMillis();
			
			System.out.println("Waiting for select...");
			int noOfKeys = selector.select();

			System.out.println( "Number of selected keys: " + noOfKeys);

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();

			while (iter.hasNext()) 
			{
				SelectionKey ky = iter.next();
				System.out.println("Acc "+ky.isAcceptable()+"  Read "+ky.isReadable()+" Write "+ky.isWritable()
						+" Valid "+ky.isValid()+" Conec "+ky.isConnectable());

				/*if(ky.isReadable() && ky.isWritable())
					continue;*/
				if (ky.isAcceptable()) 
				{
					long startTimea = System.currentTimeMillis();
					System.out.println("------ Accept");
					// Accept the new client connection
					SocketChannel client = serverSocket.accept();
					client.configureBlocking(false);

					// Add the new connection to the selector
					//client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
					client.register(selector, SelectionKey.OP_READ);
					//System.out.println("Accepted new connection from client: " + client);
					long stopTimea = System.currentTimeMillis();
				    long elapsedTimea = stopTimea - startTime;
				    System.out.println("-*-*-*-*-*-*-* Time Acc "+elapsedTimea);
				}
				else 
				{
					if (ky.isReadable()) 
					{
						long startTimea = System.currentTimeMillis();
						System.out.println("---- Readable");

						try{
							SocketChannel client = (SocketChannel) ky.channel();
							//ByteBuffer buffer = ByteBuffer.allocate(1000000);
							//client.read(buffer);
							long bytesRead=client.read(buffer);
							buffer.clear();
							//System.out.println("bbbb  "+client.read(buffer));
							ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
							ObjectInputStream in = null;

							try {
								in = new ObjectInputStream(bis);
								System.out.print("Bites avayabe: "+in.available());
								//while(true)
								//{
									
									Object ob;
									try{
										ob = in.readObject();
									} catch (Exception e)
									{
										e.printStackTrace();
										break;
									}
									if(ob instanceof LinkedList)
									{
										//System.out.println("-----+++++---");
										LinkedList<Serializable>receivedList= (LinkedList<Serializable>)ob;
										for(Object o:receivedList)
										{
											if(o instanceof GameObj)
											{
												GameObj gob=(GameObj)o;
												insertLogic(gob);
											}
											else if(o instanceof EventHandler)
											{
												EventHandler a =(EventHandler)o;
												manager.addEventHandler(a);
												//System.err.println("Se tiene " + manager.QueueEvent.size() +" evento "+a.toString());
											}
											else if(o instanceof String)
											{
												//System.out.print("Se cierra el thread ");
												ky.cancel();
											}
										}
										//System.out.println("-----+++++---");
										
									}
									else if(ob instanceof String)
									{
										String s = (String)ob;
										if(s.equals("quit"))
										{
											ky.cancel();
											System.out.println("Se desconecto del canal :v");
										}
									}
									else if(ob instanceof GameObj)
									{
										//update logica
										GameObj gob=(GameObj)ob;
										insertLogic(gob);
									}
									else if(ob instanceof EventHandler)
									{
										//agregar handlers
										EventHandler a =(EventHandler)ob;
										manager.addEventHandler(a);
										//System.err.println("Se tiene " + manager.QueueEvent.size() +" evento "+a.toString());
									}
									else
									{
										System.out.print("no se ...");
									}


							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finally
							{
								ky.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
								try {
									if(in != null)
										in.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}
						catch( IOException ie2 ) 
						{ 
							System.out.println( ie2 ); 
						}
			
						long stopTimea = System.currentTimeMillis();
					    long elapsedTimea = stopTimea - startTimea;
					    System.out.println("-*-*-*-*-*-*-* Time Read "+elapsedTimea);
					}
					if(ky.isWritable())
					{
						long startTimeb = System.currentTimeMillis();
						System.out.println("---- Writable");

						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutput out = null;
						byte[] youtBytes= null;
						try {
							SocketChannel client = (SocketChannel) ky.channel();
							out= new ObjectOutputStream(bos);
							out.writeObject(new LinkedList<GameObj>(clientsMap.values()));
							out.flush();
							youtBytes= bos.toByteArray();
							ByteBuffer bufferW = ByteBuffer.wrap(youtBytes);
							//System.out.println("ByteBuffer:  "+bufferW.toString());
							client.write(bufferW);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
							ky.interestOps(SelectionKey.OP_READ);
							try {
								bos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//buffer.clear();
						long stopTimeb = System.currentTimeMillis();
					    long elapsedTimeb = stopTimeb - startTimeb;
					    System.out.println("-*-*-*-*-*-*-* Time Write "+elapsedTimeb);
					}
				}
				iter.remove();
			}
			checkForEventsInObjects();
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    System.out.println("-**-*-*-*-*-*-*-*-**-*-*-*-*--**-*-*-*-*-*-*-*-**-*-*-*\n"+
		    					"elapsetime Selector "+elapsedTime);
		}
	}
	public void checkForEventsInObjects()
	{
		for (Integer key: clientsMap.keySet())
		{
			clientsMap.get(key).updateInServer();
			if(clientsMap.get(key).objectEvent!=null)
				manager.addEventHandler(clientsMap.get(key).extractEvent());
		}
	}
	public void insertLogic(GameObj Gameclient)
	{
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
		}
		else
		{
			clientsMap.remove(Gameclient.IDclient);
			clientsMap.put(Gameclient.IDclient, Gameclient);
		}
	}
	
	public class EventManagerT implements Runnable {

		PriorityBlockingQueue<EventHandler>QueueEvent= new PriorityBlockingQueue<>();

		@Override
		public void run() {
			try
			{
				//int count=1;
				while(true)
				{
					if(!QueueEvent.isEmpty())
					{
						System.out.println("Si esta llena la Queue");
						EventHandler ev= QueueEvent.peek();
						GameObj o = clientsMap.get(ev.IDobject);
						GameObj obj= QueueEvent.take().doEvent(o);
						if(obj.IDclient!=0)
						{
							clientsMap.replace(obj.IDclient, obj);
						}
						else 
							clientsMap.replace(obj.ID, obj);
					}
					synchronized (monitor) {
						monitor.wait();
					}
				}
			}
			catch(InterruptedException ex)
			{
			}

		}
		public void addEventHandler(EventHandler e)
		{
			QueueEvent.put(e);
			synchronized (monitor) {
				monitor.notify();
			}
		}

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

				try(FileWriter fw = new FileWriter("/Users/ivanzamora/Desktop/serverCPUlocal.txt", true);
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

				try(FileWriter fw = new FileWriter("/Users/ivanzamora/Desktop/serverMemorylocal.txt", true);
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
