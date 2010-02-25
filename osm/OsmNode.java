package osm;

import osm.Streets.Nod;
import util.jcoord.LatLng;

public class OsmNode implements Comparable<OsmNode>{
	protected long id;
	protected LatLng coord;

	public OsmNode(long id, LatLng coord) {
		this.id = id;
		this.coord = coord;
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
}

