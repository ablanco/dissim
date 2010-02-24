package osm;

import org.w3c.dom.Node;

import util.Logger;

public class Streets {

	private Node root;
	private Logger streetsLogger;
	

	
	// url="http://api.openstreetmap.org/api/0.6/map?bbox=11.54,48.14,11.543,48.145";
	public Streets(Node root) {
		this.root = root;
	}

}
