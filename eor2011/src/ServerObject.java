import java.io.Serializable;


public class ServerObject implements ServerObject_itf, Serializable {

	public Object obj;
	public int id;

	public ServerObject(){
	}
	
	public ServerObject(Object o, int i){
		this();
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
	
}
