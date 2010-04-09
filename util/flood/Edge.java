package util.flood;

import java.util.ArrayList;

import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;

public class Edge {
	private int sig;
	private Coordinate a;
	private Coordinate b;

	public Edge(int sig, Coordinate a, Coordinate b) {
		this.sig = sig;
		// Redondeamos las coordenadas
		this.a = a;
		a.setLatitude(LatLng.round(a.getLatitude()));
		a.setLongitude(LatLng.round(a.getLongitude()));
		this.b = b;
		b.setLatitude(LatLng.round(b.getLatitude()));
		b.setLongitude(LatLng.round(b.getLongitude()));
	}

	public Coordinate getA() {
		return a;
	}

	public Coordinate getB() {
		return b;
	}
	
	public ArrayList<Coordinate> getEdge(){
		ArrayList<Coordinate> edge = new ArrayList<Coordinate>();
		edge.add(a);
		edge.add(b);
		return edge;
	}

	/**
	 * Obtiene el signo de la Coordenada (si es al derecho o al reves)
	 * 
	 * @return
	 */
	public int getSig() {
		return sig;
	}

	@Override
	public boolean equals(Object obj) {
		Edge e = (Edge) obj;
		return (a == e.getA() && b == e.getB());
//		return isAdyacent(e);
	}

	/**
	 * Es adyacente si un vertice pertenece a las dos aristas
	 * 
	 * @param e
	 * @return
	 */
	public boolean isAdyacent(Edge e) {
		return a == e.getA() || b == e.getB() || b == e.getA() || a == e.getB();
	}
	
	/**
	 * Return true if e is the opposite (A,B) == (B,A)
	 * @param e
	 * @return
	 */
	public boolean isOposite(Edge e){
		Coordinate c = e.getB();
		Coordinate d = e.getA();
		return a.getLatitude() == c.getLatitude() && a.getLongitude() == c.getLongitude() && b.getLatitude() == d.getLatitude() && b.getLongitude() == d.getLongitude();
	}

	@Override
	public String toString() {
//		return sig + "[(" + a.getLatitude() + "," + a.getLongitude() + "),"
//				+ "(" + b.getLatitude() + "," + b.getLongitude() + ")], ";
		return "("+hash(a)+","+hash(b)+")";
	}
	
	/**
	 * Returns a friednly view of the edge
	 * @param c
	 * @return
	 */
	private String hash(Coordinate c){
		int llat = (int) (c.getLatitude() * Math.pow(10, 6));
		String slat = String.valueOf(llat).substring(5,8);
		
		int llng = (int) (c.getLongitude() * Math.pow(10, 6));
		String slng = String.valueOf(llng).substring(5,8);
		
		return String.valueOf(Integer.toHexString(Integer.valueOf(slat)+Integer.valueOf(slng))); 
//		return "["+slat+","+slng+"]";
//		return "["+Integer.toHexString(Integer.valueOf(slat))+","+Integer.toHexString(Integer.valueOf(slng))+"]";
	}
}
