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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import openwfe.org.misc.Wget;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.HexagonalGrid;
import util.Util;
import util.jcoord.LatLng;

public class Osm {

	/**
	 * Get And Fill streets Matrix for this Grid from Open Streets Maps
	 * @param grid
	 * @return
	 */
	public static OsmMap getMap(HexagonalGrid grid) {
		LatLng NW = grid.getArea()[0];
		LatLng SE = grid.getArea()[1];

		// Open Streets Maps uses a differente mapBox, NE, SW
		String url = "http://api.openstreetmap.org/api/0.6/map?bbox=";
		String mBox = NW.getLng() + "," + SE.getLat() + "," + SE.getLng() + ","
				+ NW.getLat();
		String fileName = "map?bbox=" + mBox;
		url += mBox;
		// ("Obtaining info from :" + url);
		File xmlFile = getOSMXmlFromURL(url, fileName);
		System.err.println("Reading file: "+xmlFile.getAbsolutePath());
		// parse XML file -> XML document will be build
		Document doc = parseFile(xmlFile);
		// get root node of xml tree structure
		Node root = doc.getDocumentElement();
		// write node and its child nodes into System.out
		// writeDocumentToLog(root, 0);
		return xmlToStreets(root,grid);
	}

	/**
	 * Dado el nodo root del xml de OSM nos devuelve OsmMap con toda la
	 * información que necesitamos
	 * 
	 * @param root
	 *            Root element de un xml obtenido de OSM
	 * @return Clase con toda la información que necesitamos de OSM
	 */
	protected static OsmMap xmlToStreets(Node root, HexagonalGrid grid) {
		// Skips osm and bounds
		root = root.getFirstChild().getNextSibling().getNextSibling();
		// gets First Node contains info about location
		Node mapInfo = root.getNextSibling();
		// Root node has id
		NamedNodeMap attributes = mapInfo.getAttributes();
		long id = Long.parseLong(attributes.item(0).getNodeValue());
		OsmMap osmMap = new OsmMap(id);
		// Children has the info
		NodeList mapInfoChilds = mapInfo.getChildNodes();
		if (mapInfo.getFirstChild() != null) {// First child has Continent name
			attributes = mapInfoChilds.item(1).getAttributes();
			osmMap.setContinent(attributes.item(1).getNodeValue());
			// Second child has the name of the place
			attributes = mapInfoChilds.item(3).getAttributes();
			osmMap.setName(attributes.item(1).getNodeValue());
			// Forth child contains place type
			mapInfo = mapInfoChilds.item(6).getNextSibling();
			attributes = mapInfo.getAttributes();
			osmMap.setPlace(attributes.item(1).getNodeValue());
			// Now we've got all information
		}
		SortedMap<Long, OsmNode> osmNodes = new TreeMap<Long, OsmNode>();
		// When this methods finised we've shuld have the first way
		getNodeInfo(root.getNextSibling().getNextSibling(), osmNodes, osmMap, grid);
		// Now we sort the list
		Collections.sort(osmMap.ways, new WaysComparator());
		//Now we fill the streets matrix
		osmMap.setMapInfo(grid);
		//Now Returns All the info, if needed ...
		return osmMap;
	}

	/**
	 * Dado un nodo lo clasifica y lo añade a un OsmMap
	 * 
	 * @param node
	 *            Nodo Root de un xml de OSM
	 * @param osmNodes
	 *            Map con todos los nodos
	 * @param osmMap
	 *            Clase donde tendremos toda la informacion que necesitamos de
	 *            OSM
	 */
	protected static void getNodeInfo(Node node, SortedMap<Long, OsmNode> osmNodes,
			OsmMap osmMap, HexagonalGrid grid) {
		while (node != null) {
			// Hasta llegar al final del XML
			String nodeName = node.getNodeName();
			if (nodeName.equalsIgnoreCase("node")) {
				// Es un nodo, de informacion o de sitio especial
				getNodeInfoNode(node, osmNodes, osmMap.specialPlaces, grid);
			} else if (nodeName.equalsIgnoreCase("way")) {
				// Forma parte de un Way
				getNodeInfoWay(node, osmNodes, osmMap.ways);
			} else {
				// Informacion que no nos interesa, que es member???
				// Skipping
			}
			node = node.getNextSibling();
		}
	}

	/**
	 * Dado un Nodo Node, que son los puntos del mapa, extraemos toda su
	 * información. Pueden ser de tod tipos, nodos normales o nodos especiales
	 * (edificios, aeropuertos ...)
	 * 
	 * @param node
	 *            Nodo que queremos analizar
	 * @param osmNodes
	 *            Map con todos los nodos
	 * @param specialPlaces
	 *            Set con los nodos especiales.
	 */
	protected static void getNodeInfoNode(Node node,
			SortedMap<Long, OsmNode> osmNodes, List<OsmNode> specialPlaces, HexagonalGrid grid) {
		NamedNodeMap attributes = node.getAttributes();
		// Getting attributes
		long id;
		double lat;
		double lng;
		try {
			id = Long.parseLong(attributes.item(1).getNodeValue());
			lat = Double.parseDouble(attributes.item(2).getNodeValue());
			lng = Double.parseDouble(attributes.item(3).getNodeValue());
			// Weird error while parsin xml file
			System.err.println("Are you at the etsii??");
		} catch (Exception e) {
			id = Long.parseLong(attributes.item(0).getNodeValue());
			lat = Double.parseDouble(attributes.item(1).getNodeValue());
			lng = Double.parseDouble(attributes.item(2).getNodeValue());
		}
		LatLng latLng = new LatLng(lat, lng);
		OsmNode osmNode = new OsmNode(id, latLng);
		// System.err.println("Nodo leido: "+osmNode.toString()+" Coordenadas: "+latLng.toString()+" lat: "+lat+", lng; "+lng);
		// Añadimos el punto del grid/punto aproximado del grid (mirar variable
		// isIn)
		osmNode.setPoint(grid.coordToTile(latLng));
		// If is extended Node:
		if (node.getFirstChild() != null) {
			// Is a special Place
			osmNode.setExtendedInfo(OsmInf
					.getExtendedInfo(node.getFirstChild()));
			// Adding to Special Places
			if (osmNode.getExtendedInfo().getKey() != OsmInf.Undefined) {
				specialPlaces.add(osmNode);
			} else {
				System.err.println("Undefinde node: " + osmNode);
			}
		} else {
			// Adding node to map of nodes
			osmNodes.put(id, osmNode);
		}
	}

	/**
	 * Dado un Nodo WAY extrae su información y lo añade a la lista de Ways y le
	 * añade todos los nodos contenidos en el Map. No los elemina porque se ha
	 * dado el caso que un nodo puede estar en varios Ways
	 * 
	 * @param node
	 *            nodo del xml que estamos reconociendo
	 * @param osmNodes
	 *            Map con todos los nodos reconocidos esperando a ser asignados
	 *            a un way
	 * @param ways
	 *            Acumulador de Ways que hemos ido reconociendo
	 */
	protected static void getNodeInfoWay(Node node, SortedMap<Long, OsmNode> osmNodes,
			List<OsmWay> ways) {
		// Getting IDattributes
		long id = Long.parseLong(node.getAttributes().item(0).getNodeValue());
		// Creating the way and adding to map
		OsmWay osmWay = new OsmWay(id);

		node = node.getFirstChild();
		// Getting Nodes id to add way
		boolean in = false;
		boolean out = true;
		while (node != null && !node.getNodeName().equalsIgnoreCase("tag")) {
			if (node.getNodeName().equalsIgnoreCase("nd")) {
				// es un nodo
				long key = Long.parseLong(node.getAttributes().item(0)
						.getNodeValue());
				// Adding to way
				OsmNode aux = osmNodes.get(key);
				if (aux != null) {
					// Tenemos el nodo que buscamos
					if (aux.isIn()) {
						// Esta dentro de las coordenadas
						osmWay.addToWay(aux);
						in = true;
						out = false;
					} else {
						// No esta dentro de las coordenadas, tenemos que buscar
						// el primero y el ultimo que si que lo esten para poder
						// interpolar la posición hasta el extremo al que
						// debemos pintar
						if (out) {
							osmWay.setFirsNode(aux);
							// Primero fuera:
						}
						if (in) {
							osmWay.setLastNode(aux);
							// "Ultimo Fuera: "
							in = aux.isIn();
						}
					}
				}// Buscamos un key que no existe ... wtf osm??
			}
			node = node.getNextSibling();
		}
		// Obtaining extended info and Priority
		osmWay.setExtendedInfo(OsmInf.getExtendedInfo(node));
		// Ahora que tenemos la prioridad añadimos el Way
		ways.add(osmWay);
	}

	/**
	 * Given a proper url for OSM returns a file with the information
	 * @param url Url for the web service
	 * @param fileName Name for the file
	 * @return
	 */
	protected static File getOSMXmlFromURL(String url, String fileName) {
		try {
			// creamos un fichero en el directorio temporal
			File dir = Util.getDefaultTempDir();
			File file = new File(dir, fileName);
			if (!file.exists()) {
				if (!Wget.wget(dir.getPath(), url)) {
					System.err
							.println("No se ha podido descargar la informacion");
					return null;
				} else {
					// Si se ha descargado devolvemos el fichero
					file = new File(dir, fileName);
					System.out.println("Se ha descargado el archivo "
							+ file.getPath());
					return file;
				}
			} else {
				// Fichero ya descargado, lo devolvemos
				System.out.println("Peticion ya en disco " + file.getName());
				return file;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Unrecheable
		return null;
	}

	/**
	 * Parses XML file and returns XML document.
	 * 
	 * @param fileName
	 *            XML file to parse
	 * @return XML document or <B>null</B> if error occured
	 */
	public static Document parseFile(File sourceFile) {
		System.out.println("Parsing XML file... " + sourceFile.getName());
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		try {
			doc = docBuilder.parse(sourceFile);
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("Could not read source file: " + e.getMessage());
		}
		System.out.println("XML file parsed");
		return doc;
	}

	protected void printNodeInfo(Node node) {
		System.out.println();
		System.out.print("Name: " + node.getNodeName() + ",Value: "
				+ node.getNodeValue() + " - ");
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			for (int j = 0; j < attributes.getLength(); j++) {
				System.out.print(attributes.item(j).getNodeValue() + ", ");
			}
		}

		NodeList infoChilds = node.getChildNodes();
		if (infoChilds != null) {
			for (int i = 0; i < infoChilds.getLength(); i++) {
				printNodeInfo(infoChilds.item(i));
			}
		}
		System.out.println("Fin de hijos");

	}
}
