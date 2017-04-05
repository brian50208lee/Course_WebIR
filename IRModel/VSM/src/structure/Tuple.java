package structure;


public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + this.x + "," + this.y + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple){
			Tuple<?, ?> o = (Tuple<?, ?>)obj;
			return o.x.equals(x) && o.y.equals(y);
		}
		return false;
	}
	

}
