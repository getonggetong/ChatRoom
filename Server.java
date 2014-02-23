package tong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
	public static final long LAST_HOUR = 60*60*1000; //an hour in milliseconds
	public static final long BLOCK_TIME = 60*1000; //60 secs in milliseconds
	private static int PORT;//port number on server side
	private ArrayList<Socket> onlineSockets = new ArrayList<Socket>();//list of all online client sockets
	private ArrayList<String> ZombieList = new ArrayList<String>();//list of names of online and less than one hour zombies
	private ServerSocket server;//server socket
	public static HashMap<String, String[]> dataBase;
	
	
	public Server(){
		try {
			
			server = new ServerSocket(PORT);
			
			/*scan user_pass.txt to store user information into HashMap*/
			dataBase = new HashMap<String, String[]>();
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream("user_pass.txt")));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String userData = null;
			
			try {
				while((userData = reader.readLine())!=null)
				{
					String[] userDataToken = userData.split(" ");
					//userDataHash = {0 password, 1 online/offline, 2 ip, 3 wrongPasswordTimes, 4 LoginLock, 5 BeBlockedBy}
					String[] userDataHash = {userDataToken[1], "offline", "", "0", "UNLOCK", ""};
					dataBase.put(userDataToken[0], userDataHash);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			reader.close();
			System.out.println("Server on------------------------");
			
			while(true){
				Socket clientSocket = server.accept();//waiting for clients
				onlineSockets.add(clientSocket);
				new ChatThread(clientSocket).start();//new thread for a new client
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		PORT = Integer.parseInt(args[0]);
		new Server();
	}
}
