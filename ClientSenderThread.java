package tong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientSenderThread extends Thread{
	public PrintWriter pw;
	public ClientSenderThread(PrintWriter pw){
		this.pw = pw;
	}
	
	public void run(){
		/*wait for client input and send messages to server*/
        while(true){
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
    			pw.println(reader.readLine());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
	}
}
