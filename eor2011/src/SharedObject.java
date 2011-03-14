import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	
	// THE object
	public Object obj;
	
	private int id;
	
	public SharedObject() {
	}
	
	public SharedObject(Object o, int i) {
		this();
		this.obj = o;
		this.id = i;
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
