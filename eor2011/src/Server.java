import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf {

	/*
	 * HashMap which contains THE serverObjects
	 */
	private HashMap<String, ServerObject_itf> serverObjectsList;
	private HashMap<Integer, ServerObject_itf> notRegisteredServerObject;
	private int idCpt = 0;
	
	protected Server() throws RemoteException {
		serverObjectsList =  new HashMap<String, ServerObject_itf>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#lookup(java.lang.String)
	 */
	@Override
	public int lookup(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * Create the ServerObject related to the SharedObject
	 * 
	 * @see Server_itf#register(java.lang.String, int)
	 */
	@Override
	public void register(String name, int id) throws RemoteException {
		ServerObject_itf so = notRegisteredServerObject.get(id);
		/* Petite vérification ... */
		if(id == so.getId()){
			serverObjectsList.put(name, so); // A VERIFIER...
		}
		else{
			System.out.println("Les id ne correspondent pas");
			System.exit(0);
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
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#lock_read(int, Client_itf)
	 */
	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#lock_write(int, Client_itf)
	 */
	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */

	public static void main(String args[]) {
		int port;
		String URL;
		try {
			// transformation d’une chaîne de caractères en entier
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

			System.out.println("Server lancé à l'adresse : ");
			System.out.println(URL);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
