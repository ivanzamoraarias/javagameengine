package test.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import elements.com.GameObj;

public class MainTest {

	public static void main(String[] args) {
		
		//objeto
		GameObj game= new GameObj(100, 100, 50, 50, 30, 30);
		//transformacion a bytes
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] youtBytes= null;
		try {
			out= new ObjectOutputStream(bos);
			out.writeObject(game);
			out.flush();
			youtBytes= bos.toByteArray();
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
		
		
		//crear el objeto desde bytes
		ByteArrayInputStream bis = new ByteArrayInputStream(youtBytes);
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

	public class testThread implements Runnable
	{
		public testThread() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//objeto
			GameObj game= new GameObj(100, 100, 50, 50, 30, 30);
			//transformacion a bytes
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			byte[] youtBytes= null;
			try {
				out= new ObjectOutputStream(bos);
				out.writeObject(game);
				out.flush();
				youtBytes= bos.toByteArray();
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
			
			
			//crear el objeto desde bytes
			ByteArrayInputStream bis = new ByteArrayInputStream(youtBytes);
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
}
