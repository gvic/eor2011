import java.io.Serializable;


public class ServerObject implements ServerObject_itf, Serializable {

	private Object obj;
	private int id;

	public ServerObject(Object o, int i){
		this.obj = o;
		this.id = i;
	}
	
	@Override
	public void lock_read() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lock_write() {
		// TODO Auto-generated method stub
		
	}

	public int getId() { return id;	}

	
}
