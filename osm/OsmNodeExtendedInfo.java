package osm;

public class OsmNodeExtendedInfo {

	private String value;
	private String name;
	private short key;
	
	
	public OsmNodeExtendedInfo(short key, String name, String value){
		this.name=name;
		this.value=value;
		this.key=key;
		
	}

	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	
	public short getKey() {
		return key;
	}
	
	public String toString(){
		return " Name: "+name+" is a "+value;
	}
}
