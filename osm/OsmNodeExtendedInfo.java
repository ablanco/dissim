package osm;

public class OsmNodeExtendedInfo {

	private String use;
	private String name;
	
	
	public OsmNodeExtendedInfo(String name, String use){
		this.name=name;
		this.use=use;
	}

	public String getName() {
		return name;
	}
	public String getUse() {
		return use;
	}
	
}
