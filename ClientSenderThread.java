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
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try {
            	String command = reader.readLine();
            	if(command != null && !command.equals(""))
            		pw.println(command);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
	}
}
