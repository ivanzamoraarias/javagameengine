package test.com;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elements.com.EventHandler;

public class SsocketT {

	LinkedBlockingQueue<SocketDataStructure>socketsList= new LinkedBlockingQueue<>();
	
	int port=8080;
	
	ServerSocketChannel ssc;
	ServerSocket ss;
	Selector selector;
	
	ObjectInputStream in;
	ObjectOutputStream out;
	
	public SsocketT() {
		// TODO Auto-generated constructor stub
		
	}
	public void initialConection() throws Exception
	{
		ssc= ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ss= ssc.socket();
		ss.bind(new InetSocketAddress(port));
		selector= Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
	}
	public void conectionLoop() throws Exception
	{
		while(true)
		{
			System.out.println("-----------------Esta funcionando");
			int num = selector.select();
			if(num==0)
			{
				continue;
			}
			Set keys = selector.selectedKeys();
			Iterator it = keys.iterator();
			while(it.hasNext())
			{
				SelectionKey key =(SelectionKey)it.next();
				
				 if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==SelectionKey.OP_ACCEPT) 
				 {
					 System.out.println( "------accept operation--------" );
					 Socket s = ss.accept();
					 System.out.println( "----------Got connection from "+s );
					 SocketChannel sc = s.getChannel();
			         sc.configureBlocking( false );
			         sc.register( selector, SelectionKey.OP_READ );
				 }
				 else if ((key.readyOps() & SelectionKey.OP_READ) ==SelectionKey.OP_READ) 
				 {
					 System.out.println("-----------------Read");
					 /*SocketChannel sc = null;
					 sc = (SocketChannel)key.channel();
					 ObjectInputStream ins= new ObjectInputStream(sc.socket().getInputStream());
					 */

				 }else if ((key.readyOps() & SelectionKey.OP_WRITE) ==SelectionKey.OP_WRITE) 
				 {
					 System.out.println("-----------------Write");
					 /*SocketChannel sc = null;
					 sc = (SocketChannel)key.channel(); 
				 	*/
				 }
			}
		}
	}
	public static void main(String[] args) throws Exception {
		SsocketT app = new SsocketT();
		app.initialConection();
		app.conectionLoop();
	}
	/*public static void main(String[] args) throws Exception 
	{
		SsocketT app= new SsocketT();
		app.runAcceptThread();
		while(true)
		{
			System.out.println("Tamano "+ app.socketsList.size());
		}
		
	}*/
	public void runAcceptThread()
	{
		Thread a = new Thread(new AcceptSocketThread());
		a.start();
	}
	public class SocketDataStructure
	{
		Socket socket;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		public SocketDataStructure(Socket s) 
		{
			socket=s;
			try {
				in = new ObjectInputStream(s.getInputStream());
				out = new ObjectOutputStream(s.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public class AcceptSocketThread implements Runnable
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true)
			{
				try {
					Socket socket = ss.accept();
					socketsList.add(new SocketDataStructure(socket));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
