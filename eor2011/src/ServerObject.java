import java.io.Serializable;


public class ServerObject implements Serializable, ServerObject_itf {

	private static final int NL = 0; // NL : 0 : no local lock
	private static final int RL = 1; // RL : 1 : read locked
	private static final int WL = 2; // WL : 2 : write locked
	
	// The object
	Object obj;
	// Object id 
	int id;

	// The lock
	int lock;       
	
	// Default constructor : start with No Lock
	public ServerObject() {
		lock = NL;
	}
	
	// Constructor used to create a ServerObject
	public ServerObject(Object o, int i){
		this();
		this.obj = o;
		this.id = i;
	}
	
	@Override
	public void lock_read() {
		
	}

	@Override
	public void lock_write() {
		
	}
	
}
