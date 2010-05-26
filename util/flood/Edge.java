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

package util.flood;

import java.util.ArrayList;

import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;

/**
 * Sometimes we need to work with edges, lines and borders, this class is for
 * easily managing this situations
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Edge {
	private int sig;
	private Coordinate a;
	private Coordinate b;

	/**
	 * New edge, given two points and a direction
	 * 
	 * @param sig
	 * @param a
	 * @param b
	 */
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

	/**
	 * Gets a coordinate
	 * 
	 * @return a
	 */
	public Coordinate getA() {
		return a;
	}

	/**
	 * Gets b coordinate
	 * 
	 * @return
	 */
	public Coordinate getB() {
		return b;
	}

	/**
	 * Gets an array list of the edge, [a,b]
	 * 
	 * @return [a,b]
	 */
	public ArrayList<Coordinate> getEdge() {
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
		return (almostEqual(a, e.getA()) && almostEqual(b, e.getB()));
	}

	/**
	 * Es adyacente si un vertice pertenece a las dos aristas
	 * 
	 * @param e
	 * @return
	 */
	public boolean isAdyacent(Edge e) {
		return almostEqual(b, e.getA()) || almostEqual(a, e.getB());
	}

	/**
	 * Returns true if the edge is the next adyacent edge this[a,b], e[a,b] si
	 * this.b==e.a
	 * 
	 * @param e
	 * @return
	 */
	public boolean isNextOf(Edge e) {
		return almostEqual(b, e.getA());
	}

	/**
	 * Returns true if the edge is the previous adyacent edge this[a,b], e[a,b]
	 * si this.a==e.b
	 * 
	 * @param e
	 * @return
	 */
	public boolean isPreviousOf(Edge e) {
		return almostEqual(a, e.getB());
	}

	/**
	 * Return true if e is the opposite (A,B) == (B,A)
	 * 
	 * @param e
	 * @return
	 */
	public boolean isOposite(Edge e) {
		return almostEqual(a, e.getB()) && almostEqual(b, e.getA());
	}

	/**
	 * Reuturns the opposite edge from this
	 * 
	 * @param e
	 * @return
	 */
	public Edge opposite() {
		return new Edge(sig, b, a);
	}

	@Override
	public String toString() {
		// return sig + "[(" + a.getLatitude() + "," + a.getLongitude() + "),"
		// + "(" + b.getLatitude() + "," + b.getLongitude() + ")], ";
		return "(" + hash(a) + "," + hash(b) + ")";
	}

	/**
	 * Returns a friednly view of the edge
	 * 
	 * @param c
	 * @return
	 */
	private String hash(Coordinate c) {
		int llat = (int) (c.getLatitude() * Math.pow(10, 6));
		String slat = String.valueOf(llat).substring(5, 8);

		int llng = (int) (c.getLongitude() * Math.pow(10, 6));
		String slng = String.valueOf(llng).substring(5, 8);

		return String.valueOf(Integer.toHexString(Integer.valueOf(slat)
				+ Integer.valueOf(slng)));
		// return "["+slat+","+slng+"]";
		// return
		// "["+Integer.toHexString(Integer.valueOf(slat))+","+Integer.toHexString(Integer.valueOf(slng))+"]";
	}

	/**
	 * Epic Method, returns true if two coordinates looks like nearly the same,
	 * due to java double shitty implemenation
	 * 
	 * @param a
	 * @param b
	 * @return true if differs in +-0.0000003
	 */
	private boolean almostEqual(Coordinate a, Coordinate b) {
		long alat = (long) Math.abs((a.getLatitude() * Math.pow(10, 6)));
		long alng = (long) Math.abs((a.getLongitude() * Math.pow(10, 6)));
		long blat = (long) Math.abs((b.getLatitude() * Math.pow(10, 6)));
		long blng = (long) Math.abs((b.getLongitude() * Math.pow(10, 6)));
		// TODO mejorar
		// System.err.println("A: "+a+", B:"+b+" resultado "+alat+"-"+blat+" vs "+alng+"-"+blng);
		return Math.abs(alat - blat) < 3 && Math.abs(alng - blng) < 3;

	}
}
