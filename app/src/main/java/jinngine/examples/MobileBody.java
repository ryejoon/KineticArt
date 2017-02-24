package jinngine.examples;
import jinngine.geometry.Geometry;
import jinngine.physics.*;

public class MobileBody extends Body{
	public MobileBody(String identifier, Geometry g) {
		super(identifier, g);
		// TODO Auto-generated constructor stub
	}

	private MobileBody child = null;

	
	public void setChild(MobileBody c){
		this.child = c;
	}
	
	public boolean hasChild(){
		if(child == null)
			return false;
		return true;
	}

	public MobileBody getChild(){
		return child;
	}

}
