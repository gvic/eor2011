import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf {

	/*
	 * HashMapS which contain serverObjects (either registered or not)
	 * public accessor needed by the Client method lookup
	 */
	public HashMap<String, ServerObject_itf> serverObjectsList;
	private HashMap<Integer, ServerObject_itf> notRegisteredServerObject;
	
	// static count number 
	private int idCpt = 0;
	
	protected Server() throws RemoteException {
		serverObjectsList =  new HashMap<String, ServerObject_itf>();
		notRegisteredServerObject = new HashMap<Integer, ServerObject_itf>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#lookup(java.lang.String)
	 */
	@Override
	public int lookup(String name) throws RemoteException {
		return ((ServerObject) serverObjectsList.get(name)).id;
	}
	
	/*
	 * Method used to retrieve the real object (!= lookup)
	 */
	public ServerObject_itf getServerObject(String name) throws RemoteException {		
		return serverObjectsList.get(name);
	}

	/*
	 * Registers an object with a name.
	 * 
	 * @see Server_itf#register(java.lang.String, int)
	 */
	@Override
	public void register(String name, int id) throws RemoteException {
		ServerObject_itf so = notRegisteredServerObject.get(id);
		/* Petite vérification ... */
		if(so != null){
			serverObjectsList.put(name, so);
		}
		else{
			System.out.println("Object "+id+" doesn't exist on the Server");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#create(java.lang.Object)
	 */
	@Override
	public int create(Object o) throws RemoteException {
		idCpt++;
		ServerObject_itf so = new ServerObject(o,idCpt);
		notRegisteredServerObject.put(idCpt,so);
		return this.idCpt;
	}

	/*
	 * "Les appels au serveur sont transférés au ServerObject concerné"
	 * 
	 * @see Server_itf#lock_read(int, Client_itf)
	 */
	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		ServerObject_itf so = serverObjectsList.get(id);
		Object o = so.lock_read(client);
		return o;
	}

	/*
	 * "Les appels au serveur sont transférés au ServerObject concerné"
	 * 
	 * @see Server_itf#lock_write(int, Client_itf)
	 */
	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		ServerObject_itf so = serverObjectsList.get(id);
		Object o = so.lock_write(client);
		return o;	}

	/**
	 * Main method :
	 * Instanciate the server and bind it to the RMI Naming space
	 * @param args contains the server port
	 */
	public static void main(String args[]) {
		int port;
		String URL;
		try {
			// Parse String into an Integer
			Integer I = new Integer(args[0]);
			port = I.intValue();
		} catch (Exception ex) {
			System.out.println(" Please enter: Server <port>");
			return;
		}
		
		try {
			// Création du serveur de nom - rmiregistry
			Registry registry = LocateRegistry.createRegistry(port);

			// Création d'un serveur
			Server_itf server = new Server();

			// Calcul de l’URL du serveur
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port
					+ "/ox";
			Naming.bind(URL, server);

			System.out.println("Server launched : ");
			System.out.println(URL);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
