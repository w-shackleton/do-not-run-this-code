package pennygame.lib.msg;

public class MRefresher extends PennyMessage {

	private static final long serialVersionUID = 5823779687536371422L;
	
	public final int what;
	
	public MRefresher(int what) {
		this.what = what;
		
	}
	
	public static final int REF_USERLIST = 1;
}
