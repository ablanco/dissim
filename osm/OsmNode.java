//    Flood and evacuation simulator using multi-agent technology
//    Copyright (C) 2010 Alejandro Blanco and Manuel Gomar
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package osm;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import util.jcoord.LatLng;

/**
 * OsmNode class for easily managing OSM nodes
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class OsmNode implements Comparable<OsmNode>, Cloneable {
	protected long id;
	protected LatLng coord;
	protected List<OsmTag> tags;
	private short type = Osm.Undefined;

	private OsmNode(long id) {
		this.id = id;
		tags = new ArrayList<OsmTag>();
	}

	/**
	 * Node from a position
	 * 
	 * @param c
	 *            positon to place de node
	 */
	public OsmNode(LatLng c) {
		coord = c;
		tags = new ArrayList<OsmTag>();
	}

	/**
	 * osm static value
	 * 
	 * @return node static value
	 */
	public short getType() {
		return type;
	}

	/**
	 * Gets tags from the node
	 * 
	 * @return list of tags describing the node
	 */
	public List<OsmTag> getTags() {
		return tags;
	}

	/**
	 * Gets coord from the node
	 * 
	 * @return geolocalization of the node
	 */
	public LatLng getCoord() {
		return coord;
	}

	/**
	 * Gets osm static value from the node
	 * 
	 * @param type
	 *            node osm static value type
	 */
	public void setType(short type) {
		this.type = type;
	}

	/**
	 * Sets coord for the node
	 * 
	 * @param coord
	 *            new coord for the node
	 */
	public void setCoord(LatLng coord) {
		this.coord = coord;
	}

	/**
	 * Gets id given by OSM
	 * 
	 * @return node id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Add a tag to the tags list describing the node
	 * 
	 * @param tag
	 *            info
	 * @return true if not null
	 */
	public boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		}
		return false;
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

	@Override
	public boolean equals(Object o) {
		OsmNode node = (OsmNode) o;
		return id == node.id;
	}

	@Override
	public String toString() {
		String result = "Node Id: " + id + ": " + coord.toString();
		for (OsmTag tag : tags) {
			result += ", " + tag.toString();
		}
		return result;
	}

	@Override
	protected OsmNode clone() {
		OsmNode n = new OsmNode(id);
		n.setCoord(new LatLng(coord.getLat(), coord.getLng(), coord
				.getAltitude()));
		n.setType(type);
		for (OsmTag t : tags) {
			n.addTag(t);
		}
		return n;
	}

	/**
	 * Given a osm xml node, parse it and initizalizates a new osmNode object.
	 * 
	 * @param node
	 *            xml node from OSM webservice
	 * @return osmNode with all the info from the node
	 */
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
