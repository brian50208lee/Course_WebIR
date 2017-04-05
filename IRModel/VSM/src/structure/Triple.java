package structure;


public class Triple<X, Y, Z> {
	public final X x;
	public final Y y;
	public final Z z;

	public Triple(X x, Y y, Z z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "(" + this.x + "," + this.y + "," + this.z + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Triple){
			Triple<?, ?, ?> o = (Triple<?, ?, ?>)obj;
			return o.x.equals(x) && o.y.equals(y) && o.z.equals(z);
		}
		return false;
	}
}
