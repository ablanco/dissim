package util.jcoord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import util.flood.Edge;
import util.flood.SizeComparator;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;

public class Topologic {

	// Posible orden? [b,c]-[a,f]+[a,b]-[f,e],+[c,d]-[e,d]
	// public static final int[] edgeOrder = new int[]{ 1,1,2, -1,0,5, 1,0,1,
	// -1,5,4, 1,2,3, -1,4,3};

	public static List<Coordinate> getHexagonVertex(LatLng centre, double ilat,
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

	private static List<Edge> getHexagonEdges(LatLng centre, double ilat,
			double ilng) {
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

	private static List<Edge> borderOperator(List<Edge> h1, List<Edge> h2) {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge e : h1) {
			if (!h2.contains(e)) {
				edges.add(e);
				h2.remove(e);
			}
		}
		edges.addAll(h2);
		return edges;
	}

	public static List<List<Edge>> conexCoverture(List<LatLng> rawPolygon,
			double ilat, double ilng) {
		// Por cada coordenada del sector ahora tendremos todos los bordes
		List<List<Edge>> edges = new ArrayList<List<Edge>>();

		for (LatLng centre : rawPolygon) {
			edges.add(getHexagonEdges(centre, ilat, ilng));
		}

		List<Edge> outInnerBorder = new ArrayList<Edge>();
		//TODO se puede optimizar
		for (List<Edge> le : edges) {
			outInnerBorder = borderOperator(le, outInnerBorder);
		}

		System.err.print("Bordes Interior y Exterior tam "
				+ outInnerBorder.size() + " ");
		for (Edge e : outInnerBorder) {
			System.err.print(e);
		}
		System.err.println();

		// Ahora tenemos varias listas de edges pero en una sola
		List<List<Edge>> borders = new ArrayList<List<Edge>>();
		LinkedList<Edge> border = new LinkedList<Edge>();
		for (Edge e : outInnerBorder) {
			// Vamos mirando la lista y cortando donde empieze otra lista
			if (border.isEmpty()) {// Agrego el primero
				border.add(e);
			} else {// Miramos si es adyacente al ultimo
				Edge a = border.getLast();
				if (e.isAdyacent(a)) {// Voy añadiendo mientras sean adyacentes
					border.add(e);
				} else {// ya no es adyacente, es otro borde
					borders.add(border);
					border = new LinkedList<Edge>();
				}
			}
		}
		Collections.sort(borders, new SizeComparator());
		System.err.println("Tiene "+borders.size()+"formas");
		
		return borders;
	}
}
