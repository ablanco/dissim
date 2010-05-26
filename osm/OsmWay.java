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
import java.util.Hashtable;
import java.util.List;

import javax.swing.text.html.HTML.Tag;

import org.w3c.dom.Node;

import util.HexagonalGrid;
import util.Point;
import util.flood.Edge;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

/**
 * It represents a Way from OSM data.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class OsmWay implements Comparable<OsmWay> {
	/**
	 * Contains OsmNodes in a certain orden that especify the road
	 */
	protected List<OsmNode> way;
	protected long id;
	/**
	 * Extended information of WAY
	 */
	protected List<OsmTag> tags;
	/**
	 * Priority of painting ROAD
	 */
	private List<OsmEdge> edges;

	protected short type = Osm.Undefined;
	private LatLngBox box = null;

	/**
	 * {@link OsmWay} constructor
	 * 
	 * @param id
	 */
	protected OsmWay(long id) {
		this.id = id;
		way = new ArrayList<OsmNode>();
		tags = new ArrayList<OsmTag>();
		box = new LatLngBox();
		edges = new ArrayList<OsmEdge>();
	}

	/**
	 * Get a list of tags from the way
	 * 
	 * @return list of {@link Tag}
	 */
	public List<OsmTag> getTags() {
		return tags;
	}

	/**
	 * Get a list of edges in order that defines de way
	 * 
	 * @return list of {@link Edge}
	 */
	public List<OsmEdge> getEdges() {
		return edges;
	}

	/**
	 * Get the box containingn the way
	 * 
	 * @return {@link LatLngBox} of coordinates
	 */
	public LatLngBox getBox() {
		return box;
	}

	/**
	 * Gets the node identified by ind
	 * 
	 * @param ind
	 *            of the node
	 * @return the {@link OsmNode} if exists
	 */
	public OsmNode getNode(int ind) {
		if (!way.isEmpty() && ind >= 0 && ind < way.size()) {
			return way.get(ind);
		}
		return null;
	}

	/**
	 * Gets the last node of the way, if exists
	 * 
	 * @return last node, if exists, else null
	 */
	public OsmNode getLastNode() {
		if (way.size() > 0)
			return getNode(way.size() - 1);
		return null;
	}

	/**
	 * Sets a new type for the way
	 * 
	 * @param type
	 *            new type
	 */
	public void setType(short type) {
		this.type = type;
	}

	/**
	 * Sets a new box for the way
	 * 
	 * @param box
	 *            new {@link LatLngBox}
	 */
	public void setBox(LatLngBox box) {
		this.box = box;
	}

	/**
	 * Adds a new node for the way. This is an ordered list, that means that
	 * order is really important, and a node must be followed by the next node
	 * to build a good road, fortunately OSM gives the nodes in order. It also
	 * creates a new edge from each new node added.
	 * 
	 * @param node
	 *            we want to add to the way
	 * @return true if inserted
	 */
	protected boolean addToWay(OsmNode node) {
		if (node != null) {
			box.addToBox(node.getCoord());
			OsmNode a = getLastNode();
			if (a != null) {
				edges.add(new OsmEdge(a, node));
			}
			return way.add(node);
		}
		return false;
	}

	/**
	 * Appends a list of nodes to the end of the way. Nodes must be in order and
	 * must be the following nodes to the last node
	 * 
	 * @param nodes
	 *            list of nodes
	 * @return true if inserted
	 */
	protected boolean addAllToWay(List<OsmNode> nodes) {
		if (nodes.isEmpty())
			return false;
		for (OsmNode node : nodes) {
			addToWay(node);
		}
		return true;
	}

	/**
	 * Adds a new tag to the list of tags
	 * 
	 * @param tag
	 *            we want to add to the list
	 * @return true if inserted
	 */
	private boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		}
		return false;
	}

	/**
	 * Gets id from the way, same that OSM
	 * 
	 * @return way id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets static value from {@link Osm} that identifies his type
	 * 
	 * @return type
	 */
	public short getType() {
		if (type == Osm.Undefined) {
			type = Osm.getNodeType(tags);
		}
		return type;
	}

	/**
	 * Gets the list of nodes that contains the way
	 * 
	 * @return list of nodes
	 */
	public List<OsmNode> getWay() {
		return way;
	}

	/**
	 * Gets the first node of the way, if exists
	 * 
	 * @return first node of the way, if exists
	 */
	public OsmNode getFirstNode() {
		if (!way.isEmpty()) {
			return way.get(0);
		}
		return null;
	}

	/**
	 * Gets the list of nodes but reversed
	 * 
	 * @return reversed way node list
	 */
	public List<OsmNode> getReverseWay() {
		List<OsmNode> reverse = new ArrayList<OsmNode>();
		for (int i = way.size() - 1; i >= 0; i--) {
			reverse.add(way.get(i));
		}
		return reverse;
	}

	/**
	 * Gets the edge list but reversed
	 * 
	 * @return reversed edge list
	 */
	public List<OsmEdge> getReverseEdge() {
		List<OsmEdge> reverse = new ArrayList<OsmEdge>();
		for (int i = edges.size() - 1; i >= 0; i--) {
			reverse.add(edges.get(i));
		}
		return reverse;
	}

	/**
	 * Gets a list of points according to a grid that represents the way. The
	 * list of points are inside the grid and correspond to a discretization of
	 * the values from the original way
	 * 
	 * @param grid
	 *            Where we want to discretize the way
	 * @return discretized point list
	 */
	public List<Point> getLines(HexagonalGrid grid) {
		LatLngBox gridBox = grid.getBox();
		if (!gridBox.isDefined() || way.isEmpty() || edges.isEmpty()) {
			return null;
		}
		List<Point> line = new ArrayList<Point>();
		// este punto no existe
		Point prev = new Point(-1, -1);
		for (OsmEdge edge : edges) {
			for (LatLng c : edge.getLine(gridBox)) {
				Point curr = grid.coordToTile(c);
				if (!prev.equals(curr)) {
					// System.err.println("Punto duplicado");
					line.add(curr);
				}
				prev = new Point(curr.getCol(), curr.getRow());
			}

		}
		return line;
	}

	/**
	 * Given a coordinate returns true if it is inside the polygonal line
	 * 
	 * @param coord
	 * @return true if the coordinate is inside the polygonal line
	 */
	public boolean isIntoPoligon(LatLng coord) {
		int counter = 0;
		double xinters;
		LatLng p1;
		LatLng p2;
		int n = way.size();
		p1 = getNode(0).getCoord();
		for (int i = 1; i <= n; i++) {
			p2 = getNode(i % n).getCoord();
			if (coord.getLng() > Math.min(p1.getLng(), p2.getLng())) {
				if (coord.getLng() <= Math.max(p1.getLng(), p2.getLng())) {
					if (coord.getLat() <= Math.max(p1.getLat(), p2.getLat())) {
						if (p1.getLng() != p2.getLng()) {
							xinters = (coord.getLng() - p1.getLng())
									* (p2.getLat() - p1.getLat())
									/ (p2.getLng() - p1.getLng()) + p1.getLat();
							if (p1.getLat() == p2.getLat()
									|| coord.getLat() <= xinters)
								counter++;
						}
					}
				}
			}
			p1 = p2;
		}
		if (counter % 2 == 0) {
			return (false);
		} else {
			return (true);
		}
	}

	/**
	 * Check if the first node and last node are the same. If true it's a
	 * polygon, if false, it's just a way
	 */
	public boolean isClosedLine() {
		OsmNode a = getNode(0);
		OsmNode b = getLastNode();
		return a.getCoord().equals(b.getCoord());
	}

	@Override
	public boolean equals(Object o) {
		OsmWay way = (OsmWay) o;
		return id == way.id;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Way: " + id + ", " + getType());
		result.append(", Box: " + box);
		result.append("\n\t tags: ");
		for (OsmTag tag : tags) {
			result.append(tag.toString() + ", ");
		}
		// result.append("\n\t Nodes: ");
		// for (OsmNode n : way) {
		// result.append(n.toString() + ", ");
		// }

		return result.toString();
	}

	@Override
	public int compareTo(OsmWay o) {
		return (int) (id - o.id);
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	/**
	 * Given a parsed node from xml file from OSM, builds a {@link OsmWay} with
	 * all the info
	 * 
	 * @param node
	 *            from OSM
	 * @param nodes
	 *            from osmMap
	 * @return the way
	 */
	public static OsmWay getOsmWay(Node node, Hashtable<Long, OsmNode> nodes) {
		OsmWay osmWay = new OsmWay(Long.parseLong(node.getAttributes().item(0)
				.getNodeValue()));
		// Bajamos un nivel
		node = node.getFirstChild();
		while (node != null) {
			String type = node.getNodeName();
			if (type.equalsIgnoreCase("nd")) {
				// Se trata de un node, obtenemos su ref
				long ref = Long.parseLong(node.getAttributes().item(0)
						.getNodeValue());
				// Lo buscamos en la lista de nodos
				OsmNode nd = nodes.get(ref);
				if (nd != null) {
					// Si lo encontramos, lo añadimos a nuestro camino
					osmWay.addToWay(nd);
				}
			} else if (type.equalsIgnoreCase("tag")) {
				// Se trata de un tag
				osmWay.addTag(OsmTag.getTag(node));
			} else {
				// No deberiamos llegar aqui
			}
			node = node.getNextSibling();
		}
		if (!osmWay.getWay().isEmpty() && !osmWay.getTags().isEmpty()) {
			// Si hemos reconocido nodos y nos interesa su tipo
			osmWay.setType(Osm.getNodeType(osmWay.getTags()));
			return osmWay;
		} else {
			return null;
		}
	}

	/**
	 * Join two ways
	 * 
	 * @param r
	 * @param gridBox
	 * @return {@link OsmWay}
	 */
	public static OsmWay join(OsmRelation r, LatLngBox gridBox) {
		// Pegamos todos los de la primera lista
		// System.err.println("Haciendo Join de "+r);
		OsmWay osmWay = new OsmWay(-1);
		// OsmNode last = null;
		for (OsmMember m : r.getMembers()) {
			OsmWay way = m.getWay();
			// System.err.println("\t"+way);
			// if (last != null &&
			// last.getCoord().distance(way.getFirstNode().getCoord()) <
			// gridBox.getTileSize() * 3){
			// System.err.println("Reverse Join");
			// osmWay.addAllToWay(reverseJoin(way, gridBox));
			// }else{
			// System.err.println("Normal Join "+way);
			osmWay.addAllToWay(normalJoin(way, gridBox));
			// }
			// last = osmWay.getLastNode();
		}

		// Cerramos el camino, Unimos el ultimo de la segunda, con el primero de
		// la primera
		osmWay.addToWay(osmWay.getNode(0));
		osmWay.setType(r.getType());
		// System.err.println("Resultado " + osmWay);
		// Adaptamos el box
		return osmWay;
	}

	/**
	 * Joins a way
	 * 
	 * @param way
	 * @param gridBox
	 * @return List of nodes
	 */
	@SuppressWarnings("unused")
	private static List<OsmNode> reverseJoin(OsmWay way, LatLngBox gridBox) {
		List<OsmNode> reverseList = new ArrayList<OsmNode>();
		for (OsmEdge e : way.getReverseEdge()) {
			OsmNode n = e.getCutNode(gridBox);
			if (n != null) {
				reverseList.add(n);
			}
		}
		return reverseList;
	}

	/**
	 * Joins a way
	 * 
	 * @param way
	 * @param gridBox
	 * @return List of nodes
	 */
	private static List<OsmNode> normalJoin(OsmWay way, LatLngBox gridBox) {
		List<OsmNode> normalList = new ArrayList<OsmNode>();
		for (OsmEdge e : way.getEdges()) {
			OsmNode n = e.getCutNode(gridBox);
			if (n != null) {
				normalList.add(n);
			}
		}
		return normalList;
	}

}
