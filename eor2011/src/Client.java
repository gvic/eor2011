import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	// The server
	private static Server_itf server;
	private static Hashtable<Integer, SharedObject_itf> sharedObjectsList;
	
	// Instance of the client
	private static Client_itf myClient;
	
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
		sharedObjectsList = new Hashtable<Integer, SharedObject_itf>();
		// Instantiate Client object
		try {
			myClient = new Client();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
//			try {
//				// Remote call to retrieve the Object
//				 Object ob = ((ServerObject) ((Server) server).getServerObject(name)).obj;
//			} catch (RemoteException exc) {
//				exc.printStackTrace();
//			} 
			// Create local copy of the ServerObject
			so = new SharedObject(null,id);
			sharedObjectsList.put(id,so);
		}
		return so;			
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			server.register(name, so.getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Add the object to the SharedObjectsList
		sharedObjectsList.put(so.getId(),so);
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
		Object o = null;
		try {
			o = server.lock_read(id, myClient);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		Object o = null;
		try {
			o = server.lock_write(id, myClient);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	
	// SERVER ----> CLIENT
	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject_itf o = sharedObjectsList.get(id);
		try {
			o.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		o.reduce_lock();
		return o.getObj();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		sharedObjectsList.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject_itf o = sharedObjectsList.get(id);
		o.invalidate_writer();
		return o.getObj();		
	}
	
}
