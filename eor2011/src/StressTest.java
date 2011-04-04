import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class StressTest extends Frame {
	
	private static final int READ = 0;

	private static final int WRITE = 1;

	private static final int BOTH = 2;

	public static int num = 0;
	
	// Related Integers
	public int nbCl;
	public int nbObj;
	
	public int mode;
	
	ArrayList<oneClient>	clients = new ArrayList<oneClient>();

	public static void main(String argv[]) {
		
		if (argv.length < 1) {
			System.out.println("cd");
			System.exit(0);
		} else {
			try {
				int m =0;
				if (argv[2].equals("write")) {
					m = WRITE;
				} else {
					if (argv[2].equals("read")){
						m = READ;
					} else {
						if (argv[2].equals("both")){
							m = BOTH;
						} else {
							System.out.println("Usage: java "+StressTest.class.getName()+" <#clients> [<#objects>] [read|write|both]");
							System.exit(0);
						}
					}
				}
				
				new StressTest(Integer.parseInt(argv[0]), Integer.parseInt(argv[1]), m);
			} catch (ArrayIndexOutOfBoundsException e) {
				try {
					new StressTest(Integer.parseInt(argv[0]), Integer.parseInt(argv[1]));
				} catch (ArrayIndexOutOfBoundsException ex) {
					new StressTest(Integer.parseInt(argv[0]));
				}
			} catch (NumberFormatException exc) {
				exc.printStackTrace();
				System.out.println("Usage: java "+StressTest.class.getName()+" <#clients> [<#objects>] [read|write|both]");
				System.exit(0);
			}
		}
		
	}

	
	public StressTest(int nbClts) {
		this.nbCl = nbClts;
		this.nbObj = 1;
		this.mode = WRITE;	
		
		next();
	}
	
	public StressTest(int nbClts, int nbObjs) {
		this.nbCl = nbClts;
		this.nbObj = nbObjs;
		this.mode = WRITE;

		next();
	}
	
	public StressTest(int nbClts, int nbObjs, int m) {
		this.nbCl = nbClts;
		this.nbObj = nbObjs;
		this.mode = m;
		
		next();
	}
	
	public void next() {
		for (int j=0; j<this.nbCl; j++) {
			// Create each clients
			oneClient oc = new oneClient(this.nbObj, this);
			this.clients.add(oc);
		}	
		
		switch(this.mode) {
		case READ:
			Iterator<oneClient> oc = this.clients.iterator();
			int j = 0;
			while (oc.hasNext()) {
				j++;
				for (int i=0; i<this.nbObj; i++) {
					System.out.println("Client "+j+", object "+i+" is : "+oc.next().read(i));					
				}
			}
			break;
		case WRITE:
			Iterator<oneClient> occ = this.clients.iterator();
			while (occ.hasNext()) {
				for (int k=0; k<this.nbObj; k++) {
					// 10 incr per object per client
					for (int kk=0; kk<10; kk++) {
						occ.next().incr(k);
					}
					
				}
			}
			break;
		case BOTH:
			Iterator<oneClient> it = this.clients.iterator();
			while (it.hasNext()) {
				for (int k=0; k<this.nbObj; k++) {
					// 10 incr per object per client
					for (int kk=0; kk<10; kk++) {
						it.next().incr(k);
					}
					
				}
			}
			Iterator<oneClient> ite = this.clients.iterator();
			int jk = 0;
			while (ite.hasNext()) {
				jk++;
				for (int i=0; i<this.nbObj; i++) {
					System.out.println("Client "+jk+", object "+i+" is : "+ite.next().read(i));					
				}
			}
			break;
		}
	}
}



class launchListener implements ActionListener {
	StressTest st;
	public launchListener (StressTest i) {
		st = i;
	}
	public void actionPerformed (ActionEvent e) {
		Iterator<oneClient> oc = st.clients.iterator();
				while (oc.hasNext()) {
					for (int i=0; i<st.nbObj; i++) {
						oc.next().incr(i);
					}
				}
	}
}

class oneClient {
	StressTest st;
	
	ArrayList<SharedObject>	objects = new ArrayList<SharedObject>();
	
	int id;
	
	public oneClient() {
		id = StressTest.num++;
	}
	public oneClient(StressTest sst) {
		this();
		st = sst;
		// initialize the system
		Client.init();	
	}
	public oneClient(int nbObj, StressTest sst) {
		this(sst);
		
		for (int i=0; i<nbObj; i++) {
			// look up the ith object in the name server
			// if not found, create it, and register it in the name server
			SharedObject s = Client.lookup(i+"");
			if (s == null) {
				s = Client.create(new Entier());
				System.out.println("Entier n°"+i+" created.");
				Client.register(i+"", s);
			} else {
				System.out.println("Entier n°"+i+" retrieved from server.");
			}
			
			
			this.objects.add(s);
		}
	}
	
	public void incr(int objNum) {
    	// lock the object in write mode
		objects.get(objNum).lock_write();
		
		// invoke the method
		((Entier)(objects.get(objNum).obj)).incrementer();
		
		// unlock the object
		objects.get(objNum).unlock();	
	}
	
	public Integer read(int objNum) {
		
		// lock the object in read mode
		objects.get(objNum).lock_read();
		
		// invoke the method
		Integer s = ((Entier)(objects.get(objNum).obj)).read();
		
		// unlock the object
		objects.get(objNum).unlock();
		
		
		return s;
	}
}


