package osm;

import java.util.ArrayList;
import java.util.List;

public class OsmWay implements Comparable<OsmWay>{
	protected List<OsmNode> way;
	protected long id;
	protected OsmNodeExtendedInfo extendedInfo;
	
	public OsmWay(long id){
		this.id = id;
		way = new ArrayList<OsmNode>();
	}

	protected void addToWay(OsmNode node) {
		way.add(node);
	}

	public boolean containsNode(OsmNode node) {
		return way.contains(node);
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
		String result ="Way Id: "+id;
		if (extendedInfo!=null){
			result +=": "+extendedInfo.toString();
		}
		result += "| Nodes: ";
		for (OsmNode n: way){
			result+=n.toString()+", ";
		}
		return result;
	}
	public void setExtendedInfo(OsmNodeExtendedInfo extendedInfo) {
		this.extendedInfo = extendedInfo;
	}
}
