package osm;

import java.util.Hashtable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class OsmMember {
	private OsmWay way;
	private String role;
	private short type;
	private long ref;

	private OsmMember(short type, long ref, OsmWay way, String role) {
		this.type = type;
		this.ref = ref;
		this.way = way;
		this.role = role;
	}

	public boolean isEmpty() {
		return way.getWay().isEmpty();
	}

	public void setWay(OsmWay way) {
		this.way = way;
	}

	public String getRole() {
		return role;
	}

	public long getRef() {
		return ref;
	}
	
	public short getType() {
		return type;
	}

	public OsmWay getWay() {
		return way;
	}

	@Override
	public boolean equals(Object obj) {
		return way.equals(obj);
	}

	@Override
	public String toString() {
		return "Member, ref: "+ref+", type: "+type+", role: "+role;//+" || "+way.getWay();
	}
	public static OsmMember getMember(Node node,
			Hashtable<Long, OsmWay> ways) {
		NamedNodeMap attributes = node.getAttributes();
		long ref = Long.parseLong(attributes.item(1).getNodeValue());
		String role = attributes.item(2).getNodeValue();
		OsmWay osmWay = ways.get(ref);
		if (osmWay != null) {
			return new OsmMember(osmWay.getType(), ref, osmWay, role);
		} else {
			return null;
		}
	}

}
