package osm;

import java.util.ArrayList;
import java.util.List;

import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

public class OsmEdge {

	/**
	 * Dentro de un Box
	 */
	public static final int In = 1;
	/**
	 * Fuera de un Box
	 */
	public static final int Out = 0;
	/**
	 * Corta al Box estan el borde A dentro
	 */
	public static final int Cuts_A = 2;
	/**
	 * Corta al Box estan el borde B dentro
	 */
	public static final int Cuts_B = 3;

	/**
	 * Coordenada inicial [y,x]
	 */
	private OsmNode nodeA;
	private OsmNode nodeB;
	/**
	 * Coordenada final [y,x]
	 */
	private double alpha;
	private double beta;
	private double[] v;

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

	// gets y sets
	public OsmNode getNodeA(){
		return nodeA;
	}
	
	public OsmNode getNodeB() {
		return nodeB;
	}
	
	/**
	 * Dado una latitud, resuelve la ecuacion y=alpha * x + beta
	 * 
	 * @param lat
	 * @return
	 */
	public LatLng getY(double lng) {
		return new LatLng(((alpha * lng) + beta), lng);
	}

	public LatLng getY(LatLng c) {
		return getY(c.getLng());
	}

	/**
	 * Dada una longitud, resuelte la ecuacion x = ( y - beta ) / alpha;
	 * 
	 * @param lng
	 * @return
	 */
	private LatLng getX(double lat) {
		return new LatLng(lat, ((lat - beta) / alpha));
	}

	private LatLng getX(LatLng c) {
		return getX(c.getLat());
	}

	// propiedades
	/**
	 * Nos dice por medio de variables estaticas cual es su relacion con un box,
	 * si lo contiene, esta totalmente fuera, o lo corta
	 * 
	 * @param box
	 * @return
	 */
	public int cutType(LatLngBox box) {
		if (box.contains(nodeA) && box.contains(nodeB))
			return In;
		if (box.contains(nodeA))
			return Cuts_A;
		if (box.contains(nodeB))
			return Cuts_B;
		return Out;
	}
	//Metodos
	
	/**
	 * Devuelve El nodo que corta al box, el Nodo A o null si los dos estan fuera
	 */
	public OsmNode getCutNode(LatLngBox box){
		switch (cutType(box)) {
		case Cuts_A:
			//nodeA esta dentro, devuelvo B
			return cutOff(box).getNodeB();
		case Cuts_B:
			//nodeB esta dentro, devuelvo A
			return cutOff(box).getNodeA();
		case In:
			return nodeA;
		case Out:
		default:
			return null;
		}
			
		
	}

	/**
	 * Este metodo nos da el punto de corte de esta arista con la caja, se
	 * deberia de llamar solo si cutType(box) == Cuts
	 * 
	 * @param box
	 * @return
	 */
	public OsmEdge cutOff(LatLngBox box) {
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
			return new OsmEdge(nodeA,nodeB);
		default:
			//No deberia llegar aqui
			return null;
		}

		// Mantenemos la direccion A -> B de la arista
		if (box.contains(nodeA)) {
			return new OsmEdge(nodeA, cut);
		} else {
			return new OsmEdge(cut, nodeB);
		}
	}

	// metodos
	public LatLng next(OsmNode curr, LatLngBox box) {
		return next(curr.getCoord(), box);
	}
	/**
	 * Nos da el siguiente punto correspondiente a la recta, segun los
	 * incrementos marcados por el box
	 */
	public LatLng next(LatLng curr, LatLngBox box) {
		// Al no ser adyacencia octogonal, algunos movimientos +1+1, -1-1, no
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
	public String toString() {
		return "A: " + nodeA + ", B: " + nodeB + ", (" + alpha + "," + beta + "), ("
				+ v[0] + "," + v[1] + ")";
		// return "A: " + a + ", B: " + b;
		// return "y=x"+alpha+"+"+beta;
	}

	public class Eqn {
		private double alpha;
		private double beta;
		private double x0;
		private double x1;
		private double y0;
		private double y1;
		private double[][] coef;
		private double[] ind;

		public Eqn(LatLng a, LatLng b) {
			x0 = a.getLng();
			y0 = a.getLat();
			x1 = b.getLng();
			y1 = b.getLat();
			this.alpha = 1;
			this.beta = 1;
			coef = new double[][] { { x0 * 1, 1 }, { x1 * 1, 1 } };
			ind = new double[] { y0, y1 };
		}

		public double[] solve() {
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

		private void multiplica(int fil, double val) {
			coef[fil][0] *= val;
			coef[fil][1] *= val;
			ind[fil] *= val;
		}

		private void resta(int c, int d) {
			coef[c][0] -= coef[d][0];
			coef[c][1] -= coef[d][1];
			ind[c] -= ind[d];
		}

//		private String print() {
//			return "coef: ((" + coef[0][0] + "," + coef[0][1] + "),("
//					+ coef[1][0] + "," + coef[1][1] + ")), ind: (" + ind[0]
//					+ "," + ind[1] + ").";
//		}
	}

}
