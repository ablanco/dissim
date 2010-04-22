package util.jcoord;


public class LatLngBox {
	
	private LatLng Nw;
	private LatLng Se;
	
	public LatLngBox(LatLng Nw, LatLng Se){
		this.Nw = Nw;
		this.Se = Se;
	}
	
	public LatLng getNw() {
		return Nw;
	}
	public LatLng getSe() {
		return Se;
	}
	
	public boolean isIn(LatLng c){
		return c.isContainedIn(Nw, Se);
	}

}
