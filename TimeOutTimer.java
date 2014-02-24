public class TimeOutTimer extends java.util.TimerTask{
	public ChatThread chatThread;
	public boolean isLogin;
	public TimeOutTimer(ChatThread chatThread, boolean isLogin){
		this.chatThread = chatThread;
		this.isLogin = isLogin;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		chatThread.logout(isLogin);
	}
}
