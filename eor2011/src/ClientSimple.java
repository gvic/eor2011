import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientSimple extends Frame {

	private static final long serialVersionUID = 1L;

	SharedObject entier;
	
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
		
		
		setSize(400,100);
		
		show();
		
		entier = s;
	}
}

class razListener implements ActionListener {
	ClientSimple simple;
	public razListener (ClientSimple i) {
		simple = i;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
    	// lock the object in write mode
		simple.entier.lock_write();
		
		// invoke the method
		((Entier)(simple.entier.obj)).raz();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
		// unlock the object
		simple.entier.unlock();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
	}

	
}

class lireListener implements ActionListener {
	ClientSimple simple;
	public lireListener (ClientSimple i) {
		simple = i;
	}
	@Override
	public void actionPerformed (ActionEvent e) {
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
		// lock the object in read mode
		simple.entier.lock_read();
		
		// invoke the method
		Integer s = ((Entier)(simple.entier.obj)).read();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
		simple.valeur.setText(""+s);
		
		// unlock the object
		simple.entier.unlock();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
	}
}

class incListener implements ActionListener {
	ClientSimple simple;
	public incListener (ClientSimple i) {
        	simple = i;
	}
	public void actionPerformed (ActionEvent e) {
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
        	
        	// lock the object in write mode
		simple.entier.lock_write();
		
		// invoke the method
		((Entier)(simple.entier.obj)).incrementer();

		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
		// unlock the object
		simple.entier.unlock();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
	}
}

class decListener implements ActionListener {
	ClientSimple simple;
	public decListener (ClientSimple i) {
        	simple = i;
	}
	public void actionPerformed (ActionEvent e) {
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
        	
        	// lock the object in write mode
		simple.entier.lock_write();
		
		// invoke the method
		((Entier)(simple.entier.obj)).decrementer();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
		
		// unlock the object
		simple.entier.unlock();
		
		simple.lock.setText(SharedObject.lockToString(simple.entier.lock));
	}
}


