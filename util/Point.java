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

package util;

import java.io.Serializable;

/**
 * Point implementation for Hexagonal Grid, contains x, y and altitude
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Point implements Comparable<Point>, Serializable {

	private static final long serialVersionUID = 1L;

	private int col;
	private int row;
	private short altitude;
	/**
	 * water value
	 */
	private short w = 0;
	/**
	 * street value
	 */
	private short s = 0;
	private int hash;

	/**
	 * New point from parameters
	 * @param col
	 * @param row
	 */
	public Point(int col, int row) {
		this(col, row, (short) 0);
	}
	/**
	 * New point from parameters
	 * @param col
	 * @param row
	 * @param altitude altitude
	 */

	public Point(int col, int row, short altitude) {
		this.col = col;
		this.row = row;
		this.altitude = altitude;
		String s = col + "," + row;
		hash = s.hashCode();
	}

	/**
	 * New Point from array [Col, Row, Altitude]
	 * @param xyz [Col, Row, Altitude]
	 */
	public Point(int[] xyz) {
		this(xyz[0], xyz[1], (short) xyz[2]);
	}

	/**
	 * New point from parameters
	 * @param col
	 * @param row
	 * @param altitude
	 * @param w water value
	 * @param s street value
	 */
	public Point(int col, int row, short altitude, short w, short s) {
		this(col, row, altitude);
		this.w = w;
		this.s = s;
	}

	/**
	 * Check is p is adyacent
	 * @param p we want to know if is adyacent to this
	 * @return true is p is adyacent
	 */
	public boolean isAdyacent(Point p) {
		return Math.abs(col - p.getCol()) < 2 && Math.abs(row - p.getRow()) < 2
				&& !(col + 1 == p.getCol() && row + 1 == p.getRow())
				&& !(col + 1 == p.getCol() && row - 1 == p.getRow());
	}

	@Override
	public int compareTo(Point o) {
		if (o.col == col && o.row == row)
			return 0;

		if (o.col > col)
			if (o.row > row)
				return 2;
			else
				return 1;
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return (p.getCol() == col) && (p.getRow() == row);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[(" + col + "," + row + ") " + altitude + "]";
	}

	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * Get column number
	 * @return
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Get row number
	 * @return
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Gets altitude of the point
	 * @return altitude
	 */
	public short getZ() {
		return altitude;
	}

	/**
	 * Get water value of the point
	 * @return water value
	 */
	public short getW() {
		return w;
	}

	/**
	 * Get street value of the point <Osm>
	 * @return
	 */
	public short getS() {
		return s;
	}
}