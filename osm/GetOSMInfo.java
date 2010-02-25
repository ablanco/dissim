package osm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import util.jcoord.LatLng;

public class GetOSMInfo {
	private Document doc;
	private Node root;
	private Logger xmlLog;

	/** Creates a new instance of XmlParser */
	public GetOSMInfo(LatLng[] mapBox) {
		//Open Streets Maps uses a differente mapBox, NE, SW
		xmlLog = new Logger();
		String url ="http://api.openstreetmap.org/api/0.6/map?bbox=";
		url +=mapBox[0].getLng()+","+mapBox[1].getLat();
		url +=","+mapBox[1].getLng()+","+mapBox[0].getLat();
		xmlLog.println("Obtaining info from :"+url);
		File xmlFile = getOSMXmlFromURL(url);
		
		// parse XML file -> XML document will be build
		doc = parseFile(xmlFile.getPath());
		// get root node of xml tree structure
		root = doc.getDocumentElement();
		// write node and its child nodes into System.out
		xmlLog.println("Statemend of XML document...");
		//writeDocumentToLog(root, 0);
		xmlToStreets(root);
		xmlLog.println("... end of statement");
	}
	
	protected OsmMap xmlToStreets(Node root){
		OsmMap map = null;
		// get element name
		String nodeName = root.getNodeName();
		// get element value
		String nodeValue = getElementValue(root);
		// get attributes of element
		NamedNodeMap attributes = root.getAttributes();
		xmlLog.debugln("NodeName: " + nodeName
				+ ", NodeValue: " + nodeValue);
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			xmlLog.debugln("AttributeName: "
					+ attribute.getNodeName() + ", attributeValue: "
					+ attribute.getNodeValue());
		}
		/*
		// write all child nodes recursively
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				writeDocumentToLog(child, indent + 2);
			}
		}*/
		return map;
	}

	protected OsmNode getNodeInfo(Node node){
		String nodeName = node.getNodeName();
		if ("node"==nodeName){
			NamedNodeMap attributes = root.getAttributes();
			long id = Long.parseLong(attributes.item(0).getNodeValue());
			double lat = Double.parseDouble(attributes.item(1).getNodeValue());
			double lng =Double.parseDouble(attributes.item(2).getNodeValue());
			xmlLog.debugln("Creating node: "+id+"("+lat+","+lng+")");
			return new OsmNode(id, new LatLng(lat, lng));	
		}else{
			return null;
		}
		
	}
	
	/**
	 * Given a proper url for OSM returns a file with the information 
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
				xmlLog.debugln("New Directory in: " + f.getPath());
			} 

			if (!Wget.wget(f.getPath(), url)) {
				xmlLog.debugln("Cannot obtain "+url+", into :"+f.getPath());
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
		xmlLog.debugln(getIndentSpaces(indent) + "NodeName: " + nodeName
				+ ", NodeValue: " + nodeValue);
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			xmlLog.debugln(getIndentSpaces(indent + 2) + "AttributeName: "
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
	
	public Document getDocument(){
		return doc;
	}
	
	public Node getRoot(){
		return root;
	}
	
	

}
