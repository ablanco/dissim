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

package osm;

import java.util.ArrayList;
import java.util.List;

import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

/**
 * 
 * @author Manuel Gomar, Alejando Blanco
 * 
 *         This class helps to manage the points in the osm, given to points of
 *         a way make's an edge that is much more easier to manage discretizing
 *         into a grid
 * 
 */
public class OsmEdge {

	/**
	 * The edge is inside a given Box
	 */
	public static final int In = 1;
	/**
	 * The edge is outside a given Box
	 */
	public static final int Out = 0;
	/**
	 * The edge is cuts a given Box, nodeA is inside the box
	 */
	public static final int Cuts_A = 2;
	/**
	 * The edge is cuts a given Box, nodeB is inside the box
	 */
	public static final int Cuts_B = 3;

	/**
	 * Node A
	 */
	private OsmNode nodeA;
	/**
	 * Node B
	 */
	private OsmNode nodeB;
	/**
	 * alpha value y = x * alpha + beta
	 */
	private double alpha;
	/**
	 * beta value y = x * alpha + beta
	 */
	private double beta;
	/**
	 * vector directions
	 */
	private double[] v;

	/**
	 * Given two nodes, makes and edge and determinate the line equation and
	 * parameters
	 * 
	 * @param nA
	 * @param nB
	 */
	public OsmEdge(OsmNode nA, OsmNode nB) {
		this.nodeA = nA;
		this.nodeB = nB;

		LatLng a = nA.getCoord();
		LatLng b = nB.getCoord();

		Eqn eqn = new Eqn(a, b);
		double sol[] = eqn.solve();
		alpha = sol[0];
		beta = sol[1];

		switch (a.absolutePosition(b)) {
		case LatLng.SAME:
			v = new double[] { 0, 0 };
			break;
		case LatLng.ABOVE:
			v = new double[] { 0.75, 0 };
			break;
		case LatLng.ABOVE_RIGHT:
			v = new double[] { 0.5, 0.5 };
			break;
		case LatLng.ABOVE_LEFT:
			v = new double[] { 0.5, -0.5 };
			break;
		case LatLng.BELOW:
			v = new double[] { -0.75, 0 };
			break;
		case LatLng.BELOW_RIGHT:
			v = new double[] { -0.5, 0.5 };
			break;
		case LatLng.BELOW_LEFT:
			v = new double[] { -0.5, -0.5 };
			break;
		case LatLng.LEFT:
			v = new double[] { 0, -0.75 };
			break;
		case LatLng.RIGHT:
			v = new double[] { 0, 0.75 };
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @return get's nodeA
	 */
	public OsmNode getNodeA() {
		return nodeA;
	}

	/**
	 * 
	 * @return get's nodeB
	 */
	public OsmNode getNodeB() {
		return nodeB;
	}

	/**
	 * Given a longitude, solves y = (alpha * x)+ beta
	 * 
	 * @param lng
	 *            Point we want to determinate latitude
	 * @return Point which longitude is c, and latidude is y = (alpha * x)+ beta
	 */
	public LatLng getY(double lng) {
		return new LatLng(((alpha * lng) + beta), lng);
	}

	/**
	 * Given a coodinate c, gets its longitude, solves y = (alpha * x)+ beta
	 * 
	 * @param c
	 *            Longitude we want to determinate latitude
	 * @return Point which longitude is c, and latidude is y = (alpha * x)+ beta
	 */
	public LatLng getY(LatLng c) {
		return getY(c.getLng());
	}

	/**
	 * Given a latitude, solves x = ( y - beta ) / alpha
	 * 
	 * @param lat
	 *            Lat we want to determinate longitude
	 * @return Point which longitude is c, and longitude is x = ( y - beta ) /
	 *         alpha
	 */
	private LatLng getX(double lat) {
		return new LatLng(lat, ((lat - beta) / alpha));
	}

	/**
	 * Given a coodinate c, gets its latitude, solves x = ( y - beta ) / alpha
	 * 
	 * @param c
	 *            Point we want to determinate longitude
	 * @return Point which longitude is c, and longitude is x = ( y - beta ) /
	 *         alpha
	 */
	private LatLng getX(LatLng c) {
		return getX(c.getLat());
	}

	/**
	 * Nos dice por medio de variables estaticas cual es su relacion con un box,
	 * si lo contiene, esta totalmente fuera, o lo corta
	 * 
	 * @param box
	 * @return
	 */
	private int cutType(LatLngBox box) {
		if (box.contains(nodeA) && box.contains(nodeB))
			return In;
		if (box.contains(nodeA))
			return Cuts_A;
		if (box.contains(nodeB))
			return Cuts_B;
		return Out;
	}

	/**
	 * Given a box, if it cuts the edge, return the cut point
	 * 
	 * @param box
	 * @return if exist, the point where the edge cuts the box, if not, null
	 */
	public OsmNode getCutNode(LatLngBox box) {
		switch (cutType(box)) {
		case Cuts_A:
			// nodeA esta dentro, devuelvo B
			return cutOff(box).getNodeB();
		case Cuts_B:
			// nodeB esta dentro, devuelvo A
			return cutOff(box).getNodeA();
		case In:
			return nodeA;
		case Out:
		default:
			return null;
		}

	}

	/**
	 * If the edge cuts the box, gives the point where it's been cut
	 * 
	 * @see getCutNode(LatLngBox box)
	 * @param box
	 *            that cuts the edge
	 * @return point where the edge is cut
	 */
	private OsmEdge cutOff(LatLngBox box) {
		OsmNode cut = nodeB.clone();
		// Averiguamos cual de las dos esta fuera
		if (box.contains(nodeB)) {
			cut = nodeA.clone();
		}
		switch (box.absoluteBoxPosition(cut)) {
		case LatLngBox.ABOVE:
			cut.setCoord(getX(box.getNw()));
			break;
		case LatLngBox.ABOVE_RIGHT:
			cut.setCoord(getX(box.getNw()));
			if (cut.getCoord().isRigthOf(box.getSe())) {
				// Corta por encima de la lat maxima
				cut.setCoord(getY(box.getSe()));
			}
			break;
		case LatLngBox.RIGHT:
			cut.setCoord(getY(box.getSe()));
			break;
		case LatLngBox.BELOW_RIGHT:
			cut.setCoord(getY(box.getSe()));
			if (cut.getCoord().isBelowOf(box.getSe())) {
				// Corta por debajo de la lng minims
				cut.setCoord(getX(box.getSe()));
			}
			break;
		case LatLngBox.BELOW:
			cut.setCoord(getX(box.getSe()));
			break;
		case LatLngBox.BELOW_LEFT:
			cut.setCoord(getY(box.getNw()));
			if (cut.getCoord().isBelowOf(box.getSe())) {
				// Corta por debajo de la lng minima
				cut.setCoord(getX(box.getSe()));
			}
			break;
		case LatLngBox.LEFT:
			cut.setCoord(getY(box.getNw()));
			break;
		case LatLngBox.ABOVE_LEFT:
			cut.setCoord(getY(box.getNw()));
			if (cut.getCoord().isAboveOf(box.getNw())) {
				// Corta por encima de la lat maxima
				cut.setCoord(getX(box.getNw()));
			}
			break;
		case LatLngBox.IN:
			return new OsmEdge(nodeA, nodeB);
		default:
			// No deberia llegar aqui
			return null;
		}

		// Mantenemos la direccion A -> B de la arista
		if (box.contains(nodeA)) {
			return new OsmEdge(nodeA, cut);
		} else {
			return new OsmEdge(cut, nodeB);
		}
	}

	/**
	 * Given a point int the line (curr) returns the next point according to the
	 * box
	 * 
	 * @param curr
	 *            OsmNode point of the line
	 * @param box
	 *            box specific values for a line
	 * @return the next point of the line
	 */
	public LatLng next(OsmNode curr, LatLngBox box) {
		return next(curr.getCoord(), box);
	}

	/**
	 * Given a point int the line (curr) returns the next point according to the
	 * box
	 * 
	 * @param curr
	 *            point of the line
	 * @param box
	 *            specific values for a line
	 * @return the next point of the line
	 */
	public LatLng next(LatLng curr, LatLngBox box) {
		// Al no ser adyacencia octogonal, algunos movimientos 0,1, 0,-1, no
		// estan permitidos, por lo que hacemos los incrementos mas pequeños el
		// problema es que tendremos que al discretizar nos dara algunos puntos
		// repetidos
		double ilat = box.getIlat() * v[0];
		double ilng = box.getIlng() * v[1];

		if (Double.isInfinite(alpha)) {
			// Hay un problema con las funciones que para un x devuelve mas de
			// un y, para salvar este pequeño defecto asumo que cuando eso pasa
			// alpha = infinity, asi que simplemente sumo los incrementos, como
			// solo uno va a ser != 0 pues nada, a vivir la vida
			return new LatLng(curr.getLat() + ilat, curr.getLng() + ilng);
		} else if (Math.abs(alpha) > 1) {
			// La pendiente es demasiado poco pronunciada para usar lng
			return getX(curr.getLat() + ilat);
		}
		return getY(curr.getLng() + ilng);
	}

	/**
	 * Get a line between nodeA and nodeB using line equations
	 * 
	 * @param box
	 *            Contains parameters for building a speceific line.
	 * @return a list of LatLng representing the line in the given box
	 */
	public List<LatLng> getLine(LatLngBox box) {
		List<LatLng> list = new ArrayList<LatLng>();
		LatLng curr = nodeA.getCoord();
		LatLng b = nodeB.getCoord();
		// System.err.println("Linea de " + this);
		while (!box.closeTo(curr, b)) {
			// System.err.println("osmEdge "+curr+"->"+b);
			if (box.contains(curr)) {
				list.add(curr);
			}
			curr = next(curr, box);
		}
		// El ultimo puede escaparseme
		if (box.contains(curr)) {
			list.add(curr);
		}
		// Pueden no ser el mismo
		if (box.contains(b)) {
			list.add(b);
		}
		return list;
	}

	@Override
	/**
	 * Redefinition of to string for a friendly view
	 */
	public String toString() {
		return "A: " + nodeA + ", B: " + nodeB + ", (" + alpha + "," + beta
				+ "), (" + v[0] + "," + v[1] + ")";
		// return "A: " + a + ", B: " + b;
		// return "y=x"+alpha+"+"+beta;
	}

	/**
	 * 
	 * @author Manuel Gomar
	 * 
	 *         Class for calculatin line equations
	 * 
	 */
	protected class Eqn {
		private double alpha;
		private double beta;
		private double x0;
		private double x1;
		private double y0;
		private double y1;
		private double[][] coef;
		private double[] ind;

		protected Eqn(LatLng a, LatLng b) {
			x0 = a.getLng();
			y0 = a.getLat();
			x1 = b.getLng();
			y1 = b.getLat();
			this.alpha = 1;
			this.beta = 1;
			coef = new double[][] { { x0 * 1, 1 }, { x1 * 1, 1 } };
			ind = new double[] { y0, y1 };
		}

		protected double[] solve() {
			// System.err.println("Antes "+print());
			multiplica(0, x1);
			multiplica(1, x0);
			// System.err.println("despues de las multiplicaciones "+print());
			resta(1, 0);
			// System.err.println("despues de la resta: "+print());
			beta = ind[1] / coef[1][1];
			alpha = (ind[0] - beta * coef[0][1]) / coef[0][0];
			// System.err.println("sols "+alpha+", "+beta);
			return new double[] { alpha, beta };
		}

		protected void multiplica(int fil, double val) {
			coef[fil][0] *= val;
			coef[fil][1] *= val;
			ind[fil] *= val;
		}

		protected void resta(int c, int d) {
			coef[c][0] -= coef[d][0];
			coef[c][1] -= coef[d][1];
			ind[c] -= ind[d];
		}
	}

}
