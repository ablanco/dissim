package osm;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import util.jcoord.LatLng;

public class OsmNode implements Comparable<OsmNode>, Cloneable {
	protected long id;
	protected LatLng coord;
	protected List<OsmTag> tags;
	private short type = Osm.Undefined;

	// private boolean in;

	private OsmNode(long id) {
		this.id = id;
		tags = new ArrayList<OsmTag>();
	}
	
	public OsmNode(LatLng c){
		coord = c;
		tags = new ArrayList<OsmTag>();
	}

	public short getType() {
		return type;
	}

	public List<OsmTag> getTags() {
		return tags;
	}

	public LatLng getCoord() {
		return coord;
	}

	public void setType(short type) {
		this.type = type;
	}

	public void setCoord(LatLng coord) {
		this.coord = coord;
	}


	public long getId() {
		return id;
	}

	public boolean isSimpleNode() {
		if (tags.size() > 0) {
			return false;
		}
		return true;
	}

	public boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		}
		return false;
	}

	// public void setIn(boolean in) {
	// this.in = in;
	// }

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

	@Override
	public boolean equals(Object o) {
		OsmNode node = (OsmNode) o;
		return id == node.id;
	}

	@Override
	public String toString() {
		String result = "Node Id: " + id + ": "+coord.toString();
		for (OsmTag tag : tags) {
			result += ", "+ tag.toString();
		}
		return result;
	}
	
	@Override
	protected OsmNode clone(){		
		OsmNode n = new OsmNode(id);
		n.setCoord(new LatLng(coord.getLat(), coord.getLng(), coord.getAltitude()));
		n.setType(type);
		for (OsmTag t : tags){
			n.addTag(t);
		}
		return n;
	}

	public static OsmNode getNode(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		long id = Long.parseLong(attributes.item(0).getNodeValue());
		OsmNode osmNode = new OsmNode(id);
		double lat = Double.parseDouble(attributes.item(1).getNodeValue());
		double lng = Double.parseDouble(attributes.item(2).getNodeValue());
		osmNode.setCoord(new LatLng(lat, lng));
		// Bajamos un nivel
		node = node.getFirstChild();
		while (node != null) {
			String type = node.getNodeName();
			if (type.equalsIgnoreCase("tag")) {
				osmNode.addTag(OsmTag.getTag(node));
			} else {
				// No deberiamos llegar aqui
			}
			node = node.getNextSibling();
		}
		if (!osmNode.isSimpleNode()) {
			osmNode.setType(Osm.getNodeType(osmNode.getTags()));
		}
		return osmNode;
	}
}
