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

package gui;

import java.awt.Polygon;

/**
 * Bidimensional hexagon
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class Hexagon2D extends Polygon {

	/**
	 * {@link Hexagon2D} constructor
	 * 
	 * @param x
	 *            component of center
	 * @param y
	 *            component of center
	 * @param r
	 *            radious
	 */
	public Hexagon2D(int x, int y, int r) {
		this(x, y, r, Math.PI / 2);
	}

	/**
	 * {@link Hexagon2D} constructor
	 * 
	 * @param x
	 *            component of center
	 * @param y
	 *            component of center
	 * @param r
	 *            radious
	 * @param a
	 *            initial rotation angle
	 */
	public Hexagon2D(int x, int y, int r, double a) {
		super(getXCoord(x, r, a), getYCoord(y, r, a), 6);

	}

	/**
	 * Returns the X coordinates of the vertices of the {@link Hexagon2D}
	 * 
	 * @param x
	 *            component of center
	 * @param r
	 *            radious
	 * @param a
	 *            initial rotation angle
	 * @return the X coordinates of the vertices of the {@link Hexagon2D}
	 */
	private static int[] getXCoord(int x, int r, double a) {
		int xcoord[] = new int[6];
		double aux = a;
		for (int i = 0; i < 6; i++) {
			xcoord[i] = (int) Math.round(r * Math.cos(aux)) + x;
			aux += Math.PI / 3;
		}
		return xcoord;
	}

	/**
	 * Returns the Y coordinates of the vertices of the {@link Hexagon2D}
	 * 
	 * @param y
	 *            component of center
	 * @param r
	 *            radious
	 * @param a
	 *            initial rotation angle
	 * @return the Y coordinates of the vertices of the {@link Hexagon2D}
	 */
	private static int[] getYCoord(int y, int r, double a) {
		int ycoord[] = new int[6];
		double aux = a;
		for (int i = 0; i < 6; i++) {
			ycoord[i] = (int) Math.round(r * Math.sin(aux)) + y;
			aux += Math.PI / 3;
		}
		return ycoord;
	}
}
