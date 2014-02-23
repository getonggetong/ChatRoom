package tong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
	public static final long LAST_HOUR = 60*60*1000; //an hour in milliseconds
	public static final long BLOCK_TIME = 60*1000; //60 secs in milliseconds
	public static final long TIME_OUT = 5000; //half an hour in milliseconds
	private static int PORT;//port number on server side
	public static ArrayList<String> onlineClients = new ArrayList<String>();//list of all online client clients
//	public static ArrayList<Socket> onlineSockets = new ArrayList<Socket>();//list of all online client clients
	public static ArrayList<String> zombieList = new ArrayList<String>();//list of names of online and less than one hour zombies
	private ServerSocket server;//server socket
	public static HashMap<String, String[]> dataBase;
	public static HashMap<String, PrintWriter> serverWriter = new HashMap<String, PrintWriter>();
	
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
					//userDataHash = {0 password, 1 online/offline, 2 ip, 3 wrongPasswordTimes, 4 LoginLock, 5 Block, 6 OfflineMsg}
					String[] userDataHash = {userDataToken[1], "OFFLINE", "", "0", "UNLOCK", "", ""};
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
