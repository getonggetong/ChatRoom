package tong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
					pw.println("==========\n>>>>>Please enter your username:");
					user = br.readLine();
				}while(user == null || user.equals(""));
				System.out.println(user);
				
				/*check if the client is login locked*/
				if(Server.dataBase.containsKey(user) 
						&& Server.dataBase.get(user)[4].equals("LOCK") 
						&& Server.dataBase.get(user)[2].equals(socket.getInetAddress().toString())){
					pw.println("==========\n>>>>>" + user + " is still LOGIN LOCKED. Try later.");
					//drop the connection
					try {
						pw.println("==========\n>>>>>Connection closed.");
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
					pw.println("==========\n>>>>>" + user + " is already login.");
					//drop the connection
					try {
						pw.println("==========\n>>>>>Connection closed.");
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
					pw.println("==========\n>>>>>Please enter your password:");
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
			
				pw.println("==========\n>>>>>Welcome " + user + "! " + socket.getInetAddress());//welcome message
				/*record login status of the client*/
			    Server.dataBase.get(user)[1] = "ONLINE";//login status as online
//				System.out.println(Server.dataBase.get(user)[1]);
			    Server.dataBase.get(user)[2] = socket.getInetAddress().toString();//record ip address
//			    System.out.println(Server.dataBase.get(user)[2]);
			    Server.dataBase.get(user)[3] = "0";//reset wrong login times
//			    System.out.println(Server.dataBase.get(user)[3]);
			    Server.dataBase.get(user)[4] = "UNLOCK";//reset login lock
//			    System.out.println(Server.dataBase.get(user)[4]);
			    Server.onlineClients.add(user);//add the client into online list
//			    Server.onlineSockets.add(socket);//add the socket into online list
			    Server.zombieList.add(user);//add username into zombie list
			    Server.serverWriter.put(user, pw);//add the PrintWriter of this socket
			    pw.println(Server.dataBase.get(user)[6]);//send all offline messages
			    Server.dataBase.get(user)[6] = "";//reset offline messages buffer
			}
			else{
				/*if no such a user*/
				if(!Server.dataBase.containsKey(user))
					pw.println("==========\n>>>>>Invalid username and/or password. Please try again");
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
							pw.println("==========\n>>>>>Connection closed.");
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;//disconnect
					}
					else{
						pw.println("==========\n>>>>>Invalid username and/or password. Please try again");
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
				pw.println("==========\n>>>>>Command:");
				try {
					command = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(command != null){
					/*logout this client*/
					if(command.equals("logout")){
						logout();
						break;//disconnect
					}
					/*who is currently online*/
					else if(command.equals("whoelse")){
						whosOnList(Server.onlineClients);
					}
					/*who logged in in last hour*/
					else if(command.equals("wholasthr")){
						whosOnList(Server.zombieList);
					}
					/*broadcast*/
					else if(command.split(" ")[0].equals("broadcast")){
						broadcast(Server.serverWriter, command);
					}
					/*private message*/
					else if(command.split(" ")[0].equals("message")){
						privateMsg(Server.serverWriter, Server.dataBase, command);
					}
					/*block clients*/
					else if(command.split(" ")[0].equals("block")){
						block(Server.dataBase, command);
					}
					/*unblock clients*/
					else if(command.split(" ")[0].equals("unblock")){
						unblock(Server.dataBase, command);
					}
					else{
						pw.println("\"" + command + "\"" + "Command Not Found");
					}
				}
			}
		}
	}
	/*unblock clients*/
	public void unblock(HashMap<String, String[]> dataBase, String command){
		String newBlockList = "";//new blocklist after unblock
		if(command.split(" ").length < 2)//error if command is too short
			pw.println("==========\n>>>>>No target client.");
		else{
			String targetUser = command.split(" ")[1];
			/*error if client unblocks himself*/
			if(targetUser.equals(user))
				pw.println("==========\n>>>>>Error! You cannot unblock yourself!");
			else{
				String[] blockList = dataBase.get(user)[5].split(" ");
				boolean isBlocked = false;
				/*iterate the blocklist to check if target user is blocked*/
				for(int i = 0; i < blockList.length; i++){
					/*remove target user from block list if exists*/
					if(blockList[i].equals(targetUser)){
						isBlocked = true;
					}
					/*add old blocked users into new blocklist if not unblocked*/
					else{
						newBlockList = newBlockList.concat(blockList[i] + " ");
					}
				}
				
				
				/*error if target user is not currently blocked*/
				if(!isBlocked){
					pw.println("==========\n>>>>>Error! " + targetUser + " is not blocked!");
				}
				else{
					pw.println("==========\n>>>>>You have successfully unblocked " +targetUser + ".");
					dataBase.get(user)[5] = newBlockList;//update blocklist for current client
				}
			}
		}
	}
	/*block clients from sending current client messages*/
	public void block(HashMap<String, String[]> dataBase, String command){
		if(command.split(" ").length < 2)//error if command is too short
			pw.println("==========\n>>>>>No target client.");
		else{
			String targetUser = command.split(" ")[1];
			/*error if client blocks himself*/
			if(targetUser.equals(user))
				pw.println("==========\n>>>>>Error! You cannot block yourself!");
			else{
				dataBase.get(user)[5]= dataBase.get(user)[5].concat(" " + targetUser);
				System.out.println(dataBase.get(user)[5]);
				pw.println("==========\n>>>>>You have successfully blocked " + targetUser + " from sending you messages.");
			}
		}
	}
	/*private message*/
	public void privateMsg(HashMap<String, PrintWriter> map, HashMap<String, String[]> dataBase, String command){
		String[] subCmd = command.split(" ");
		String targetUser = subCmd[1];
		String msg = user + ": ";
		/*construct the whole message*/
		if(subCmd.length >= 2){
			for(int i = 2; i < subCmd.length; i++){
				msg = msg.concat(subCmd[i]).concat(" ");
			}
			/*if target client is online*/
			if(map.containsKey(targetUser)){
				/*check if current user is blocked by target user*/
				String[] blockList = dataBase.get(targetUser)[5].split(" ");
				boolean isBlocked = false;
				for(int i = 0; i < blockList.length; i++){
					if(blockList[i].equals(user)){
						pw.println("==========\n>>>>>You cannot send any message to " +targetUser + ". You have been blocked by the user.");
						isBlocked = true;
						break;
					}
				}
				/*send message if not blocked*/
				if(!isBlocked)
					map.get(targetUser).println(msg);
				
			}
			/*store the message if target client is not online*/
			else if(dataBase.containsKey(targetUser)){
				dataBase.get(targetUser)[6] = dataBase.get(targetUser)[6].concat(msg + "\n");
			}
			/*Invalid target user*/
			else{
				pw.println("User " + targetUser + "doesn't exist.");
			}
		}
		
	}
	/*broadcast to all online clients*/
	public void broadcast(HashMap<String, PrintWriter> map, String command){
		Object[] pws = map.values().toArray();//transfer values (PrintWriters) in the HashMap into an array
		String msg = command.replace("broadcast ", "");//get broadcast message
		for(int i = 0; i < pws.length; i++){//iterate all PrintWriters in the HashMap of online clients
			PrintWriter brcstPw = (PrintWriter) pws[i];
			if(!brcstPw.equals(pw))//broadcast to all other online clients
				brcstPw.println(user + ": " + msg);
		}
	}
	/*deal with who's online and who login in last hour*/
	public void whosOnList(ArrayList<String> list){
		String userName;
		/*no clients online except the current one*/
		if(list.size() == 1)
			pw.println("==========\n>>>>>No one else online");
		for(int i = 0; i < list.size(); i++){
			userName =  list.get(i);
			if(!userName.equals(user) && userName != null){
				pw.println(userName);
			}
		}
	}
	/*logout client*/
	public void logout(){
		pw.println("==========\n>>>>>Bye " + user + "!");
		Server.onlineClients.remove(user);//remove client from online list
//		Server.onlineSockets.remove(socket);//remove socket from online list
		Timer timer = new Timer();
		timer.schedule(new LastLoginTimer(user), Server.LAST_HOUR);//remove client from zombie list after LAST_HOUR
		Server.dataBase.get(user)[1] = "OFFLINE";//change its status as offline
		Server.serverWriter.remove(user);//remove the PrintWriter for this socket
		//drop the connection
		try {
			pw.println("==========\n>>>>>Connection closed");
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
