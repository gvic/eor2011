import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	
	private static final long serialVersionUID = 1L;
	static final int NL = 0; // NL : 0 : no local lock
	static final int RLC = 1; // RLC : 1 : read lock cached (not taken)
	static final int WLC = 2; // WLC : 2 : write lock cached
	static final int RLT = 3; // RLT : 3 : read lock taken
	static final int WLT = 4; // WLT : 4 : write lock taken
	static final int RLT_WLC = 5; // RLT_WLC : 5 : read lock taken and write lock cache
	
	// The object
	public Object obj;
	// Object id 
	public int id;
	// The lock
	public int lock;
	private boolean callback_processing;       
	
	// Default constructor : start with No Lock
	public SharedObject() {
		lock = NL;
	}
	
	// Constructor used to create a SharedObject
	public SharedObject(Object o, int i) {
		this();
		obj = o;
		id = i;	
		callback_processing = false;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		
		if(callback_processing){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		switch(lock){
		case NL: 
			this.obj = Client.lock_read(id);
			lock = RLT;
			break;
		case RLC: /* The lock had been cached, it's useless to make a request to Client layer */
			lock = RLT;
			break;
		case WLC: /* The WL is stronger than RL, we don't need to call Client layer */
			lock = RLT_WLC;
			break;
		case RLT: /* Nothing to do */
			break;
		case WLT: /* Warn the user, he has to unlock the WL himself */
			System.out.println("You had not release the write lock");
			break;
		case RLT_WLC:
			break;
		
		default: break;
		
		}
	}
	
	// invoked by the user program on the client node
	public void lock_write() {
		
		if(callback_processing){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		switch(lock){
		case NL: 
			obj = Client.lock_write(id); // Retrieve object
			lock = WLT; // A VERIFIER SI C'EST A FAIRE ICI
			break;
		case RLC: /* The lock had been cached, it's useless to make a request to Client layer */
			obj = Client.lock_write(id); // Retrieve object - server call
			lock = WLT; // A 
			break;
		case WLC: /* The WL is stronger than RL, we don't need to call Client layer */
			lock = WLT;
			break;
		case RLT: /* The client cannot switch directly into WLT state from RLT state */
			System.out.println("You have to remove the reader lock before using a write lock.");
			break;
		case WLT: /* Nothing to do */
			break;
		case RLT_WLC: 
			lock = WLT;
			break;
		
		default: break;
		
		}

	}

	// invoked by the user program on the client node
	public synchronized void unlock() {

		switch(lock){
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
		
		notify(); // Le sharedObject notifie qu'il a finit son travail
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		
		this.callback_processing = true;
		
		switch(lock){  
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case WLC: 		lock = RLC;		break;
			case WLT:
				/** Il faut faire attention ici : 
				 * ce n'est pas a nous de passer le lock en RLC,
				 * c'est le client qui le fera lorsqu'il appelera
				 * unlock() sur l'objet.
				 * Seulement a partir de ce moment on pourra appliquer
				 * le reduce_lock.
				 * Ce qui justifie l'utilisation du wait() puisque unlock()
				 * emet un notify()
				 */
				while(lock == WLT){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				break;
				
			case RLT_WLC:	lock = RLT;		break;  	
			default: 						break;
		}
		
		notify();
		
		// Des qu'on a émis le notify on peut mettre le booleen unlock_processing a false
		this.callback_processing = false;
		
		return obj;		
	}
	

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		
		this.callback_processing = true;
		
		switch(lock){  
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case RLC: 		lock = NL;		break;
			case RLT:	 	lock = NL;		break;   	
			default: 						break;
		}
		
		notify();
		
		this.callback_processing = false;
	}

	public synchronized Object invalidate_writer() {

		this.callback_processing = true;
		
		switch(lock){
			/* Se reporter au diagramme d'états diapo 27 des slides */
			case WLC: 		lock = NL;		break;
			case WLT: 		lock = NL;		break;		
			case RLT_WLC:	lock = NL;		break;  	
			default: 						break;
		
		}
		
		notify();
		this.callback_processing = false;
		return obj;	
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public Object getObj() {
		return this.obj;
	}
	
	static public String lockToString(int l) {
		String lock = "";
		
		switch(l){
		case SharedObject.NL: 
			lock = "NL";
			break;
		case SharedObject.RLC:
			lock = "RLC";
			break;
		case SharedObject.WLC: 
			lock = "WLC";
			break;
		case SharedObject.RLT:
			lock = "RLT";
			break;
		case SharedObject.WLT: 
			lock = "WLT";
			break;
		case SharedObject.RLT_WLC:
			lock = "RLT_WLC";
			break;
		
		default: break;
		
		}
		
		return lock;
	}
}
