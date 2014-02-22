package tong;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
	
	public void run(){
		String pass = null;
		boolean isClient = false;
		/*initialize input and output*/
		
		/*Prompt to take client input for username and password*/
        pw.println("Please enter your username: ");
		try {
			user = br.readLine();
			System.out.println(user);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		pw.println("Please enter your password: ");
		try {
			pass = br.readLine();
			System.out.println(pass);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
 
//		System.out.println("Please enter your password: ");
		
		
		/*authenticate the client from user_pass.txt file*/
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
				if(userDataToken[0].equals(user) && userDataToken[1].equals(pass)){
					isClient = true;
					break;
				}
				 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*start to serve the client if authenticated*/
		if(isClient){
			System.out.println(socket.getInetAddress() + " logged in");
			/*record login status of the client*/
			
			
//			while(true){
//				
//			}
				
			/*deal with client commands*/
		}
		else{
			/*record the wrong trial time*/
			
		}
	}
}
