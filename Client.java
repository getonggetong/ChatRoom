import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
	public String IP;
	public int PORT;
	public Socket socket;
	public String user;
	public static PrintWriter pw;
	public BufferedReader br;
	
	public Client(String IP, int PORT){
		this.IP = IP;
		this.PORT = PORT;
		try {
			socket = new Socket(IP, PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*initiate input and output stream*/
		
		try {
			pw=new PrintWriter(socket.getOutputStream(),true);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    

	    try {
	    	br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        /*create new thread to wait user input and send message to server*/
        final ClientSenderThread csThread = new ClientSenderThread(pw);
        /*register shutdownhook to deal with exit of clients*/
        new ShutDownHook(pw);
        csThread.start();
        
        String serverMsg;
        /*wait messages from server*/
        while(true){
	        try {
				while((serverMsg = br.readLine()) != null){
					System.out.println(serverMsg);
					/*break if connection is dropped*/
					if(serverMsg.contains(">>>>Connection")){
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
        }
        System.exit(0);//exit program
	}
	
	
	public static void main(String[] args){
		
		new Client(args[0], Integer.parseInt(args[1]));
		
	}
}
