package util.jcoord;

import java.io.Serializable;

import osm.OsmWay;

public class LatLngBox implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int IN = 0;
	public static final int ABOVE = 1; // TODO estan repetidos?? en latlng
	public static final int ABOVE_RIGHT = 2;
	public static final int RIGHT = 3;
	public static final int BELOW_RIGHT = 4;
	public static final int BELOW = 5;
	public static final int BELOW_LEFT = 6;
	public static final int LEFT = 7;
	public static final int ABOVE_LEFT = 8;

	/**
	 * Northern Wester point
	 */
	private LatLng nW;
	/**
	 * Southern Eastern point
	 */
	private LatLng sE;

	private double ilat;
	private double ilng;

	private int tileSize;

	public LatLngBox() {
	}

	public LatLngBox(LatLng NW, LatLng SE, int tileSize) {
		nW = new LatLng(NW.getLat(), NW.getLng());
		sE = new LatLng(SE.getLat(), SE.getLng());

		if (nW.isBelowOf(sE) || nW.isRigthOf(sE))
			throw new IllegalStateException("La caja no esta bien definida: "
					+ nW + ", " + sE);

		this.tileSize = tileSize;

		double ts = (double) tileSize;
		double hexWidth = ((ts / 2.0) * Math.cos(Math.PI / 6.0)) * 2.0;
		int cols = Math.max(1, (int) (nW.distance(new LatLng(nW.getLat(), sE
				.getLng())) / hexWidth));
		int rows = Math.max(1, (int) (nW.distance(new LatLng(sE.getLat(), nW
				.getLng())) / ((ts * 3.0) / 4.0)));

		ilat = LatLng.round(Math.abs(nW.getLat() - sE.getLat()) / rows);
		ilng = LatLng.round(Math.abs(nW.getLng() - sE.getLng()) / cols);

	}

	public int getTileSize() {
		return tileSize;
	}

	public int absoluteBoxPosition(LatLng c) {
		if (c.getLat() > nW.getLat()) {
			if (c.getLng() < nW.getLng()) {
				return ABOVE_LEFT;
			} else if (c.getLng() > sE.getLng()) {
				return ABOVE_RIGHT;
			} else {
				return ABOVE;
			}
		}

		if (c.getLat() < sE.getLat()) {
			if (c.getLng() < nW.getLng()) {
				return BELOW_LEFT;
			} else if (c.getLng() > sE.getLng()) {
				return BELOW_RIGHT;
			} else {
				return BELOW;
			}
		}

		if (c.getLng() < nW.getLng()) {
			return LEFT;
		} else if (c.getLng() > sE.getLng()) {
			return RIGHT;
		} else {
			return IN;
		}
	}

	public boolean closeTo(LatLng a, LatLng b) {
		return (Math.abs(a.getLat() - b.getLat()) < ilat)
				&& (Math.abs(a.getLng() - b.getLng()) < ilng);
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public double getIlat() {
		return ilat;
	}

	public double getIlng() {
		return ilng;
	}

	public String getInf() {
		// return
		// "Nw: "+nW.getLat()+","+nW.getLng()+", tam:("+cols+","+rows+") offset:("+offCol+","+offRow+")";
		return "Nw: " + nW + "Se: " + sE;
	}

	/**
	 * Añade una coordenada a la caja y amplia sus limites si es necesario
	 * 
	 * @param c
	 * @return
	 */
	public boolean addToBox(LatLng c) {
		boolean change = false;
		if (nW == null || sE == null) {
			nW = new LatLng(c.getLat(), c.getLng());
			sE = new LatLng(c.getLat(), c.getLng());
			return true;
		} else {
			change = contains(c);
			if (!change) {
				if (c.isAboveOf(nW))
					nW = new LatLng(c.getLat(), nW.getLng());
				if (c.isLeftOf(nW))
					nW = new LatLng(nW.getLat(), c.getLng());
				if (c.isBelowOf(sE))
					sE = new LatLng(c.getLat(), sE.getLng());
				if (c.isRigthOf(sE))
					sE = new LatLng(sE.getLat(), c.getLng());
			}
		}
		return change;
	}

	public void addToBox(OsmWay way) {
		LatLngBox box = way.getBox();
		addToBox(box.getNw());
		addToBox(box.getSe());
	}

	public LatLng getNw() {
		return nW;
	}

	public LatLng getSe() {
		return sE;
	}

	public boolean contains(LatLngBox box) {
		return contains(box.getNw()) && contains(box.getSe());
	}

	public boolean contains(LatLng c) {
		return c.isContainedIn(nW, sE);
	}

	public boolean isDefined() {
		return nW != null && sE != null && ilat != 0 && ilng != 0
				&& tileSize != 0;
	}

	@Override
	public boolean equals(Object obj) {
		LatLngBox box = (LatLngBox) obj;
		return nW.equals(box.getNw()) && sE.equals(box.getSe())
				&& ilat == box.getIlat() && ilng == box.getIlng()
				&& tileSize == box.getTileSize();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (nW == null || sE == null)
			return " Undefined Box ";
		s.append("Nw: " + nW + ", Se: " + sE);
		if (tileSize != 0)
			s.append(", tile size: " + tileSize);
		if (ilat != 0 && ilng != 0)
			s.append(", incs [" + ilat + "," + ilng + "]");
		return s.toString();
	}

	private double smallest(double a, double b) {
		if (a > 0)
			return Math.min(a, b);
		return Math.max(a, b);
	}

	public void intersection(LatLngBox box) {
		nW = new LatLng(smallest(nW.getLat(), box.getNw().getLat()), smallest(
				nW.getLng(), box.getNw().getLng()));
		sE = new LatLng(smallest(sE.getLat(), box.getSe().getLat()), smallest(
				sE.getLng(), box.getSe().getLng()));
		tileSize = box.getTileSize();
	}

}
