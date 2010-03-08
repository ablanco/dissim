package osm;

import java.util.ArrayList;
import java.util.List;

public class OsmWay implements Comparable<OsmWay> {
	/**
	 * Contains OsmNodes in a certain orden that especify the road
	 */
	protected List<OsmNode> way;
	protected long id;
	/**
	 * Información Extendida de WAY
	 */
	protected OsmNodeExtendedInfo extendedInfo;
	/**
	 * Sometimes OSM give nodes out of the box, Next node to this is contained
	 */
	protected OsmNode firsNode;
	/**
	 * Sometimes OSM give nodes out of the box, Previous node is contained
	 */
	protected OsmNode lastNode;
	// TODO prioridad a la hora de pintarlo
	/**
	 * Priority of painting ROAD
	 */
	protected int priority;

	public OsmWay(long id) {
		this.id = id;
		way = new ArrayList<OsmNode>();
	}

	protected void addToWay(OsmNode node) {
		way.add(node);
	}

	public boolean containsNode(OsmNode node) {
		return way.contains(node);
	}

	public void setFirsNode(OsmNode firsNode) {
		this.firsNode = firsNode;
	}

	public long getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public void setLastNode(OsmNode lastNode) {
		this.lastNode = lastNode;
	}

	public List<OsmNode> getWay() {
		return way;
	}

	public OsmNode getFirsNode() {
		return firsNode;
	}

	public OsmNode getLastNode() {
		return lastNode;
	}

	public short getKey() {
		if (extendedInfo != null) {
			return extendedInfo.getKey();
		} else {
			System.err.println("Undefined WAY TYPE - " + toString());
			return -1;
		}
	}

	@Override
	public int compareTo(OsmWay w) {
		if (id == w.id)
			return 0;
		if (id > w.id) {
			return (int) (id - w.id);
		} else {
			return (int) (w.id - id);
		}
	}

	public boolean equals(Object o) {
		OsmWay way = (OsmWay) o;
		return id == way.id;
	}

	public String toString() {
		String result = "Way Id: " + id;
		if (extendedInfo != null) {
			result += ": " + extendedInfo.toString();
		}
		if (firsNode != null) {
			result += " *First Node: " + firsNode + "* ";
		}
		if (lastNode != null) {
			result += " *Last Node: " + lastNode + "* ";
		}
		result += "| Nodes: ";
		for (OsmNode n : way) {
			result += n.toString() + ", ";
		}
		return result;
	}

	public void setExtendedInfo(OsmNodeExtendedInfo extendedInfo) {
		this.extendedInfo = extendedInfo;
	}
}
