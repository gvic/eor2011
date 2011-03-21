import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	private static final int NL = 0; // NL : 0 : no local lock
	private static final int RLC = 1; // RLC : 1 : read lock cached (not taken)
	private static final int WLC = 2; // WLC : 2 : write lock cached
	private static final int RLT = 3; // RLT : 3 : read lock taken
	private static final int WLT = 4; // WLT : 4 : write lock taken
	private static final int RLT_WLC = 5; // RLT_WLC : 5 : read lock taken and write lock cache
	
	// The object
	Object obj;
	// Object id 
	int id;
	// The lock
	int lock;       
	
	// Default constructor : start with No Lock
	public SharedObject() {
		lock = NL;
	}
	
	// Constructor used to create a SharedObject
	public SharedObject(Object o, int i) {
		this();
		obj = o;
		id = i;	
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		
		switch(lock){
		
		/* On fait un switch pour tout les etats de 
		 * lock pour l'instant, pour bien visualiser tous les cas.
		 */
		
		case NL: 
			Client.lock_read(id);
			break;
		case RLC: /* The lock had been cached, it's useless to make a request to Client layer */
			lock = RLT;
			break;
		case WLC: /* The WL is stronger than RL, we don't need to call Client layer */
			lock = RLT_WLC;
			break;
		case RLT: /* Nothing to do */
			break;
		case WLT: /* Warning the user, he has to unlock the WL himself */
			System.out.println("You had not release the write lock");
			break;
		case RLT_WLC:
			break;
		
		default: break;
		
		}
	}
	
	// invoked by the user program on the client node
	public void lock_write() {
		
		switch(lock){
		
		/* On fait un switch pour tout les etats de 
		 * lock pour l'instant, pour bien visualiser tous les cas.
		 */
		
		case NL: 
			Client.lock_write(id);
			break;
		case RLC: /* The lock had been cached, it's useless to make a request to Client layer */
			Client.lock_write(id);
			break;
		case WLC: /* The WL is stronger than RL, we don't need to call Client layer */
			lock = WLT;
			break;
		case RLT: /* We suppose that it's possible to increase the lock level without unlocking it before */
			Client.lock_write(id);
		case WLT: /* Nothing to do */
			break;
		case RLT_WLC:
			Client.lock_write(id);
			break;
		
		default: break;
		
		}

	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		
		switch(lock){
		
		/* On fait un switch pour tout les etats de 
		 * lock pour l'instant, pour bien visualiser tous les cas.
		 */
		
		case NL: break;
		case RLC: break;
		case WLC: break;
		case RLT: /* We cached the read lock */ 
			lock = RLC;
			break;
		case WLT: /* We cached the write lock */
			lock = WLC;
			break;
		case RLT_WLC:  /* We get back the WLC state */
			lock = WLC;
			break;
		
		default: break;
		
		}
		
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		
		switch(lock){  
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case WLC: 		lock = RLC;		break;
			case WLT: 		lock = RLC;		break;		
			case RLT_WLC:	lock = RLT;		break;  	
			default: 						break;
		}
		
		return obj;		
	}
	

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		
		switch(lock){  
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case RLC: 		lock = NL;		break;
			case RLT:	 	lock = NL;		break;   	
			default: 						break;
		}
		
	}

	public synchronized Object invalidate_writer() {

		switch(lock){
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case WLC: 		lock = NL;		break;
			case WLT: 		lock = NL;		break;		
			case RLT_WLC:	lock = NL;		break;  	
			default: 						break;
		
		}
		
		return obj;	
	}
	
}
