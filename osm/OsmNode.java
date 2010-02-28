package osm;

import util.Point;
import util.jcoord.LatLng;

public class OsmNode implements Comparable<OsmNode> {
	protected long id;
	protected Point point;
	protected LatLng coord;
	protected OsmNodeExtendedInfo extendedInfo;
	private boolean in;

	public OsmNode(long id, Point point) {
		this.id = id;
		this.point = point;
		in=true;
	}

	public OsmNode(long id, LatLng coord) {
		this.id=id;
		this.coord=coord;
		in=false;
	}

	public void setExtendedInfo(OsmNodeExtendedInfo extendedInfo) {
		this.extendedInfo = extendedInfo;
	}
	public OsmNodeExtendedInfo getExtendedInfo() {
		return extendedInfo;
	}
	
	public void setPoint(Point point) {
		this.point = point;
		in = true;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public boolean isIn(){
		return in;
	}

	@Override
	public int compareTo(OsmNode n) {
		if (id == n.id)
			return 0;
		if (id > n.id) {
			return (int) (id - n.id);
		} else {
			return (int) (n.id - id);
		}
	}

	public boolean equals(Object o) {
		OsmNode node = (OsmNode) o;
		return id == node.id;
	}

	public String toString() {
		String result = "Node Id: " + id + " ";
		if (point != null) {
			result += point.toString();
		} else {
			result += coord.toString()+" ";
		}
		if (extendedInfo!=null){
			result += extendedInfo.toString();
		}
		return result;
	}
}
