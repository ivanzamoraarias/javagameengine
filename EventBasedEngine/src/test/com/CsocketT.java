package test.com;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

import elements.com.EventHandler;
import elements.com.EventTypesEnum;
import elements.com.GameObj;

public class CsocketT {
	public CsocketT() {
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		Socket socket= new Socket("localhost", 8080);
		ObjectOutputStream out= new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in= new ObjectInputStream(socket.getInputStream());
	
		while(true)
		{
			System.out.println("-----------------Esta funcionando");
		Random r= new Random();
		out.writeUTF(":v "+r.nextInt(200));
		String s= in.readUTF();
		System.out.println("llega "+s);
		Thread.sleep(100);
		}
	}
	public static int currentTimeMillis() {
	    return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
	}

}
