package kml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
		List<LinkedList<Edge>> rawEdgeList = new ArrayList<LinkedList<Edge>>();
		for (LatLng l : rawPolygon) {
			rawEdgeList.add(getHexagonEdges(l, ilat, ilng));
		}
		System.err.println("Creando polygono, obteniendas todas las aristas "
				+ rawEdgeList);
		// TODO mejorar eficiencia
		Collection<LinkedList<Edge>> edgeList = new TreeSet<LinkedList<Edge>>(new SizeComparator());
		Iterator<LinkedList<Edge>> it = rawEdgeList.iterator();
		// La razon de esto es, Al hacerlo de otra forma, podia coincidir la
		// suma de dos sectores que no fueran adyacentes, lo cual crea huecos
		// que no se pueden resolver de una forma eficiente, esto tampoco es muy
		// eficiente, pero es mucho menos complicao, creo, que lo otro.
		while (it.hasNext()) {
			LinkedList<Edge> curr = it.next();
//			System.err.println("Borde Actual "+curr);
			edgeList = borderOperator(edgeList, curr);
//			System.err.println("Bordes Separados "+edgeList.size()+" : "+edgeList);
			edgeList = joinBorders(edgeList);
//			System.err.println("Bordes reordenados "+edgeList.size()+" : "+edgeList);
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
	private Collection<LinkedList<Edge>> borderOperator(
			Collection<LinkedList<Edge>> polygonBorders,
			LinkedList<Edge> polygonEdges) {
//		System.err.println("Listas entrada");
//		System.err.println("\t Actual "+polygonBorders);
//		System.err.println("\t Por añadir" + polygonEdges);

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
			while(iEdge.hasNext()){
				Edge e = iEdge.next();
				int index = currList.indexOf(e.opposite());
				if (index>0){
//					System.err.println("\t Se ha encontrado un opuesto "+e);
					currList.remove(index);
					iEdge.remove();
					modified = true;
				}
			}
			
			if (modified) {
				//Los que han sido modificados, ya no son conexos, asi que los separamos en listas conexas
//				System.err.println("\t Bordes Modificados "+split(currList));
				modificados.addAll(split(currList));
				it.remove();
			}
		}
		//Esto quiere decir que quedan bordes interiores quizas que no son conexos
		
		if (!polygonEdges.isEmpty()) {
			modificados.add(polygonEdges);
		}
//		System.err.println("\t Todos los Modificados "+modificados.size()+" : "+modificados);

		//Ya tenemos todas las listas separadas, ahora las juntamos en una sola lista
		polygonBorders.addAll(modificados);
//		System.err.println("\t Bordes Modificados y No Modificados "+polygonBorders.size()+" :"+polygonBorders);
		return polygonBorders;
	}

	/**
	 * Separa una lista de aristas en una collecion de aristas conexas entre si
	 * @param currList
	 * @return
	 */
	private Collection<LinkedList<Edge>> split(
			LinkedList<Edge> currList) {
		LinkedList<Edge> conexo = new LinkedList<Edge>();
		Collection<LinkedList<Edge>> borders = new ArrayList<LinkedList<Edge>>();
		while (!currList.isEmpty()){
			Edge prev = currList.pop();
			conexo.add(prev);
			borders.add(conexo);
			while(!currList.isEmpty()){
				Edge curr = currList.pop();
				if (prev.isNextOf(curr)){
//					System.err.println("\t\t "+prev+ " conexo con "+ curr);
					conexo.add(curr);
				}else{
//					System.err.println("\t\t "+prev+ " NO conexo con "+ curr);
					conexo = new LinkedList<Edge>();
					conexo.add(curr);
					borders.add(conexo);
				}
				prev = curr;
			}
		}
		return borders;
	}

	private Collection<LinkedList<Edge>> joinBorders (Collection<LinkedList<Edge>> borderList){
		Collection<LinkedList<Edge>> join = new ArrayList<LinkedList<Edge>>();
		Iterator<LinkedList<Edge>> bIterator = borderList.iterator();
		while (bIterator.hasNext()){
			LinkedList<Edge> curr = bIterator.next();
			bIterator.remove();
			boolean modificado = true;
			bIterator = borderList.iterator();
			while (bIterator.hasNext() && modificado && !borderList.isEmpty()){
				modificado = false;
				LinkedList<Edge> conexo = bIterator.next();
				if (curr.getLast().isNextOf(conexo.getFirst())){
					curr.addAll(conexo);
					bIterator.remove();
					modificado = true;
				}else if (curr.getFirst().isPreviousOf(conexo.getLast())){
					curr.addAll(0, conexo);
					bIterator.remove();
					modificado = true;
				}
			}
			join.add(curr);
		}
		return join;
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
	
}
