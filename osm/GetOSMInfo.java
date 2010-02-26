package osm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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

import util.Logger;
import util.Point;
import util.Scenario;
import util.jcoord.LatLng;

public class GetOSMInfo {
	private Document doc;
	private Node root;
	private Logger osmLog;
	private Scenario scene;

	/** Creates a new instance of XmlParser */
	public GetOSMInfo(Scenario scene) {
		// Open Streets Maps uses a differente mapBox, NE, SW
		this.scene = scene;
		LatLng[] mapBox = scene.getSimulationArea();
		osmLog = new Logger();
		String url = "http://api.openstreetmap.org/api/0.6/map?bbox=";
		url += mapBox[0].getLng() + "," + mapBox[1].getLat();
		url += "," + mapBox[1].getLng() + "," + mapBox[0].getLat();
		osmLog.println("Obtaining info from :" + url);
		File xmlFile = getOSMXmlFromURL(url);

		// parse XML file -> XML document will be build
		doc = parseFile(xmlFile.getPath());
		// get root node of xml tree structure
		root = doc.getDocumentElement();
		// write node and its child nodes into System.out
		osmLog.println("Statemend of XML document...");
		// writeDocumentToLog(root, 0);
		xmlToStreets(root);
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
		// Children has the info
		NodeList mapInfoChilds = mapInfo.getChildNodes();
		// First child has Continent name
		attributes = mapInfoChilds.item(1).getAttributes();
		String continent = attributes.item(1).getNodeValue();
		// Second child has the name of the place
		attributes = mapInfoChilds.item(3).getAttributes();
		String name = attributes.item(1).getNodeValue();
		// Forth child contains place type
		mapInfo = mapInfoChilds.item(6).getNextSibling();
		attributes = mapInfo.getAttributes();
		String place = attributes.item(1).getNodeValue();
		// Now we've got all information
		osmLog.debugln(continent + " " + place + ", " + name);
		osmMap = new OsmMap(id, continent, name, place);

		SortedMap<Long, OsmNode> osmNodes = new TreeMap<Long, OsmNode>();
		// When this methods finised we've shuld have the first way
		getNodeInfo(root.getNextSibling().getNextSibling(), osmNodes,
				osmMap.ways);

		return osmMap;
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

	protected void getNodeInfo(Node node, SortedMap<Long, OsmNode> osmNodes,
			SortedSet<OsmWay> ways) {
		String nodeName = node.getNodeName();
		if ("node" == nodeName) {
			NamedNodeMap attributes = node.getAttributes();
			long id = Long.parseLong(attributes.item(0).getNodeValue());
			double lat = Double.parseDouble(attributes.item(1).getNodeValue());
			double lng = Double.parseDouble(attributes.item(2).getNodeValue());
			OsmNode osmNode;
			try {
				osmNode = new OsmNode(id, new Point(0,0));//scene.coordToTile(new LatLng(lat, lng)));
				osmLog.debugln("new node: " + osmNode.toString());
				osmNodes.put(id, osmNode);
			} catch (Exception e) {
				osmNodes.put(id, new OsmNode(id, lat, lng));
			}
			getNodeInfo(node.getNextSibling(), osmNodes, ways);
		} else if ("#text" == nodeName) {
			getNodeInfo(node.getNextSibling(), osmNodes, ways);
		} else {
			osmLog.debugln("we've reached the end of nodes begin: " + nodeName);

			getWayInfo(node, osmNodes, ways);
		}
	}

	protected void getWayInfo(Node way, SortedMap<Long, OsmNode> osmNodes,
			SortedSet<OsmWay> osmWays) {
		String nodeName = way.getNodeName();
		if ("way" == nodeName) {
			NamedNodeMap attributes = way.getAttributes();
			long id = Long.parseLong(attributes.item(0).getNodeValue());
			OsmWay osmWay = new OsmWay(id);
			osmWays.add(osmWay);
			NodeList wayNodesList = way.getChildNodes();
			for (int i = 0; i < wayNodesList.getLength(); i++) {
				Node n = wayNodesList.item(i);
				// Here comes ID
				if (n.getNodeName().equalsIgnoreCase("nd")) {
					String key = n.getAttributes().item(0).getNodeValue();
					OsmNode on = osmNodes.get(Long.parseLong(key));
					if (on != null) {
						osmWay.addToWay(on);
						osmNodes.remove(Long.parseLong(key));
					}
					// Here comes Info
				} else if (n.getNodeName().equalsIgnoreCase("tag")) {
					String value = n.getAttributes().item(0).getNodeValue();
					if (value.equalsIgnoreCase("highway")) {
						String type = n.getAttributes().item(1).getNodeValue();
						// osmLog.debug("Type: "+type);
						osmWay.setType(type);
					} else if (value.equalsIgnoreCase("name")) {
						String name = n.getAttributes().item(1).getNodeValue();
						// osmLog.debug("Name: "+name);
						osmWay.setName(name);
					} else if (value.equalsIgnoreCase("oneway")) {
						String oneWay = n.getAttributes().item(1)
								.getNodeValue();
						// osmLog.debug("OneWay: "+oneWay);
						osmWay.setOneWay(oneWay);
					} else {
						// osmLog.debug(", item: "+value);
					}
				} else {
					// not interested
				}
			}
			osmLog.debugln("new way: " + osmWay);
			getWayInfo(way.getNextSibling(), osmNodes, osmWays);
		} else if ("#text" == nodeName) {
			getWayInfo(way.getNextSibling(), osmNodes, osmWays);
		} else if ("node" == nodeName) {
			osmLog.debugln("que coño hace aqui un nodo???");
			getWayInfo(way.getNextSibling(), osmNodes, osmWays);
		} else {
			osmLog.debugln("We've reached the end of way begins: " + nodeName);
		}
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
			File f = File.createTempFile("OSM", "temp");
			if (f.exists()) {
				f.delete();
			}
			if (f.mkdir()) {
				osmLog.debugln("New Directory in: " + f.getPath());
			}

			if (!Wget.wget(f.getPath(), url)) {
				osmLog.debugln("Cannot obtain " + url + ", into :"
						+ f.getPath());
			}

			f.deleteOnExit();
			File[] xmlFile = f.listFiles();

			file = xmlFile[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

}
