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
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
	}
	
}
