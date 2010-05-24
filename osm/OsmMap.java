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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import util.HexagonalGrid;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

/**
 * This class is an interface for the OSM, Webservice, so we can manage all
 * usefull information about the map and manage easily
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class OsmMap {

	protected Hashtable<Long, OsmWay> ways;
	protected Hashtable<Long, OsmNode> nodes;
	protected List<OsmRelation> relations;
	protected List<OsmTag> tags;
	protected LatLngBox mapBox;

	/**
	 * Empty constructor, initializes ways, nodes, tags, relations in witch all
	 * info will be stored
	 */
	public OsmMap() {
		ways = new Hashtable<Long, OsmWay>();
		nodes = new Hashtable<Long, OsmNode>();
		tags = new ArrayList<OsmTag>();
		relations = new ArrayList<OsmRelation>();
	}

	/**
	 * Each OsmMap is contained into a box of two coordinates, NorthWest, and
	 * SouthEast
	 * 
	 * @return Box containing map
	 */
	public LatLngBox getMapBox() {
		return mapBox;
	}

	/**
	 * Returns a table of all the nodes contained in the map, ordered by osm id
	 * 
	 * @return All the Nodes of the map
	 */
	public Hashtable<Long, OsmNode> getNodes() {
		return nodes;
	}

	/**
	 * Returns a table of all the osmWays contained in the map, ordered by osm
	 * id
	 * 
	 * @return All the osmWays in the map
	 */
	public Hashtable<Long, OsmWay> getWays() {
		return ways;
	}

	/**
	 * Returns a list of all the osmRelations contained in the map
	 * 
	 * @return OsmRelations in the map
	 */
	public List<OsmRelation> getRelations() {
		return relations;
	}

	/**
	 * Sets Box of the map
	 * 
	 * @param mapBox
	 *            new MapBox of the map
	 */
	public void setMapBox(LatLngBox mapBox) {
		this.mapBox = mapBox;
	}

	/**
	 * Adds a new osmWay to the osmWays table, only if is not null
	 * 
	 * @param way
	 *            we want to add to the osmWay table
	 * @return returns previous (if same id) value of the list
	 */
	public OsmWay addWay(OsmWay way) {
		if (way != null) {
			return ways.put(way.getId(), way);
		}
		return null;
	}

	/**
	 * Adds a new osmNode to the osmNode table, only if is not null
	 * 
	 * @param node
	 *            we want to add to the osmNode table
	 * @return previous value, if same id, null if node == null or not
	 *         prevoiusly inserted
	 */
	public OsmNode addNode(OsmNode node) {
		if (node != null) {
			return nodes.put(node.getId(), node);
		}
		return null;
	}

	/**
	 * Adds a new osmRelation to the osmRelation list, only if is not null
	 * 
	 * @param relation
	 *            we want to add to the osmNode table
	 * @return true if added, false if not.
	 */
	public boolean addRelation(OsmRelation relation) {
		if (relation != null) {
			return relations.add(relation);
		}
		return false;
	}

	/**
	 * Checks if an osmNode is inside the map
	 * 
	 * @param n
	 *            node we want to chek
	 * @return true if is inside de Box, false if not
	 */
	public boolean isIn(OsmNode n) {
		return mapBox.contains(n.getCoord());
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (OsmWay w : ways.values()) {
			result.append(w.toString() + "\n");
		}

		for (OsmNode n : nodes.values()) {
			result.append(n.toString() + "\n");
		}

		for (OsmRelation r : relations) {
			result.append(r + "\n");
		}
		return result.toString();
	}

	/**
	 * Get info from Open Streets Maps, it uses the OSM webservice, obtain the
	 * xml file, parse it and puts values into the grid * @param grid we want to
	 * obtain the info
	 * 
	 * @return OsmMap object containing info from OSM
	 */
	public static OsmMap getMap(HexagonalGrid grid) {
		LatLngBox box = grid.getBox();

		LatLng NW = box.getNw();
		LatLng SE = box.getSe();

		// Open Streets Maps uses a differente mapBox, NE, SW
		String url = "http://api.openstreetmap.org/api/0.6/map?bbox=";
		String mBox = NW.getLng() + "," + SE.getLat() + "," + SE.getLng() + ","
				+ NW.getLat();
		String fileName = "map?bbox=" + mBox;
		url += mBox;
		System.err.println("Obtaining info from :" + url);
		File xmlFile = Osm.getOSMXmlFromURL(url, fileName);
		System.err.println("Reading file: " + xmlFile.getAbsolutePath());
		// parse XML file -> XML document will be build
		Document doc = null;
		Node node = null;
		try {
			doc = Osm.parseFile(xmlFile);
			// get root node of xml tree structure
			node = doc.getDocumentElement();
			// write node and its child nodes into System.out

		} catch (NullPointerException e) {
			// A ocurrido un error al obtener el xml, abortado calles
			System.err.println("Error obtaining Streets, no map generated");
			return null;
		}
		OsmMap osmMap = new OsmMap();
		// <?xml version="1.0" encoding="UTF-8"?>
		// <osm version="0.6" generator="CGImap 0.0.2">
		// <bounds minlat="29.925797" minlon="-90.075409" maxlat="29.947426"
		// maxlon="-90.046214"/>
		// Pasamos de todo eso
		osmMap.setMapBox(grid.getBox());
		node = node.getFirstChild().getNextSibling().getNextSibling();
		// Seguimos con todos los demás atributos
		while (node != null) {
			String type = node.getNodeName();
			if (type.equalsIgnoreCase("node")) {
				OsmNode nd = OsmNode.getNode(node);
				// if (!nd.isSimpleNode()) {
				// // Solo añadimos los puntos de los sitios de interes
				// nd.setPoint(grid.coordToTile(nd.getCoord()));
				// }
				// System.err.println("\t" + nd);
				osmMap.addNode(nd);
			} else if (type.equalsIgnoreCase("way")) {
				OsmWay w = OsmWay.getOsmWay(node, osmMap.getNodes());
				// Actualizamos el Box a los valores del grid principal
				osmMap.addWay(w);
			} else if (type.equalsIgnoreCase("relation")) {
				OsmRelation r = OsmRelation.getRelation(node, osmMap.getWays());
				// System.err.println("\t" + r);
				// Actualizamos a un box general
				if (r != null) {
					for (OsmMember member : r.getMembers()) {
						r.getBox().addToBox(member.getWay());
					}
					// Y ahora con los valores del grid principal
					osmMap.addRelation(r);
				}
			} else if (type.equalsIgnoreCase("#text")) {
				// skipping
			} else {
				System.err.println("\tEtiqueta no reconocida " + type);
			}
			node = node.getNextSibling();
		}
		return osmMap;
	}

}
