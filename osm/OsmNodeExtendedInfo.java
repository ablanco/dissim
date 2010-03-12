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
	
	@Override
	public String toString(){
		String result = "("+OsmInf.getName(key)+") ";
		if (value!=null)
			result += value;
		if (name!=null)
			result += ", Named: "+name;
		return result;
	}
}
