package tong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

public class ChatThread extends Thread{
	public Socket socket;
	public String user;
	PrintWriter pw;
	BufferedReader br;

	public ChatThread(Socket socket){
		this.socket = socket;
		try {
			pw = new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public void authenticate(boolean isClient){
		/*Prompt to take client input for username and password*/
		while(!isClient){
			String pass = null;
			
			try {
				do{
					pw.println("Please enter your username: ");
					user = br.readLine();
				}while(user == null || user.equals(""));
				System.out.println(user);
				
				/*check if the client is login locked*/
				if(Server.dataBase.containsKey(user) 
						&& Server.dataBase.get(user)[4].equals("LOCK") 
						&& Server.dataBase.get(user)[2].equals(socket.getInetAddress().toString())){
					pw.println(user + " is still LOGIN LOCKED. Try later.");
					//drop the connection
					try {
						pw.println("Connection closed.");
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;//disconnect
				}
				
				/*check if the client is already login*/
				else if(Server.dataBase.containsKey(user)
						&& Server.dataBase.get(user)[1].equals("ONLINE")){
					pw.println(user + " is already login.");
					//drop the connection
					try {
						pw.println("Connection closed.");
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;//disconnect
				}
					
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			try {
				do{
					pw.println("Please enter your password: ");
					pass = br.readLine();
				}while(pass == null || pass.equals(""));
				System.out.println(pass);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			/*authenticate the client from user_pass.txt file*/
			if(Server.dataBase.containsKey(user) && Server.dataBase.get(user)[0].equals(pass))
				isClient = true;

			/*start to serve the client if authenticated*/
			if(isClient){
			
				pw.println("Welcome " + user + "! " + socket.getInetAddress());//welcome message
				/*record login status of the client*/
			    Server.dataBase.get(user)[1] = "ONLINE";//login status as online
//				System.out.println(Server.dataBase.get(user)[1]);
			    Server.dataBase.get(user)[2] = socket.getInetAddress().toString();//record ip address
//			    System.out.println(Server.dataBase.get(user)[2]);
			    Server.dataBase.get(user)[3] = "0";//reset wrong login times
//			    System.out.println(Server.dataBase.get(user)[3]);
			    Server.dataBase.get(user)[4] = "UNLOCK";//reset login lock
//			    System.out.println(Server.dataBase.get(user)[4]);
			    Server.onlineSockets.add(socket);//add the socket into online socket list
			    Server.zombieList.add(user);//add username into zombie list
			}
			else{
				/*if no such a user*/
				if(!Server.dataBase.containsKey(user))
					pw.println("Invalid username and/or password. Please try again");
				/*Wrong password. record the wrong trial time*/
				else{
					int failTimes = Integer.parseInt(Server.dataBase.get(user)[3]);
					if(failTimes == 2){//already failed times
						pw.println("Failed 3 times. Login LOCKED for 60 seconds.");
						Server.dataBase.get(user)[2] = socket.getInetAddress().toString();//record failed IP
						Server.dataBase.get(user)[4] = "LOCK";//login locked
						Timer timer = new Timer();  
						timer.schedule(new LockTimer(user), Server.BLOCK_TIME);
						 
						//drop the connection
						try {
							pw.println("Connection closed.");
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;//disconnect
					}
					else{
						pw.println("Invalid username and/or password. Please try again");
						failTimes++;
						Server.dataBase.get(user)[3] = failTimes + "";//increase wrong trial time
					}
				}
				
			}
		}
	}
	public void run(){
		
		boolean isClient = false;
		/*authenticate user*/
		this.authenticate(isClient);
		/*serve this client's commands if it is not closed*/
		if(!socket.isClosed()){
			String command = null;
			while(true){
				try {
					command = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(command.equals("logout")){
					pw.println("Bye " + user + "!");
					Server.onlineSockets.remove(socket);
					Timer timer = new Timer();
					timer.schedule(new LastLoginTimer(user), Server.LAST_HOUR);
					Server.dataBase.get(user)[1] = "OFFLINE";
					
					//drop the connection
					try {
						pw.println("Connection closed.");
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					while(true){
						System.out.println(Server.zombieList.size());
					}
//					break;//disconnect
				}
			}
		}
		
		
        
	}
}
