import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	// The server
	private static Server_itf server;
	
	public Client() throws RemoteException {
		super();
	}

///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		// Lookup in the Naming space
		try {
		    int port = 12596;
		    String URL = "//bouba:"+port+"/ox";
		    server = (Server_itf) Naming.lookup(URL);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		return null;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		// Create ServerObject in da server
		int id;
		SharedObject so = null;
		try {
			id = server.create(o);
			so = new SharedObject(o,id);
		} catch (RemoteException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Can't create SharedObject because of the server");
		}
		
		
		
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
	}
	
}
