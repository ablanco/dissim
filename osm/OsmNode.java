package osm;

import util.Point;

public class OsmNode implements Comparable<OsmNode>{
	protected long id;
	protected Point point;
	protected double lng;
	protected double lat;

	public OsmNode(long id, Point point) {
		this.id = id;
		this.point = point;
	}
	
	public OsmNode(long id, double lat, double lng) {
		this.id=id;
		this.lat=lat;
		this.lng=lng;
	}

	@Override
	public int compareTo(OsmNode n) {
		if (id == n.id)
			return 0;
			if (id > n.id){
				return (int) (id-n.id);
			}else{
				return (int) (n.id - id);
			}
	}

	public boolean equals(Object o) {
		OsmNode node = (OsmNode) o;
		return id == node.id;
	}
	
	public String toString(){
		String result="IdNode: "+id+" ";
		if (point != null){
			result +=point.toString();
		}else{
			result +="("+lat+","+lng+")";
		}
		return result;
	}
}

