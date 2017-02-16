package test.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

import elements.com.GameObj;

public class SelectorClient {

	public static void main(String[] args) throws Exception {
		InetSocketAddress hostAddress = new InetSocketAddress("localhost", 5454);
		SocketChannel client = SocketChannel.open(hostAddress);
		//client.configureBlocking(false);
		System.out.println("Client sending messages to server...");

		// Send messages to server
		
		String [] messages = new String [] {"Time goes fast.", "What now?", "Bye."};

		Random rand = new  Random();
		
		for (int i = 0; i < messages.length; i++) 
		{
			if(i!=2)
			{
				System.out.println("-----------Write to server");
				
				GameObj game= new GameObj(rand.nextFloat(), 100, 50, 50, 30, 30);
				System.out.print("envia "+game.toString());
				//transformacion a bytes
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = null;
				byte[] youtBytes= null;
				
				out= new ObjectOutputStream(bos);
				out.writeObject(game);
				out.flush();
				youtBytes= bos.toByteArray();
				ByteBuffer buffer = ByteBuffer.wrap(youtBytes);
				client.write(buffer);
				
				
				/*byte [] message = new String(messages [i]).getBytes();
				ByteBuffer buffer = ByteBuffer.wrap(message);
				client.write(buffer);

				System.out.println(messages [i]);
				buffer.clear();
				*/
				//Thread.sleep(3000);
			}
			else
			{
				System.out.println("-----------Read tfrom server");
				
			
				
				
				//crear el objeto desde bytes
				GameObj game=null;
				ByteBuffer bb=ByteBuffer.allocate(1000000);
				client.read(bb);
				ByteArrayInputStream bis = new ByteArrayInputStream(bb.array());
				ObjectInputStream in = null;
				try {
					in = new ObjectInputStream(bis);
					game=(GameObj)in.readObject();
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
		}

		//client.close();		
	}
	
}
