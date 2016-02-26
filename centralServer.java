import java.io.*;
import java.net.*;
import java.util.*;

public class centralServer
{

	private static Socket socket;
	private static HashMap<String,String> fileLoc;

	public static void main(String[] args)
	{
		try
		{   	
		
			fileLoc = new HashMap<String,String>();

			int port = 25000;
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port 25000");

			//Server is running always. This is done using this while(true) loop
			while(true)
			{
				//Reading the message from the client
				socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				String recvd = br.readLine();

				//System.out.println("Message received from client is: " + recvd);
				String[] com = recvd.split(" ");
				String returnMessage = new String("Bad command");

				if(com[0].equals(":up")){
					if(fileLoc.containsKey(com[1]))
						returnMessage = "File exists\n\n";
					else{
						fileLoc.put(com[1],socket.getInetAddress().toString());
						returnMessage = "Success\n\n";
					}
				}
				if(com[0].equals(":down")){
					if(fileLoc.containsKey(com[1])){
						fileLoc.remove(com[1]);
						returnMessage = "File deleted\n\n";
					}
					else{
						returnMessage = "File not found\n\n";
					}
				}
				if(com[0].equals(":ls")){
					Set<String> files = fileLoc.keySet();
					returnMessage = "";
					for (String s : files) {
    					returnMessage = returnMessage + s + " ";
					}
					returnMessage = returnMessage + "\n";
				}
				if(com[0].equals(":get")){
					if(fileLoc.containsKey(com[1])){
						returnMessage = "Success" + fileLoc.get(com[1]) + "\n";
					}
					else{
						returnMessage = "File not found\n\n";
					}
				}


				//Sending the response back to the client.
				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);

				bw.write(returnMessage);
				System.out.println(returnMessage);
				bw.flush();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch(Exception e){}
		}
	}
}
