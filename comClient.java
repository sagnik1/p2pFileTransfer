import java.io.*;
import java.net.*;
import java.util.*;

public class comClient
{

	private static Socket socket;

	private static class SimpleFileClient {

		
		public void getFile (String destIP, String filename, int port ) throws IOException {
			int SOCKET_PORT = port;      // you may change this
			String SERVER = destIP.trim();  // localhost
			String FILE_TO_RECEIVE = filename+"DL";  // you may change this, I give a
			// different name because i don't want to
			// overwrite the one used by server...

			//Send filename to destination File Server


			int FILE_SIZE = 6022386; // file size temporary hard coded
													// should bigger than the file to be downloaded
			int bytesRead;
			int current = 0;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			Socket sock = null;
			try {
				sock = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Connecting...");

				//Send filename to file server
				OutputStream os = sock.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);

				bw.write(filename+"\n");
				bw.flush();
				
				// receive file
				byte [] mybytearray  = new byte [FILE_SIZE];
				InputStream is = sock.getInputStream();
				fos = new FileOutputStream(FILE_TO_RECEIVE);
				bos = new BufferedOutputStream(fos);
				bytesRead = is.read(mybytearray,0,mybytearray.length);
				current = bytesRead;

				do {
					bytesRead =
						is.read(mybytearray, current, (mybytearray.length-current));
					if(bytesRead >= 0) current += bytesRead;
				} while(bytesRead > -1);

				bos.write(mybytearray, 0 , current);
				bos.flush();
				System.out.println("File " + FILE_TO_RECEIVE
						+ " downloaded (" + current + " bytes read)");
			}
			finally {
				if (fos != null) fos.close();
				if (bos != null) bos.close();
				if (sock != null) sock.close();
			}
		}

	}
	public static void main(String args[]){
		try{
			if(args.length!=2)
				System.exit(1);
			String host = "localhost";
			int port = 25000;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);
			socket.setSoTimeout(1000); 			


			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);

			bw.write(args[0]+' '+args[1]+'\n');
			bw.flush();
			System.out.println("Message sent to the server : " + args[0] + ' ' + args[1]);
			//Get the return message from the server
			try{	
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String message = br.readLine();
				System.out.println("Message received from the central server : \n" + message);

				if(args[0].equals(":get")){
						if(message.startsWith("S")){
							String dest = message.split("/")[1];
							SimpleFileClient sfc = new SimpleFileClient();
							sfc.getFile(dest,args[1],13267);
						}
				}
			}
			catch(SocketTimeoutException e){
				System.out.println("Packet Lost");
				System.out.println();
			}
		}
		catch (Exception exception){
				exception.printStackTrace();
		}
		finally{
			//Closing the socket
			try{
					socket.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
