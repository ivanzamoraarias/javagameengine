package elements.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class Server5 {

	int loopnumber;
	TimeLine GlobalServerTime;

	ConcurrentHashMap<Integer, GameObj> clientsMap = new ConcurrentHashMap<>();
	LinkedBlockingQueue<EventHandler> serverevents= new LinkedBlockingQueue<>();
	EventManagerT manager;

	boolean busy=false;
	boolean Evbusy=false;
	private static Object monitor= new Object();



	int IDServer;
	int port=2223;
	int portEvents;
	ServerSocket socketConnection;
	ServerSocket socketEvents;


	//game world facts
	float gameWorldHeigh;
	float gameWorldWidth;

	public static void main(String[] args) throws Exception {
		//port=2223;
		//int eveport= 2224;
		Server5 application= new Server5();
		application.setWolrdSize(800, 400);
		application.gameWorldCreation();
		application.runEventManager();

		application.selectorExchangeDataWithClient();
		//application.checkForEventsInObjects();

	}

	Server5()
	{
		IDServer= this.hashCode();
		loopnumber=0;
	}
	Server5(int i, int j)
	{
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
	public void selectorExchangeDataWithClient() throws IOException
	{
		Selector selector = Selector.open();
		System.out.println("Selector open: " + selector.isOpen());

		// Get server socket channel and register with selector
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
		serverSocket.bind(hostAddress);
		serverSocket.configureBlocking(false);
		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);

		for (;;) 
		{
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
					System.out.println("------ Accept");
					// Accept the new client connection
					SocketChannel client = serverSocket.accept();
					client.configureBlocking(false);

					// Add the new connection to the selector
					client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);

					System.out.println("Accepted new connection from client: " + client);
				}
				else 
				{
					if (ky.isReadable()) 
					{
						System.out.println("---- Readable");

						try{
							SocketChannel client = (SocketChannel) ky.channel();
							ByteBuffer buffer = ByteBuffer.allocate(1000000);
							//client.read(buffer);
							
							System.out.println("bbbb  "+client.read(buffer));
							ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
							ObjectInputStream in = null;

							try {
								in = new ObjectInputStream(bis);
								System.out.print("Bites avayabe: "+in.available());
								while(true)
								{
									
									Object ob;
									try{
										ob = in.readObject();
									} catch (Exception e)
									{
										e.printStackTrace();
										break;
									}

									if(ob instanceof GameObj)
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
										System.err.println("Se tiene " + manager.QueueEvent.size() +" evento "+a.toString());
									}
									else
									{
										System.out.print("no se ...");
									}

								}//end while
																//game=(GameObj)in.readObject();
								//System.out.print("LLega "+game.toString());

							} catch (Exception e) {
								// TODO Auto-generated catch block
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
						}
						catch( IOException ie2 ) 
						{ 
							System.out.println( ie2 ); 
						}
					}
					if(ky.isWritable())
					{
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
							ByteBuffer buffer = ByteBuffer.wrap(youtBytes);
							System.out.println("ByteBuffer:  "+buffer.toString());
							client.write(buffer);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finally
						{
							
							try {
								bos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				iter.remove();
			}
			checkForEventsInObjects();
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
			//System.out.println("Just added to clientsMap: " + Gameclient.IDclient);

		}
		else
		{

			clientsMap.remove(Gameclient.IDclient);
			clientsMap.put(Gameclient.IDclient, Gameclient);

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
						//System.err.println("peek "+ev.ID);
						GameObj o = clientsMap.get(ev.IDobject);
						GameObj obj= QueueEvent.take().doEvent(o);
						//GameObj obj= QueueEvent.take().doEvent(clientsMap.get(ev.IDobject));
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
