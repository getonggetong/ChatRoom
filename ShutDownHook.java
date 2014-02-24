import java.io.PrintWriter;

public class ShutDownHook{
	private PrintWriter pw;
	public ShutDownHook(PrintWriter pw){
		this.pw = pw;
		logoutOnShutDown();
	}
	
	private void logoutOnShutDown(){
		Runtime run = Runtime.getRuntime();
		run.addShutdownHook(new Thread(){
			@Override
			public void run(){				
				/*logout this client when client program exit*/
				pw.println("logout");
			}
		});
	}
}
