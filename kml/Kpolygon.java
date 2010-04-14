package kml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
		List<LinkedList<Edge>> rawEdgeList = new ArrayList<LinkedList<Edge>>();
		for (LatLng l : rawPolygon) {
			rawEdgeList.add(getHexagonEdges(l, ilat, ilng));
		}
		System.err.println("Creando polygono, obteniendas todas las aristas "
				+ rawEdgeList);
		// TODO mejorar eficiencia
		LinkedList<Edge> edgeList = new LinkedList<Edge>();
		Iterator<LinkedList<Edge>> it = rawEdgeList.iterator();
		// La razon de esto es, Al hacerlo de otra forma, podia coincidir la
		// suma de dos sectores que no fueran adyacentes, lo cual crea huecos
		// que no se pueden resolver de una forma eficiente, esto tampoco es muy
		// eficiente, pero es mucho menos complicao, creo, que lo otro.
		while (it.hasNext()) {
			edgeList = borderOperator(it.next(), edgeList);
		}
		System.err.println("Lista en bruto de las aristas " + edgeList);
		Collection<List<Edge>> edges = separateLines(edgeList);
		System.err.println("\t Bordes separados " + edges);
		edges = joinLines(edges);

		Iterator<List<Edge>> e = edges.iterator();
		while (e.hasNext()) {
			// El primero es el Borde exterior
			outerLine = edgeToList(e.next());
			while (e.hasNext()) {
				// Los demas son bordes interiores
				innerLines.add(edgeToList(e.next()));
			}
		}
		System.err.println("\t Borde exterior " + outerLine);
		System.err.println("\t Bordes Interiores " + innerLines);
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

	private LinkedList<Edge> getHexagonEdges(LatLng centre, double ilat,
			double ilng) {
		if (centre == null) {
			throw new IllegalArgumentException();
		}
		LinkedList<Edge> edges = new LinkedList<Edge>();
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
	private LinkedList<Edge> borderOperator(LinkedList<Edge> h1,
			LinkedList<Edge> h2) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		System.err.println("Listas entrada");
		System.err.println("\t " + h1);
		System.err.println("\t " + h2);

		// Here goes
		while (!h1.isEmpty() && !h2.isEmpty()) {
			Edge a = h1.pop();
			int posx = h2.indexOf(a.opposite());
			if (posx > -1) {
				Collection<Edge> subl = nextAdyacentIndexOf(a, posx, h2);
				edges.addAll(subl);
				System.err.println("\t\t\t Lista hasta el momento " + edges);
				h2.removeAll(subl);
				h2.remove(a.opposite());
			} else {
				edges.add(a);
				// System.err.println("\t\t añadido "+a+" a "+edges);
			}
		}
		// En caso de que h2 sea vacio tenemos que añadir h1, en todos los demas
		// casos añadira una lista vacia
		if (!h1.isEmpty()) {
			edges.addAll(h1);
		}
		if (!h2.isEmpty()) {
			edges.addAll(h2);
		}
		System.err.println("\t\t  Salida" + edges);

		return edges;
	}

	private LinkedList<Edge> nextAdyacentIndexOf(Edge a, int pos,
			LinkedList<Edge> edges) {
		System.err.println("\t\t Partiendo " + a + " desde pos " + pos);
		// Iterador ordenado para la lista

		Edge e = edges.get(pos);
		int key = -1;
		if (a.isNext(e)) {
			key = 0; // Adyacente a la derecha
		} else if (a.isPrevious(e)) {
			key = 1; // Adyacente a la izquierda
		}
		ListIterator<Edge> li = edges.listIterator(pos);
		LinkedList<Edge> aux = new LinkedList<Edge>();
		switch (key) {
		case 0:
			Edge der = li.next();
			System.err.println("\t\t\t Sublista a la Der " + der
					+ " adyacente a " + a);
			boolean adyacent = true;
			// aux.add(der);
			while (adyacent && li.hasNext()) {
				Edge curr = li.next();
				adyacent = der.isNext(curr);
				if (adyacent) {
					aux.add(curr);
					der = curr;
				}
			}
			System.err.println("\t\tSublista Der Detectada " + aux);
			return aux;
		case 1:
			System.err.println("\t\t\t Buscando por la izq previous ady de "
					+ a);
			boolean previous = false;
			while (li.hasPrevious() && !previous) {
				Edge izq = li.previous();
				aux.push(izq);
				if (a.isPrevious(izq)) {
					previous = true;
				}
			}
			if (previous) {
				System.err.println("\t\tSublista Izq Detectada " + aux);
				return aux;
			}
		default:
			System.err.println("\t\t\t Sublista ni a la Izq ni Der");
			return new LinkedList<Edge>();
		}
	}

	/**
	 * Suma todas las aristas, el problema es que es un caos, asi que da muchas
	 * listas que hay que juntar luego, otra chapuza mas ...
	 * 
	 * @param rawPolygon
	 */
	private Collection<List<Edge>> separateLines(List<Edge> rawPolygon) {
		Collection<List<Edge>> borders = new TreeSet<List<Edge>>(
				new SizeComparator());
		List<Edge> border = new ArrayList<Edge>();
		Iterator<Edge> it = rawPolygon.iterator();
		Edge prev = it.next();
		border.add(prev);
		it.remove();
		while (it.hasNext()) {
			Edge curr = it.next();
			if (prev.isAdyacent(curr)) {
				border.add(curr);
				prev = curr;
			} else {
				borders.add(border);
				border = new ArrayList<Edge>();
				border.add(curr);
			}
			prev = curr;
		}
		return borders;
	}

	/**
	 * El problema es que al final no hace bien las mezclas de listas, y hay que
	 * volver a rejuntarlos
	 * 
	 * @param borders
	 * @return
	 */
	private Collection<List<Edge>> joinLines(Collection<List<Edge>> borders) {
		// No sabemos en el orden que van a estar, asi que cada vez que haya
		// una modificacion tenemos que repetir todo el proceso
		boolean unchanged = false;
		while (!unchanged && borders.size() > 1) {
			// Suponemos que no ha cambiado
			unchanged = true;
			// Primer iterador
			Iterator<List<Edge>> it = borders.iterator();
			// Nos quedamos con el primer elemento y borramos
			List<Edge> curr = it.next();
			it.remove();
			while (it.hasNext()) {
				List<Edge> next = it.next();
				int j = isAdyacentList(curr, next);
				if (j != 0) {
					System.err.println("Uniendo Bordes " + curr + " con "
							+ next);
					curr = union(j, curr, next);
					System.err.println("\t y este es el resultado " + curr);
					it.remove();
					unchanged = false;
				}
			}
			borders.add(curr);
		}
		System.err.println("\t Bordes unidos " + borders);
		return borders;
	}

	/**
	 * Pasa una lista de Aristas a una lista de Coordinate
	 * 
	 * @param line
	 * @return
	 */
	private List<Coordinate> edgeToList(List<Edge> line) {
		List<Coordinate> lc = new ArrayList<Coordinate>();
		for (Edge e : line) {
			lc.addAll(e.getEdge());
		}
		return lc;
	}

	/**
	 * Returns 0 if list are not adyacent, 1 if l1+l2 -1 if l2+l1
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	private int isAdyacentList(List<Edge> l1, List<Edge> l2) {
		if (l1 == null || l2 == null || l1.size() == 0 || l2.size() == 0) {
			// Logicamento no son iguales
			return 0;
		}
		Edge p1 = l1.get(0);
		Edge p2 = l2.get(0);
		Edge u1 = l1.get(l1.size() - 1);
		Edge u2 = l2.get(l2.size() - 1);

		// l1 -> l2
		if (u1.isAdyacent(p2)) {
			return 1;
		}

		// l2 -> l1
		if (u2.isAdyacent(p1)) {
			return -1;
		}

		// l1 y l2 no son adyacentes
		return 0;
	}

	private List<Edge> union(int key, List<Edge> a, List<Edge> b) {
		List<Edge> union = new ArrayList<Edge>();
		if (key > 0) {
			union.addAll(a);
			union.addAll(b);
		} else if (key < 0) {
			union.addAll(b);
			union.addAll(a);
		} else {
			throw new IllegalArgumentException(
					"Estas listas no son adyacentes!!");
		}
		return union;
	}
}
