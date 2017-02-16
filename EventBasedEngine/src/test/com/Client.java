package test.com;

//$Id$

import java.io.*;
import java.net.*;
import java.util.*;

public class Client implements Runnable
{
private String host;
private int port;

// Bounds on how much we write per cycle
private static final int minWriteSize = 1024;
private static final int maxWriteSize = 65536;

// Bounds on how long we wait between cycles
private static final int minPause = (int)( 0.05 * 1000 );
private static final int maxPause = (int)( 0.5 * 1000 );

// Random number generator
Random rand = new Random();

public Client( String host, int port, int numThreads ) {
 this.host = host;
 this.port = port;

 for (int i=0; i<numThreads; ++i) {
   new Thread( this ).start();
 }
}

public void run() {

 byte buffer[] = new byte[maxWriteSize];

 try {
   Socket s = new Socket( host, port );

   InputStream in = s.getInputStream();
   OutputStream out = s.getOutputStream();

   while (true) {
     int numToWrite = minWriteSize +
       (int)(rand.nextDouble() * (maxWriteSize-minWriteSize));

     for (int i=0; i<numToWrite; ++i) {
       buffer[i] = (byte)rand.nextInt( 256 );
     }

     out.write( buffer, 0, numToWrite );
     int sofar = 0;
     while (sofar < numToWrite) {
       sofar += in.read( buffer, sofar, numToWrite-sofar );
     }

System.out.println( Thread.currentThread()+" wrote "+numToWrite );

     int pause = minPause +
       (int)(rand.nextDouble() * (maxPause-minPause));
     try { Thread.sleep( pause ); } catch( InterruptedException ie ) {}
   }
 } catch( IOException ie ) {
   ie.printStackTrace();
 }
}

static public void main( String args[] ) throws Exception {
 //String host = args[0];
 //int port = Integer.parseInt( args[1] );
 //int numThreads = Integer.parseInt( args[2] );
 String host="localhost";
 int port = 3030;
 int numThreads = 2;
 new Client( host, port, numThreads );
}
}
