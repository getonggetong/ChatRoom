public class LockTimer extends java.util.TimerTask{
	public String user;
	public LockTimer(String user){
		this.user = user;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Server.dataBase.get(user)[4] = "UNLOCK";
	}

}
