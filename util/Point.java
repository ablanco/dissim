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

public class Point implements Comparable<Point>, Serializable {

	private static final long serialVersionUID = 1L;

	private int col;
	private int row;
	private short z;
	private boolean isIn = true;
	private short w = 0;
	private short s = 0;

	public Point(int col, int row) {
		this(col, row, (short) 0);
	}

	public Point(int col, int row, short z) {
		this.col = col;
		this.row = row;
		this.z = z;
	}

	public Point(int[] xyz) {
		this(xyz[0], xyz[1], (short) xyz[2]);
	}

	// Optional data constructors
	public Point(int col, int row, short z, boolean isIn) {
		this(col, row, z);
		this.isIn = isIn;
	}

	public Point(int col, int row, short z, short w, short s) {
		this(col, row, z);
		this.w = w;
		this.s = s;
	}

	public boolean isIn() {
		return isIn;
	}

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
		return "[(" + col + "," + row + ") " + z + "]";
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public short getZ() {
		return z;
	}

	public short getW() {
		return w;
	}

	public short getS() {
		return s;
	}
}