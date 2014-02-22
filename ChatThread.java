import java.net.Socket;

public class ChatThread extends Thread{
	public Socket socket;
	public ChatThread(Socket socket){
		this.socket = socket;
	}
}
