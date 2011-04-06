import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientSimple extends Frame {

	private static final long serialVersionUID = 1L;
	boolean stop = false;

	SharedObject entier;
	boolean init;
	
	TextArea lock;
	TextArea valeur;
	
	
	public static void main(String argv[]) {

		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("Entier");
		if (s == null) {
			s = Client.create(new Entier());
			Client.register("Entier", s);
		}
		
		// create the graphical part
		new ClientSimple(s);
	}

	@SuppressWarnings("deprecation")
	public ClientSimple(SharedObject s) {
		entier = s;		
		init = true;
	
		setLayout(new FlowLayout());

		lock = new TextArea("",3,8,TextArea.SCROLLBARS_NONE);
		lock.setEditable(false);
		lock.setForeground(Color.red);
		lock.setBackground(Color.black);
		add(lock);
		
		valeur =new TextArea("",3,8,TextArea.SCROLLBARS_NONE);
		valeur.setEditable(false);
		valeur.setForeground(Color.green);
		valeur.setBackground(Color.black);
		add(valeur);
	
		Button inc_button = new Button("Inc");
		inc_button.addActionListener(new incListener(this));
		add(inc_button);
		Button dec_button = new Button("Dec");
		dec_button.addActionListener(new decListener(this));
		add(dec_button);
		Button raz_button = new Button("Raz");
		raz_button.addActionListener(new razListener(this));
		add(raz_button);
		Button lire_button = new Button("Read");
		lire_button.addActionListener(new lireListener(this));
		add(lire_button);
		Button pause_button = new Button("Pause");
		pause_button.addActionListener(new pauseListener(this));
		add(pause_button);
		
		
		setSize(450,150);
		
		show();
				
		Thread updateLock = new Thread(new updateLock(this));
		updateLock.start();
		
	}
}

class updateLock implements Runnable {
	ClientSimple simple;
	public updateLock(ClientSimple cs) {
		this.simple = cs;
	}
	@Override
	public void run() {
		while (true) {
			if (!simple.init) {
				simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
			}
		}
	}
}

class pauseListener implements ActionListener{
	
	ClientSimple simple;
	public pauseListener(ClientSimple c){
		this.simple = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		simple.stop = true;
	}
	
}

class repriseListener implements ActionListener{
	
	ClientSimple simple;
	public repriseListener(ClientSimple c){
		this.simple = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		simple.stop = false;
	}
	
}

class razListener extends myActionListener {
	public razListener (ClientSimple i) {
		super(i);
		simple = i;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		simple.stop = true;
    	// lock the object in write mode
		simple.entier.lock_write();
		
		// invoke the method
		((Entier)(simple.entier.obj)).raz();
		
		// unlock the object
		simple.entier.unlock();
	}

	
}

class lireListener extends myActionListener  {
	public lireListener (ClientSimple i) {
		super(i);
		simple = i;
	}
	@Override
	public void actionPerformed (ActionEvent e) {
		super.actionPerformed(e);
		simple.stop = false;
		Thread t = new Thread(new lireRunnableTask(simple));
		t.start();	
	}
}

class lireRunnableTask implements Runnable{

	private ClientSimple client;
	public lireRunnableTask(ClientSimple c) {
		this.client = c;
	}
	@Override
	public void run() {
		while(!client.stop){
			// lock the object in read mode
			client.entier.lock_read();
			
			// invoke the method
			Integer s = ((Entier)(client.entier.obj)).read();
			
			client.valeur.setText(""+s);
			
			// unlock the object
			client.entier.unlock();
			}
	}
	
}

class incListener extends myActionListener  {
	public incListener (ClientSimple i) {
		super(i);
    	simple = i;
	}
	public void actionPerformed (ActionEvent e) {  
		super.actionPerformed(e);
		simple.stop = false;
		Thread t = new Thread(new incRunnableTask(simple));
		t.start();		
	}
}


class incRunnableTask implements Runnable{

	private ClientSimple client;
	public incRunnableTask(ClientSimple c) {
		this.client = c;
	}
	@Override
	public void run() {
		while(!client.stop){
        	// lock the object in write mode
			client.entier.lock_write();
			
			// invoke the method
			((Entier)(client.entier.obj)).incrementer();
		
			// unlock the object
			client.entier.unlock();
		}
	}
	
}

class decListener extends myActionListener  {
	public decListener (ClientSimple i) {
		super(i);
    	simple = i;
	}
	public void actionPerformed (ActionEvent e) {  
		super.actionPerformed(e);
		simple.stop = false;
		Thread t = new Thread(new decRunnableTask(simple));
		t.start();	
	}
}

class decRunnableTask implements Runnable{

	private ClientSimple client;
	public decRunnableTask(ClientSimple c) {
		this.client = c;
	}
	@Override
	public void run() {
		while(!client.stop){
		    	// lock the object in write mode
			client.entier.lock_write();
			
			// invoke the method
			((Entier)(client.entier.obj)).decrementer();
						
			// unlock the object
			client.entier.unlock();
			}
	}
	
}

abstract class myActionListener implements ActionListener {
	ClientSimple simple;
	public myActionListener(ClientSimple s) {
		simple = s;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("test");
		if (simple.init) {
			simple.init = false;
		}
	}
}

