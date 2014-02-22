import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static int PORT;//port number on server side
	private ArrayList<Socket> onlineSockets = new ArrayList<Socket>();//list of all online client sockets
	private ServerSocket server;//server socket
	public Server(){
		try {
			server = new ServerSocket(PORT);
			System.out.println("Server on------------------------");
			while(true){
				Socket clientSocket = server.accept();
				onlineSockets.add(clientSocket);
				ChatThread chatThread = new ChatThread(clientSocket);
				chatThread.start();
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
