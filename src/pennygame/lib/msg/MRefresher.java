package pennygame.lib.msg;

public class MRefresher extends PennyMessage {

	private static final long serialVersionUID = 5823779687536371422L;
	
	public final int what;
	
	public MRefresher(int what) {
		this.what = what;
		
	}
	
	public static final int REF_USERLIST = 1;
	public static final int REF_OPENQUOTELIST = 2;
	public static final int REF_MYINFO = 3;
	public static final int REF_MYQUOTES = 4;
	
	/**
	 * Used by projector and the admin clients
	 */
	public static final int REF_PASTTRADES = 5;
}
