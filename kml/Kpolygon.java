package kml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import util.flood.Edge;
import util.flood.SizeComparator;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;

public class Kpolygon {

	public static final int RawType = 1;
	public static final int WaterType = 1;

	private List<Coordinate> outerLine;
	private List<List<Coordinate>> innerLines;
	private int type;
	private short deep;

	/**
	 * Recivimos una lista de puntos adyacentes y los ordenamos en bordes
	 * exteriores e interiores
	 * 
	 * @param type
	 * @param rawPolygon
	 */
	public Kpolygon(int type, List<LatLng> rawPolygon, double ilat, double ilng) {
		if (rawPolygon == null || rawPolygon.size() == 0) {
			throw new IllegalArgumentException(
					"No se ha pasado una lista de vertices valida para este poligono");
		}
		this.type = type;
		outerLine = new ArrayList<Coordinate>();
		innerLines = new ArrayList<List<Coordinate>>();
		List<List<Edge>> rawEdgeList = new ArrayList<List<Edge>>();
		for (LatLng l : rawPolygon) {
			rawEdgeList.add(getHexagonEdges(l, ilat, ilng));
		}
		System.err.println("Creando polygono, obteniendas todas las aristas "
				+ rawEdgeList);
		int size = rawEdgeList.size();
		// TODO mejorar eficiencia
		while (size > 1) {
			List<List<Edge>> edgeList = new ArrayList<List<Edge>>();
			// Mientras el tamaño sea mayor que uno no habre terminado
			if (size % 2 != 0) {
				size--;
			}
			// Voy sumando de dos en dos
			for (int i = 0; i < size; i = i + 2) {
				edgeList.add(borderOperator(rawEdgeList.get(i), rawEdgeList
						.get(i + 1)));
			}
			// Me quedo con la nueva lista
			rawEdgeList = edgeList;
			size = rawEdgeList.size();
		}
		System.err.println("Lista en bruto de las aristas "
				+ rawEdgeList.get(0));
		separateLines(rawEdgeList.get(0));
	}

	public List<Coordinate> getOuterLine() {
		return outerLine;
	}

	public List<List<Coordinate>> getInnerLines() {
		return innerLines;
	}

	public int getType() {
		return type;
	}

	public short getDeep() {
		return deep;
	}

	public void setDeep(short deep) {
		this.deep = deep;
	}

	// Posible orden? [b,c]-[a,f]+[a,b]-[f,e],+[c,d]-[e,d]
	// public static final int[] edgeOrder = new int[]{ 1,1,2, -1,0,5, 1,0,1,
	// -1,5,4, 1,2,3, -1,4,3};

	private List<Coordinate> getHexagonVertex(LatLng centre, double ilat,
			double ilng) {
		final double[] f = new double[] { 1, 0, 0.5, 1, -0.5, 1, -1, 0, -0.5,
				-1, 0.5, -1, 1, 0 };
		double lat = centre.getLat();
		double lng = centre.getLng();
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		for (int i = 0; i < f.length; i = i + 2) {
			Coordinate c = new Coordinate(f[i + 1] * ilng + lng, f[i] * ilat
					+ lat, centre.getAltitude());
			coordinates.add(c);
		}
		return coordinates;
	}

	private List<Edge> getHexagonEdges(LatLng centre, double ilat, double ilng) {
		if (centre == null) {
			throw new IllegalArgumentException();
		}
		ArrayList<Edge> edges = new ArrayList<Edge>();
		// return a,b,c,d,e,f,g
		List<Coordinate> vertex = getHexagonVertex(centre, ilat, ilng);
		final int[] edgeOrder = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0 };
		for (int i = 0; i < edgeOrder.length; i = i + 2) {
			edges.add(new Edge(1, vertex.get(edgeOrder[i]), vertex
					.get(edgeOrder[i] + 1)));
		}
		return edges;
	}

	/**
	 * Devuelve los elementos que no aparecen en las dos listas (equivalentes)
	 * 
	 * @param h1
	 * @param h2
	 * @return
	 */
	private List<Edge> borderOperator(List<Edge> h1, List<Edge> h2) {
		List<Edge> edges = new ArrayList<Edge>();
		// System.err.println("Listas entrada");
		// System.err.println("\t "+h1);
		// System.err.println("\t "+h2);
		for (Edge e : h1) {
			if (!h2.contains(e)) {
				edges.add(e);
				h2.remove(e);
			}
		}
		edges.addAll(h2);
		// System.err.println("\t Salida "+edges);
		return edges;
	}

	/**
	 * Suma todas las aristas, el resultado es una lista de aristas que coincide
	 * con la covertura conexa de los hexagonos. Tambien detecta el borde
	 * exterior y los bordes interiores en caso de existir
	 * 
	 * @param rawPolygon
	 */
	private void separateLines(List<Edge> rawPolygon) {
		Collection<List<Edge>> borders = new TreeSet<List<Edge>>(
				new SizeComparator());
		LinkedList<Edge> border = new LinkedList<Edge>();
		border.add(rawPolygon.get(0));
		rawPolygon.remove(0);
		borders.add(border);
		for (Edge e : rawPolygon) {
			Edge a = border.getLast();
			if (e.isAdyacent(a)) {// Voy añadiendo mientras sean adyacentes
			// System.err.println(a+" son adyacentes "+e);
				border.add(e);
			} else {// ya no es adyacente, es otro borde
				System.err.println("\tDetectado Borde " + border);
				border = new LinkedList<Edge>();
				border.add(e);
				borders.add(border);
			}
		}
		boolean outer = true;
		for (List<Edge> b : borders) {
			if (outer) {
				outerLine = edgeToList(b);
				outer = false;
			} else {
				innerLines.add(edgeToList(b));
			}
		}
	}

	private List<Coordinate> edgeToList(List<Edge> line) {
		List<Coordinate> lc = new ArrayList<Coordinate>();
		for (Edge e : line) {
			lc.addAll(e.getEdge());
		}
		return lc;
	}
}
