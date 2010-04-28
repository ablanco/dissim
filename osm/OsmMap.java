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

public class OsmMap {

	protected Hashtable<Long, OsmWay> ways;
	protected Hashtable<Long, OsmNode> nodes;
	protected List<OsmRelation> relations;
	protected List<OsmTag> tags;
	protected LatLngBox mapBox;

	public OsmMap() {
		ways = new Hashtable<Long, OsmWay>();
		nodes = new Hashtable<Long, OsmNode>();
		tags = new ArrayList<OsmTag>();
		relations = new ArrayList<OsmRelation>();
	}

	public LatLngBox getMapBox() {
		return mapBox;
	}

	public Hashtable<Long, OsmNode> getNodes() {
		return nodes;
	}

	public Hashtable<Long, OsmWay> getWays() {
		return ways;
	}

	public List<OsmRelation> getRelations() {
		return relations;
	}

	public void setMapBox(LatLngBox mapBox) {
		this.mapBox = mapBox;
	}

	public OsmWay addWay(OsmWay way) {
		if (way != null) {
			return ways.put(way.getId(), way);
		}
		return null;
	}

	public OsmNode addNode(OsmNode node) {
		if (node != null) {
			return nodes.put(node.getId(), node);
		}
		return null;
	}

	public boolean addRelation(OsmRelation relation) {
		if (relation != null) {
			return relations.add(relation);
		}
		return false;
	}

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
	 * Get info from Open Streets Maps
	 * 
	 * @param grid
	 * @return
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
//				if (!nd.isSimpleNode()) {
//					// Solo añadimos los puntos de los sitios de interes
//					nd.setPoint(grid.coordToTile(nd.getCoord()));
//				}
				// System.err.println("\t" + nd);
				osmMap.addNode(nd);
			} else if (type.equalsIgnoreCase("way")) {
				OsmWay w = OsmWay.getOsmWay(node, osmMap.getNodes());
				//Actualizamos el Box a los valores del grid principal
				osmMap.addWay(w);
			} else if (type.equalsIgnoreCase("relation")) {
				OsmRelation r = OsmRelation.getRelation(node, osmMap.getWays());
				// System.err.println("\t" + r);
				//Actualizamos a un box general 
				for (OsmMember member : r.getMembers()){
					r.getBox().addToBox(member.getWay());
				}
				//Y ahora con los valores del grid principal
				osmMap.addRelation(r);
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
