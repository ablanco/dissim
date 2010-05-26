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

package kml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import util.Scenario;
import util.flood.Edge;
import util.flood.SizeComparator;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;

/**
 * This class solves plenty of problems for drawing polygons given by a list of
 * dots
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Kpolygon {

	/**
	 * Polygon has no type
	 */
	public static final int RawType = -1;
	/**
	 * Polygon is water
	 */
	public static final int WaterType = 1;
	/**
	 * Polygon is a Pedestrian
	 */
	public static final int Pedestrian = 2;

	private List<Coordinate> outerLine;
	private List<List<Coordinate>> innerLines;
	private int type;
	private short deep;

	/**
	 * Given a list of dots, a type and geographical increments for a concrete
	 * {@link Scenario}, it creates a polygon which we can easily manipulate and
	 * write into a kml
	 * 
	 * @param type
	 *            of the polygon, must be one of the statics value
	 * @param rawPolygon
	 *            list of {@link LatLng} that describes a water sector
	 * @param ilat
	 *            latitude increment of the scenario
	 * @param ilng
	 *            longitude increment of the scenario
	 */
	public Kpolygon(int type, List<LatLng> rawPolygon, double ilat, double ilng) {
		if (rawPolygon == null || rawPolygon.size() == 0) {
			throw new IllegalArgumentException(
					"The list of vertices can't be null or empty");
		}
		this.type = type;
		outerLine = new ArrayList<Coordinate>();
		innerLines = new ArrayList<List<Coordinate>>();
		List<LinkedList<Edge>> rawEdgeList = new ArrayList<LinkedList<Edge>>();
		for (LatLng l : rawPolygon) {
			rawEdgeList.add(getHexagonEdges(l, ilat, ilng));
		}
		// System.err.println("Creando polygono, obteniendas todas las aristas "
		// + rawEdgeList);
		// TODO mejorar eficiencia
		Collection<LinkedList<Edge>> edgeList = new TreeSet<LinkedList<Edge>>(
				new SizeComparator());
		Iterator<LinkedList<Edge>> it = rawEdgeList.iterator();
		// La razon de esto es, Al hacerlo de otra forma, podia coincidir la
		// suma de dos sectores que no fueran adyacentes, lo cual crea huecos
		// que no se pueden resolver de una forma eficiente, esto tampoco es muy
		// eficiente, pero es mucho menos complicao, creo, que lo otro.
		while (it.hasNext()) {
			LinkedList<Edge> curr = it.next();
			// System.err.println("\tBorde Actual " + curr);
			edgeList = borderOperator(edgeList, curr);
			// System.err.println("Bordes Separados "+edgeList.size()+" : "+edgeList);
			edgeList = joinBorders(edgeList);
			// System.err.println("\t\tBordes reordenados " + edgeList.size()
			// + " : " + edgeList);
		}

		Iterator<LinkedList<Edge>> e = edgeList.iterator();
		while (e.hasNext()) {
			// El primero es el Borde exterior
			outerLine = edgeToList(e.next());
			while (e.hasNext()) {
				// Los demas son bordes interiores
				innerLines.add(edgeToList(e.next()));
			}
		}
		// System.err.println("\t Borde exterior " + outerLine);
		// System.err.println("\t Bordes Interiores " + innerLines);
	}

	/**
	 * Gets the outer border of the polygon,
	 * 
	 * @return outer border
	 */
	public List<Coordinate> getOuterLine() {
		return outerLine;
	}

	/**
	 * Gets a list of inner borders of the polygon
	 * 
	 * @return a list of borders
	 */
	public List<List<Coordinate>> getInnerLines() {
		return innerLines;
	}

	/**
	 * Gets the type of the polygon
	 * 
	 * @return type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the elevation of the polygon, relative to sea level
	 * 
	 * @return elevation
	 */
	public short getDeep() {
		return deep;
	}

	/**
	 * Sets elevation for the polygon, relative to sea level
	 * 
	 * @param deep
	 *            elevation of the polygon
	 */
	public void setDeep(short deep) {
		this.deep = deep;
	}

	/*
	 * Posible orden? [b,c]-[a,f]+[a,b]-[f,e],+[c,d]-[e,d] public static final
	 * int[] edgeOrder = new int[]{ 1,1,2, -1,0,5, 1,0,1, -1,5,4, 1,2,3,
	 * -1,4,3};
	 */

	/**
	 * Gets a list of coordinates that describes the vertices of the hexagon
	 * 
	 * @param centre
	 *            of the hexagon
	 * @param ilat
	 *            height
	 * @param ilng
	 *            width
	 * @return hexagon vertices
	 */
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

	/**
	 * Gets edges from an hexagon whose center is at given position
	 * 
	 * @param centre
	 *            of the hexagon
	 * @param ilat
	 *            heith
	 * @param ilng
	 *            width
	 * @return a list of edges
	 */
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
	 * Given two lists, merges them and removes duplicates, mantaining the
	 * proper order. Two edges are equal if A(a1,a2), B(b1,b2) -> a1=b2, a2=b1.
	 * 
	 * @param h1
	 *            list of edges
	 * @param h2
	 *            list of edges
	 * @return merged and ordered list of edges, may contain more than one
	 *         border
	 */
	private Collection<LinkedList<Edge>> borderOperator(
			Collection<LinkedList<Edge>> polygonBorders,
			LinkedList<Edge> polygonEdges) {
		// System.err.println("Listas entrada");
		// System.err.println("\t Actual "+polygonBorders);
		// System.err.println("\t Por añadir" + polygonEdges);

		// Buscamos en la lista de bordes conexa, bordes que se repiten, si hay
		// alguno
		// opuesto lo marco como modificado y lo borro de la lista para volver a
		// poner la lista conexa
		Collection<LinkedList<Edge>> modificados = new ArrayList<LinkedList<Edge>>();
		Iterator<LinkedList<Edge>> it = polygonBorders.iterator();
		while (it.hasNext() && !polygonEdges.isEmpty()) {
			LinkedList<Edge> currList = it.next();
			boolean modified = false;
			Iterator<Edge> iEdge = polygonEdges.iterator();
			while (iEdge.hasNext()) {
				Edge e = iEdge.next();
				int index = currList.indexOf(e.opposite());
				if (index > 0) {
					// System.err.println("\t Se ha encontrado un opuesto "+e);
					currList.remove(index);
					iEdge.remove();
					modified = true;
				}
			}

			if (modified) {
				// Los que han sido modificados, ya no son conexos, asi que los
				// separamos en listas conexas
				// System.err.println("\t Bordes Modificados "+split(currList));
				modificados.addAll(split(currList));
				it.remove();
			}
		}
		// Esto quiere decir que quedan bordes interiores quizas que no son
		// conexos

		if (!polygonEdges.isEmpty()) {
			// System.err.println("\t\tAun quedan por añadir Aristas "
			// + polygonBorders);
			modificados.add(polygonEdges);
		}
		// System.err.println("\t Todos los Modificados "+modificados.size()+" : "+modificados);

		// Ya tenemos todas las listas separadas, ahora las juntamos en una sola
		// lista
		polygonBorders.addAll(modificados);
		// System.err.println("\t Bordes Modificados y No Modificados "
		// + polygonBorders.size() + " :" + polygonBorders);
		return polygonBorders;
	}

	/**
	 * Splits into conex list. A list is conex if A(a1,a2), B(b1,b2) -> a2==b1
	 * || b2==a1
	 * 
	 * @param currList
	 *            may contains unconex lists
	 * @return a list of conex lists
	 */
	private Collection<LinkedList<Edge>> split(LinkedList<Edge> currList) {
		// System.err.println("\t\t Antes de Partir "+currList.size()+" : "+currList);
		LinkedList<Edge> conexo = new LinkedList<Edge>();
		Collection<LinkedList<Edge>> borders = new ArrayList<LinkedList<Edge>>();
		while (!currList.isEmpty()) {
			Edge prev = currList.pop();
			conexo.add(prev);
			borders.add(conexo);
			while (!currList.isEmpty()) {
				Edge curr = currList.pop();
				if (prev.isNextOf(curr)) {
					// System.err.println("\t\t " + prev + " conexo con " +
					// curr);
					conexo.add(curr);
				} else {
					// System.err.println("\t\t " + prev + " NO conexo con "
					// + curr);
					conexo = new LinkedList<Edge>();
					conexo.add(curr);
					borders.add(conexo);
				}
				prev = curr;
			}
		}
		// System.err.println("\t\t Despues de Partir "+borders.size()+" : "+borders);
		return borders;
	}

	/**
	 * Looks for conex borders and joins them
	 * 
	 * @param borderList
	 *            may contain unconex borders
	 * @return a list of conex borders
	 */
	private Collection<LinkedList<Edge>> joinBorders(
			Collection<LinkedList<Edge>> borderList) {
		// Lista de bordes Final
		// System.err.println("\t\t Tamaño entrada " + borderList.size() + " :"
		// + borderList);
		Collection<LinkedList<Edge>> join = new ArrayList<LinkedList<Edge>>();
		// Iterador sobre los bordes que ya teniamos
		Iterator<LinkedList<Edge>> bIterator = borderList.iterator();
		while (bIterator.hasNext()) {
			// Mientras tengamos bordes que explorar
			LinkedList<Edge> curr = bIterator.next();
			bIterator.remove();
			// Marcamos la lista como modificada, para que entre en el bucle
			boolean modificado = true;
			// Reutilizamos el iterador, para iterar sobre todos los demas
			// elementos, menos el primero
			while (modificado && !borderList.isEmpty() && bIterator.hasNext()) {
				bIterator = borderList.iterator();
				while (bIterator.hasNext() && modificado
						&& !borderList.isEmpty()) {
					// Aqui nos aseguramos de haber recorrido Toda la lista
					// buscando posibles uniones repitiendo cada vez que haya
					// una modificacion
					modificado = false;
					LinkedList<Edge> conexo = bIterator.next();
					// System.err.println("\t\t Mirando si " + conexo
					// + " es conexo con " + curr);
					// Mientras tenga aristas por añadir
					if (curr.getLast().isNextOf(conexo.getFirst())) {
						// Es la continuacion, añado y borro
						curr.addAll(conexo);
						bIterator.remove();
						modificado = true;
						// System.err.println("\t\t Se ha añadido al Final "
						// + curr);
					} else if (curr.getFirst().isPreviousOf(conexo.getLast())) {
						// Esta al principio, añado y borro
						curr.addAll(0, conexo);
						bIterator.remove();
						modificado = true;
						// System.err.println("\t\t Se ha añadido al Principio "
						// + curr);
					}
				}
			}
			// Añado la nueva lista a la lista de resultados finales
			join.add(curr);
		}
		if (!borderList.isEmpty()) {
			join.addAll(borderList);
		}
		// System.err.println("\t\t Tamaño salida " + join.size() + " :" +
		// join);
		return join;
	}

	/**
	 * From {@link List}<{@link Edge}> to {@link List}<{@link Coordinate}>
	 * 
	 * @param line
	 *            list of {@link Edge}
	 * @return list of {@link Coordinate}
	 */
	private List<Coordinate> edgeToList(List<Edge> line) {
		List<Coordinate> lc = new ArrayList<Coordinate>();
		for (Edge e : line) {
			lc.addAll(e.getEdge());
		}
		return lc;
	}

}
