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

package util.jcoord;

import java.io.Serializable;

import osm.OsmEdge;
import osm.OsmNode;
import osm.OsmWay;

/**
 * Scenarios and items must be geolocated to be a realistic simulation, so we
 * need some box for geolocalize big things, like scenarios, roads, seas,
 * rivers...
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class LatLngBox implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Position relative to the box, is In
	 */
	public static final int IN = 0;
	/**
	 * Position relative to the box, is Above
	 */
	public static final int ABOVE = 1;
	/**
	 * Position relative to the box, is Above Right
	 */
	public static final int ABOVE_RIGHT = 2;
	/**
	 * Position relative to the box, is Right
	 */
	public static final int RIGHT = 3;
	/**
	 * Position relative to the box, is Below Right
	 */
	public static final int BELOW_RIGHT = 4;
	/**
	 * Position relative to the box, is Below
	 */
	public static final int BELOW = 5;
	/**
	 * Position relative to the box, is Below Left
	 */
	public static final int BELOW_LEFT = 6;
	/**
	 * Position relative to the box, is Left
	 */
	public static final int LEFT = 7;
	/**
	 * Position relative to the box, is Above Left
	 */
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

	/**
	 * New empty Box
	 */
	public LatLngBox() {
	}

	/**
	 * New box, initializes with
	 * 
	 * @param NW
	 *            Upper Left corner
	 * @param SE
	 *            Lower Right corner
	 * @param tileSize
	 *            size of the tile
	 */
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

	/**
	 * Get the tile size
	 * 
	 * @return tile size
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Given a {@link OsmNode} Returns IN, ABOVE, ABOVE_RIGHT, RIGHT,
	 * BELOW_RIGHT, BELOW, BELOW_LEFT, LEFT, ABOVE_LEFT
	 * 
	 * @param n
	 *            OsmNode from which we want to know position
	 * @return position relative to us
	 */
	public int absoluteBoxPosition(OsmNode n) {
		return absoluteBoxPosition(n.getCoord());
	}

	/**
	 * Given a Latlng Returns IN, ABOVE, ABOVE_RIGHT, RIGHT, BELOW_RIGHT, BELOW,
	 * BELOW_LEFT, LEFT, ABOVE_LEFT
	 * 
	 * @param c
	 *            Coordinate from which we want to know position
	 * @return position relative to the box
	 */
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

	/**
	 * Returns true if the coordinate a is near coordinate b attending to box
	 * parameters, Math.abs(a.getLat() - b.getLat()) < ilat) &&
	 * (Math.abs(a.getLng() - b.getLng()) < ilng
	 * 
	 * @param a
	 *            coordinate
	 * @param b
	 *            coordinate
	 * @return true if they are near according to the box parameters.
	 */
	public boolean closeTo(LatLng a, LatLng b) {
		return (Math.abs(a.getLat() - b.getLat()) < ilat)
				&& (Math.abs(a.getLng() - b.getLng()) < ilng);
	}

	/**
	 * Set tile size
	 * 
	 * @param tileSize
	 *            new tile size
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * Gets latitude increment for the box
	 * 
	 * @return latitude increment
	 */
	public double getIlat() {
		return ilat;
	}

	/**
	 * Gets longitude increment for the box
	 * 
	 * @return longitude increment
	 */
	public double getIlng() {
		return ilng;
	}

	/**
	 * Gets important info from the box as a {@link String}
	 * 
	 * @return info
	 */
	public String getInf() {
		return "Nw: " + nW + "Se: " + sE;
	}

	/**
	 * Adds a coordinate to the box, and makes the box bigger if needed
	 * 
	 * @param c
	 * @return true if an already contained coordinate has changed as result of
	 *         this
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

	/**
	 * Adds the {@link OsmWay} coordinates to this box
	 * 
	 * @param way
	 */
	public void addToBox(OsmWay way) {
		LatLngBox box = way.getBox();
		addToBox(box.getNw());
		addToBox(box.getSe());
	}

	/**
	 * Gets the NW coordinate
	 * 
	 * @return the NW coordinate
	 */
	public LatLng getNw() {
		return nW;
	}

	/**
	 * Gets the SE coordinate
	 * 
	 * @return the SE coordinate
	 */
	public LatLng getSe() {
		return sE;
	}

	/**
	 * Returns true if this box contains the specified one
	 * 
	 * @param box
	 * @return true if this box contains the specified one
	 */
	public boolean contains(LatLngBox box) {
		return contains(box.getNw()) && contains(box.getSe());
	}

	/**
	 * Returns true if the specified coordinate is contained in the box
	 * 
	 * @param c
	 * @return true if the specified coordinate is contained in the box
	 */
	public boolean contains(LatLng c) {
		return c.isContainedIn(nW, sE);
	}

	/**
	 * Returns true if the specified {@link OsmNode} is contained in the box
	 * 
	 * @param n
	 * @return true if the specified {@link OsmNode} is contained in the box
	 */
	public boolean contains(OsmNode n) {
		return contains(n.getCoord());
	}

	/**
	 * Returns true if the specified {@link OsmEdge} is contained in the box
	 * 
	 * @param e
	 * @return true if the specified {@link OsmEdge} is contained in the box
	 */
	public boolean contains(OsmEdge e) {
		return contains(e.getNodeA()) || contains(e.getNodeB());
	}

	/**
	 * Returns true if the specified {@link OsmEdge} cuts the border of this box
	 * 
	 * @param e
	 * @return true if the specified {@link OsmEdge} cuts the border of this box
	 */
	public boolean cutsBox(OsmEdge e) {
		return contains(e)
				&& !(contains(e.getNodeA()) && contains(e.getNodeB()));
	}

	/**
	 * Returns true if the box parameters are initialized
	 * 
	 * @return true if the box parameters are initialized
	 */
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

	/**
	 * Returns the minor of the specified doubles
	 * 
	 * @param a
	 * @param b
	 * @return the minor of the specified doubles
	 */
	private double smallest(double a, double b) {
		if (a > 0)
			return Math.min(a, b);
		return Math.max(a, b);
	}

	/**
	 * Set this box as the intersection of this box and the specified one
	 * 
	 * @param box
	 */
	public void intersection(LatLngBox box) {
		nW = new LatLng(smallest(nW.getLat(), box.getNw().getLat()), smallest(
				nW.getLng(), box.getNw().getLng()));
		sE = new LatLng(smallest(sE.getLat(), box.getSe().getLat()), smallest(
				sE.getLng(), box.getSe().getLng()));
		tileSize = box.getTileSize();
	}

}
