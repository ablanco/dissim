package osm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Node;

import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

public class OsmWay implements Comparable<OsmWay> {
	/**
	 * Contains OsmNodes in a certain orden that especify the road
	 */
	protected List<OsmNode> way;
	protected long id;
	/**
	 * Información Extendida de WAY
	 */
	protected List<OsmTag> tags;
	/**
	 * Priority of painting ROAD
	 */
	private List<OsmEdge> edges;

	protected short type = Osm.Undefined;
	private LatLngBox box = null;

	protected OsmWay(long id) {
		this.id = id;
		way = new ArrayList<OsmNode>();
		tags = new ArrayList<OsmTag>();
		box = new LatLngBox();
		edges = new ArrayList<OsmEdge>();
	}

	public List<OsmTag> getTags() {
		return tags;
	}

	public List<OsmEdge> getEdges() {
		return edges;
	}

	public LatLngBox getBox() {
		return box;
	}

	public OsmNode getNode(int ind) {
		if (!way.isEmpty() && ind >= 0 && ind < way.size()) {
			return way.get(ind);
		}
		return null;
	}

	public OsmNode getLastNode() {
		return getNode(way.size() - 1);
	}

	public void setType(short type) {
		this.type = type;
	}

	public void setBox(LatLngBox box) {
		this.box = box;
	}

	/**
	 * Construye el camino de nodos que forman la linea (deben de estar en
	 * orden) ademas crea sus aristas correspondientes y actualiza el box.
	 * 
	 * @param node
	 * @return
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

	private boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		}
		return false;
	}

	public long getId() {
		return id;
	}

	public short getType() {
		if (type == Osm.Undefined) {
			type = Osm.getNodeType(tags);
		}
		return type;
	}

	public List<OsmNode> getWay() {
		return way;
	}

	/**
	 * Devuelve una lista (punto por punto) que compone la linea del poligono,
	 * al discretizar algunos puntos pueden estar repetidos, asi que tenemos que
	 * llevar un control con el anterior para no devolver puntos repetidos
	 * 
	 * @param grid
	 *            La lista de puntos esta referenciada a este grid
	 * @return
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
	 * Given a coord returns true if it is into the poligonal line
	 * 
	 * @param coord
	 * @return
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
	 * es una linea cerrada si su ultimo nodo es igual que el primer nodo
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
		result.append("\n\t Nodes: ");
		for (OsmNode n : way) {
			result.append(n.toString() + ", ");
		}

		return result.toString();
	}

	@Override
	public int compareTo(OsmWay o) {
		return (int) (id - o.id);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (int) id;
	}

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
	 * Dados dos caminos, los une formando un solo poligono
	 * 
	 * @param way2
	 * @param way3
	 * @param type
	 * @return
	 */
	public static OsmWay join(OsmWay way2, OsmWay way3, short type,
			LatLngBox gridBox) {
		// Pegamos todos los de la primera lista
		System.err.println("Haciendo Join de " + way2.getId() + ", "
				+ way2.getType() + " con " + way3.getId() + ", "
				+ way3.getType());
		OsmWay osmWay = new OsmWay(-1);
		for (OsmNode n : way2.getWay()) {
			if (gridBox.contains(n.getCoord())) {
				osmWay.addToWay(n);
			}
		}
		// Le añadimos los de la segunda lista pero al reves
		ListIterator<OsmNode> reverseList = way3.getWay().listIterator();
		while (reverseList.hasPrevious()) {
			OsmNode prev = reverseList.previous();
			if (gridBox.contains(prev.getCoord())) {
				osmWay.addToWay(prev);
			}
		}
		// Unimos el ultimo de la segunda, con el primero de la primera
		osmWay.addToWay(osmWay.getNode(0));
		osmWay.setType(type);
		// Adaptamos el box
		return osmWay;
	}
}
