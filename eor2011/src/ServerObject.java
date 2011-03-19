import java.io.Serializable;
import java.util.HashMap;


public class ServerObject implements Serializable, ServerObject_itf {

	private static final int NL = 0; // NL : 0 : no local lock
	private static final int RL = 1; // RL : 1 : read locked
	private static final int WL = 2; // WL : 2 : write locked
	
	// The object
	public Object obj;
	// Object id 
	int id;

	// The lock
	int lock;    
	
	//reader Clients
	private HashMap<Integer, Client_itf> readerClients;
	private Client_itf writerClient;
	
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
	public void lock_read(Client_itf c) {
		
	}

	@Override
	public void lock_write(Client_itf c) {
		
	}
	
}
