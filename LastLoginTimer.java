public class LastLoginTimer extends java.util.TimerTask{
	public String user;
	public LastLoginTimer(String user){
		this.user = user;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Server.zombieList.remove(user);
	}

}
