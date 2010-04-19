package osm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

public class OsmWay implements Comparable<OsmWay>{
	/**
	 * Contains OsmNodes in a certain orden that especify the road
	 */
	protected List<OsmNode> way;
	protected long id;
	/**
	 * Información Extendida de WAY
	 */
	protected List<OsmTag> tags;
	/**
	 * Priority of painting ROAD
	 */
	
	protected short type=Osm.Undefined;

	private OsmWay(long id) {
		this.id = id;
		way = new ArrayList<OsmNode>();
		tags = new ArrayList<OsmTag>();
	}

	public List<OsmTag> getTags() {
		return tags;
	}
	
	public void setType(short type) {
		this.type = type;
	}

	protected boolean addToWay(OsmNode node) {
		if (node != null){
			return way.add(node);	
		}
		return false;
	}

	private boolean addTag(OsmTag tag) {
		if (tag != null){
			return tags.add(tag);
		}
		return false;		
	}

	public long getId() {
		return id;
	}

	public short getType() {
		if (type == Osm.Undefined){
			type = Osm.getNodeType(tags);
		}
		return type;
	}

	public List<OsmNode> getWay() {
		return way;
	}


	@Override
	public boolean equals(Object o) {
		OsmWay way = (OsmWay) o;
		return id == way.id;
	}

	@Override
	public String toString() {
		String result = "Way: "+id+", "+getType()+"\t tags: ";		
		for (OsmTag tag : tags){
			result += tag.toString() +", ";
		}
		result += "\n\t Nodes: ";
		for (OsmNode n : way) {
			result += n.toString() + ", ";
		}
		
		return result;
	}


	@Override
	public int compareTo(OsmWay o) {
		return (int) (id - o.id);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (int) id;
	}
	
	public static OsmWay getOsmWay(Node node, Hashtable<Long, OsmNode> nodes){
		OsmWay osmWay = new OsmWay(Long.parseLong(node
				.getAttributes().item(0).getNodeValue()));
		// Bajamos un nivel
		node = node.getFirstChild();
		while (node != null) {
			String type = node.getNodeName();
			if (type.equalsIgnoreCase("nd")) {
				long ref = Long.parseLong(node.getAttributes().item(0).getNodeValue());
				osmWay.addToWay(nodes.get(ref));
			} else if (type.equalsIgnoreCase("tag")) {
				osmWay.addTag(OsmTag.getTag(node));
			} else {
				// No deberiamos llegar aqui
			}
			node = node.getNextSibling();
		}
		if (osmWay.getWay().size() > 0) {
			osmWay.setType(Osm.getNodeType(osmWay.getTags()));
			return osmWay;
		} else {
			return null;
		}
	}

	public void cleanNodes() {
		Iterator<OsmNode> it = way.iterator();
		while (it.hasNext()){
			if (it.next().getPoint() == null){
				it.remove();
			}
		}
		
	}


}
