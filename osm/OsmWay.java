package osm;

import java.util.SortedSet;
import java.util.TreeSet;

public class OsmWay implements Comparable<OsmWay>{
	protected SortedSet<OsmNode> way;
	protected long id;
	protected String type;
	protected String name;
	protected boolean oneWay;

	public OsmWay(long id, String name, String type) {
		this.name = name;
		this.type = type;
		this.id = id;
		way = new TreeSet<OsmNode>();
	}
	
	public OsmWay(long id){
		this.id = id;
		way = new TreeSet<OsmNode>();
	}

	protected void addToWay(OsmNode node) {
		way.add(node);
	}

	public boolean containsNode(OsmNode node) {
		return way.contains(node);
	}
	
	protected void setName(String name){
		this.name = name;
	}
	protected void setType(String type){
		this.type=type;
	}
	protected void setOneWay(String value){
		if (value == "yes"){
			oneWay = true;
		}else{
			oneWay = false;
		}
	}

	@Override
	public int compareTo(OsmWay w) {
		if (id == w.id)
		return 0;
		if (id > w.id){
			return (int) (id-w.id);
		}else{
			return (int) (w.id - id);
		}
	}
	
	public boolean equals (Object o){
		OsmWay way = (OsmWay) o;
		return id==way.id;
	}
	
	public String toString(){
		String result ="Id: "+id+", Type: "+type+", Name: "+name+"\nNodes: ";
		for (OsmNode n: way){
			result+=n.toString()+", ";
		}
		return result;
	}
}
