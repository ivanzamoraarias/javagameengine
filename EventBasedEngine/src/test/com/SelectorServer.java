package test.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import elements.com.GameObj;

public class SelectorServer {

	public static void main(String[] args) throws Exception {
		// Get selector
				LinkedList<GameObj> listaObjetos = new LinkedList<>();
				Selector selector = Selector.open();

				System.out.println("Selector open: " + selector.isOpen());

				// Get server socket channel and register with selector
				ServerSocketChannel serverSocket = ServerSocketChannel.open();
				InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
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

					while (iter.hasNext()) {

						SelectionKey ky = iter.next();
						System.out.println("Acc "+ky.isAcceptable()+"  Read "+ky.isReadable()+" Write "+ky.isWritable()
								+" Valid "+ky.isValid()+" Conec "+ky.isConnectable());
						
						if(ky.isReadable() && ky.isWritable())
							continue;
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
						else if (ky.isReadable()) 
						{
							System.out.println("---- Readable");
							// Read the data from client
							GameObj game=null;
							try{
								SocketChannel client = (SocketChannel) ky.channel();
								ByteBuffer buffer = ByteBuffer.allocate(1000000);
								client.read(buffer);
								
								ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
								ObjectInputStream in = null;
								/*
								String output = new String(buffer.array()).trim();

								System.out.println("Message read from client: " + output);
								*/
								try {
									in = new ObjectInputStream(bis);
									//game=(GameObj)in.readObject();
									Object ob=in.readObject();
									
									if(ob instanceof GameObj)
										System.out.print("el objeto es instancia de Game");
									
									game= (GameObj)ob;
									listaObjetos.add(game);
									System.out.print("LLega "+game.toString());
									
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
						else if(ky.isWritable())
						{
							System.out.println("---- Writable");
							
							
							GameObj game= new GameObj(100, 100, 50, 50, 30, 30);
							System.out.print("envia "+game.toString());
							//transformacion a bytes
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ObjectOutput out = null;
							byte[] youtBytes= null;
							try {
								SocketChannel client = (SocketChannel) ky.channel();
								out= new ObjectOutputStream(bos);
								out.writeObject(game);
								out.flush();
								youtBytes= bos.toByteArray();
								ByteBuffer buffer = ByteBuffer.wrap(youtBytes);
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

						iter.remove();

					} // end while loop
					System.out.print("Lista "+listaObjetos.size());
				} // end for loop
	}
}
