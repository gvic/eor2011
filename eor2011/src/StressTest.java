import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class StressTest extends Frame {
	
	public static int num;
	
	//	public TextArea		text;
	public TextField	nbClients;
	public TextField	nbObjects;
	
	ArrayList<oneClient>	clients = new ArrayList<oneClient>();

	public static void main(String argv[]) {
				
//		// initialize the system
//		Client.init();
//		
//		// look up the IRC object in the name server
//		// if not found, create it, and register it in the name server
//		SharedObject s = Client.lookup("IRC");
//		if (s == null) {
//			s = Client.create(new Sentence());
//			Client.register("IRC", s);
//		}
		// create the graphical part
		new StressTest();
	}

	public StressTest() {
	
		setLayout(new FlowLayout());
	
//		text=new TextArea(10,60);
//		text.setEditable(false);
//		text.setForeground(Color.red);
//		add(text);
	
		TextArea ta1 = new TextArea("Nombre d'objects",1,16,TextArea.SCROLLBARS_NONE);
		TextArea ta2 = new TextArea("Nombre de clients",1,17,TextArea.SCROLLBARS_NONE);
		ta1.setEditable(false);
		ta2.setEditable(false);
		
		
		add(ta2);
		nbClients=new TextField(6);
		add(nbClients);

		
		add(ta1);
		nbObjects =new TextField(6);
		add(nbObjects);
	
		Button set_button = new Button("Set");
		set_button.addActionListener(new setListener(this));
		add(set_button);
		Button launch_button = new Button("Launch!");
		launch_button.addActionListener(new launchListener(this));
		add(launch_button);
		
		setSize(500,100);
//		text.setBackground(Color.black); 
		show();

	}
}



class launchListener implements ActionListener {
	StressTest st;
	public launchListener (StressTest i) {
		st = i;
	}
	public void actionPerformed (ActionEvent e) {
				
	}
}

class setListener implements ActionListener {
	StressTest st;
	public setListener (StressTest i) {
        	st = i;
	}
	public void actionPerformed (ActionEvent e) {
		try {
			// get the value of nbClients
			int nbClt = Integer.parseInt(st.nbClients.getText());
			// get the value of nbObjects
			int nbObj = Integer.parseInt(st.nbObjects.getText());

			for (int j=0; j<nbClt; j++) {
				// Create each clients
				oneClient oc = new oneClient(nbObj, st);
				st.clients.add(oc);
			}			
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
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
				s = Client.create(new Sentence());
				System.out.println("sentence n°"+i+" created.");
				Client.register(i+"", s);
			} else {
				System.out.println("sentence n°"+i+" retrieved from server.");
			}
			
			
			this.objects.add(s);
		}
	}
	
	public void write(String txt, int objNum) {
    	// lock the object in write mode
		objects.get(objNum).lock_write();
		
		// invoke the method
		((Sentence)(objects.get(objNum).obj)).write(txt);
		
		// unlock the object
		objects.get(objNum).unlock();	
	}
	
	public String read(int objNum) {
		
		// lock the object in read mode
		objects.get(objNum).lock_read();
		
		// invoke the method
		String s = ((Sentence)(objects.get(objNum).obj)).read();
		
		// unlock the object
		objects.get(objNum).unlock();
		
		
		return s;
	}
}


