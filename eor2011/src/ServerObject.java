import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;


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
		readerClients = new HashMap<Integer, Client_itf>();
		writerClient = null;
	}
	
	// Constructor used to create a ServerObject
	public ServerObject(Object o, int i){
		this();
		this.obj = o;
		this.id = i;
	}
	
	@Override
	public Object lock_read(Client_itf c) {
		Object o = null;
		try {
			o = writerClient.reduce_lock(id);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return o; // On retourne l'objet touché par le dernier écrivain
	}

	@Override
	public Object lock_write(Client_itf c) {
		Object o = null;
		Iterator<Client_itf> it = readerClients.values().iterator();

		try{
			o = writerClient.invalidate_writer(id); // On invalide le dernier client qui était ecrvain		
			while(it.hasNext()){
				it.next().invalidate_reader(id);
			}
		} catch(RemoteException e){
			e.printStackTrace();
		}
		
		writerClient = c; //Et on met une référence vers le nouveau client écrivain
		
		return o;  // On retourne l'objet touché par le dernier ecrivain
	}
	
}
