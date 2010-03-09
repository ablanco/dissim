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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import openwfe.org.misc.Wget;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.HexagonalGrid;
import util.Logger;
import util.jcoord.LatLng;

public class GetOSMInfo {
	private Document doc;
	private Node root;
	private Logger osmLog;
	private HexagonalGrid grid;
	private OsmMap osmMap;
	private String fileName;

	/** Creates a new instance of XmlParser */
	public GetOSMInfo(HexagonalGrid grid) {
		this.grid = grid;
		LatLng NW = grid.getArea()[0];
		LatLng SE = grid.getArea()[1];

		// Open Streets Maps uses a differente mapBox, NE, SW
		osmLog = new Logger();
		String url = "http://api.openstreetmap.org/api/0.6/map?bbox=";
		String mBox = NW.getLng() + "," + SE.getLat() + "," + SE.getLng() + ","
				+ NW.getLat();
		fileName = "map?bbox=" + mBox;
		url += mBox;
		osmLog.println("Obtaining info from :" + url);
		File xmlFile = getOSMXmlFromURL(url);
		// System.err.println("Reading file: "+xmlFile.getAbsolutePath());
		// parse XML file -> XML document will be build
		doc = parseFile(xmlFile.getPath());
		// get root node of xml tree structure
		root = doc.getDocumentElement();
		// write node and its child nodes into System.out
		osmLog.println("Statemend of XML document...");
		// writeDocumentToLog(root, 0);
		osmMap = xmlToStreets(root);
		osmLog.println("... end of statement");
	}

	protected OsmMap xmlToStreets(Node root) {
		OsmMap osmMap = null;
		// Skips osm and bounds
		root = root.getFirstChild().getNextSibling().getNextSibling();
		// gets First Node contains info about location
		Node mapInfo = root.getNextSibling();
		// Root node has id
		NamedNodeMap attributes = mapInfo.getAttributes();
		long id = Long.parseLong(attributes.item(0).getNodeValue());
		osmMap = new OsmMap(id);
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
		getNodeInfo(root.getNextSibling().getNextSibling(), osmNodes, osmMap);
		adaptNodesMap();
		// osmLog.debugln(osmMap.toString());
		return osmMap;
	}

	private void adaptNodesMap() {

	}

	protected void getNodeInfo(Node node, SortedMap<Long, OsmNode> osmNodes,
			OsmMap osmMap) {
		while (node != null) {
			String nodeName = node.getNodeName();
			if (nodeName.equalsIgnoreCase("node")) {
				getNodeInfoNode(node, osmNodes, osmMap.specialPlaces);
			} else if (nodeName.equalsIgnoreCase("way")) {
				getNodeInfoWay(node, osmNodes, osmMap.ways);
			} else {
				// Skipping
			}
			node = node.getNextSibling();
		}
	}

	protected void getNodeInfoNode(Node node,
			SortedMap<Long, OsmNode> osmNodes, SortedSet<OsmNode> specialPlaces) {
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
		//Añadimos el punto del grid/punto aproximado del grid (mirar variable isIn)
		osmNode.setPoint(grid.coordToTile(latLng));
		// If is extended Node:
		if (node.getFirstChild() != null) {
			// Is a special Place
			osmNode.setExtendedInfo(OsmMap
					.getExtendedInfo(node.getFirstChild()));
			// Adding to Special Places
			specialPlaces.add(osmNode);
		} else {
			// Adding node to map of nodes
			osmNodes.put(id, osmNode);
		}
		// osmLog.debugln(osmNode.toString());
	}

	protected void getNodeInfoWay(Node node, SortedMap<Long, OsmNode> osmNodes,
			SortedSet<OsmWay> ways) {
		// Getting IDattributes
		long id = Long.parseLong(node.getAttributes().item(0).getNodeValue());
		// Creating the way and adding to map
		OsmWay osmWay = new OsmWay(id);
		ways.add(osmWay);
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
						// System.err.println("*****Dentro: " + aux.toString());
						in = true;
						out = false;
					} else {
						// No esta dentro de las coordenadas, tenemos que buscar
						// el primero y el ultimo que si que lo esten para poder
						// interpolar la posición hasta el extremo al que
						// debemos pintar
						if (out) {
							osmWay.setFirsNode(aux);
							// System.err.println("Primero fuera: " +
							// aux.toString());
						}
						if (in) {
							osmWay.setLastNode(aux);
							// System.err.println("Ultimo Fuera: " +
							// aux.toString());
							in = aux.isIn();
						}
					}
				}// Buscamos un key que no existe ... wtf osm??
			}
			node = node.getNextSibling();
		}
		// Obtaining extended info
		osmWay.setExtendedInfo(OsmMap.getExtendedInfo(node));
		// osmLog.debugln(osmWay.toString());
	}

	/**
	 * Given a proper url for OSM returns a file with the information
	 * 
	 * @param url
	 * @return
	 */
	protected File getOSMXmlFromURL(String url) {
		File file = null;
		try {
			File f = File.createTempFile("CatastrofesOpenStreetMaps", null);
			String path = f.getAbsolutePath();
			path = (String) path.subSequence(0, path.length()
					- f.getName().length());
			path += "PFC-Catastrofes" + File.separator;
			f.deleteOnExit();
			f.delete();

			// Opens File Dir
			File osmMap = new File(path);

			if (!osmMap.exists()) {
				osmMap.mkdir();
				// osmLog.debugln("New Directory in: " + osmMap.getPath());
			}

			file = new File(path + fileName);
			if (!file.exists()) {
				if (!Wget.wget(path, url)) {
					// osmLog.debugln("Cannot obtain " + url + ", into :"
					// + path);
				} else {
					System.err.println("Getting File info");
					return new File(path + fileName);
				}
			} else {
				// System.err.println("File info cached");
				return file;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Returns element value
	 * 
	 * @param elem
	 *            element (it is XML tag)
	 * @return Element value otherwise empty String
	 */
	protected final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	private String getIndentSpaces(int indent) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			buffer.append(" ");
		}
		return buffer.toString();
	}

	/**
	 * Writes node and all child nodes into System.out
	 * 
	 * @param node
	 *            XML node from from XML tree wrom which will output statement
	 *            start
	 * @param indent
	 *            number of spaces used to indent output
	 */
	protected void writeDocumentToLog(Node node, int indent) {
		// get element name
		String nodeName = node.getNodeName();
		// get element value
		String nodeValue = getElementValue(node);
		// get attributes of element
		NamedNodeMap attributes = node.getAttributes();
		osmLog.debugln(getIndentSpaces(indent) + "NodeName: " + nodeName
				+ ", NodeValue: " + nodeValue);
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			osmLog.debugln(getIndentSpaces(indent + 2) + "AttributeName: "
					+ attribute.getNodeName() + ", attributeValue: "
					+ attribute.getNodeValue());
		}
		// write all child nodes recursively
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				writeDocumentToLog(child, indent + 2);
			}
		}
	}

	/**
	 * Saves XML Document into XML file.
	 * 
	 * @param fileName
	 *            XML file name
	 * @param doc
	 *            XML document to save
	 * @return <B>true</B> if method success <B>false</B> otherwise
	 */
	protected boolean saveXMLDocument(String fileName, Document doc) {
		System.out.println("Saving XML file... " + fileName);
		// open output stream where XML Document will be saved
		File xmlOutputFile = new File(fileName);
		FileOutputStream fos;
		Transformer transformer;
		try {
			fos = new FileOutputStream(xmlOutputFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error occured: " + e.getMessage());
			return false;
		}
		// Use a Transformer for output
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			System.out.println("Transformer configuration error: "
					+ e.getMessage());
			return false;
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fos);
		// transform source into result will do save
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			System.out.println("Error transform: " + e.getMessage());
		}
		System.out.println("XML file saved.");
		return true;
	}

	/**
	 * Parses XML file and returns XML document.
	 * 
	 * @param fileName
	 *            XML file to parse
	 * @return XML document or <B>null</B> if error occured
	 */
	public Document parseFile(String fileName) {
		System.out.println("Parsing XML file... " + fileName);
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
		File sourceFile = new File(fileName);
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

	public Document getDocument() {
		return doc;
	}

	public Node getRoot() {
		return root;
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

	public OsmMap getOsmMap() {
		return osmMap;
	}

	public void fillMatrix() {
		osmMap.setMapInfo(grid);
	}
}
