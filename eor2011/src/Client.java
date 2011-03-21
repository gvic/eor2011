import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.HashMap;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	// The server
	private static Server_itf server;
	private static HashMap<String, SharedObject> sharedObjectsList;
	
	public Client() throws RemoteException {
		super();
	}

///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		// Lookup for the server in the Naming space
		try {
		    int port = 8008;
		    String URL = "//" + InetAddress.getLocalHost().getHostName() + ":"+port+"/ox";
		    server = (Server_itf) Naming.lookup(URL);
		} catch (Exception ex) {
		    ex.printStackTrace();
		    System.out.println("Did you launch the server?");
		    System.exit(0);
		}
		
		// Create the sharedObjectsList
		sharedObjectsList = new HashMap<String, SharedObject>();
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		int id = -1;
		SharedObject so = null;
		
		try {
			id = server.lookup(name);
		} catch (RemoteException e) { // Remote problem
			e.printStackTrace();
		} catch (Exception ex) { 
			ex.printStackTrace();
			System.out.println("The object "+ name + " doesn't exist on the server.");
		}
		
		// Object *name* found on the server (server.lookup() didn't throw an exception)
		if( id != -1){
			try {
				// Remote call to retrieve the Object
				Object ob = ((ServerObject) ((Server) server).getServerObject(name)).obj;
				
				// Create local copy of the ServerObject
				so = new SharedObject(ob,id);
				sharedObjectsList.put(name,so);
				
			} catch (RemoteException exc) {
				exc.printStackTrace();
			}
		}
		return so;			
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject so) {
		try {
			server.register(name, so.id);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Add the object to the SharedObjectsList
		sharedObjectsList.put(name,so);
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
	// Create ServerObject in the server
		int id;
		SharedObject so = null;
		try {
			id = server.create(o);
			so = new SharedObject(o,id);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println(e.getCause().toString());
		} finally {
			System.out.println("Can't create SharedObject because of the server");
			
		}		
		
		
		return so;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////
	
	/* Voir page 3 sujet pour specs */

	// CLIENT ----> SERVER
	// request a read lock from the server
	public static Object lock_read(int id) {		
		Object o = server.lock_read(id, ? this ?);
		return o;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		Object o = server.lock_write(id, ??);
		return o;
	}

	
	// SERVER ----> CLIENT
	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject o = sharedObjectsList.get(id);
		o.reduce_lock();
		return o.obj;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		sharedObjectsList.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject o = sharedObjectsList.get(id);
		o.invalidate_writer();
		return o.obj;		
	}
	
}
