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

import util.jcoord.LatLng;

/**
 * It calculates and shows the size of the grid that results of the given
 * coordinates and tile size.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class GridSizeCalculator {

	/**
	 * 
	 * @param args
	 *            NorthWestLatitude NorthWestLongitude SouthEastLatitude
	 *            SouthEastLongitude TileSize
	 */
	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("Parameters: NorthWestLatitude "
					+ "NorthWestLongitude SouthEastLatitude "
					+ "SouthEastLongitude TileSize");
			return;
		}

		double lat = Double.parseDouble(args[0]);
		double lng = Double.parseDouble(args[1]);
		LatLng NW = new LatLng(lat, lng);

		lat = Double.parseDouble(args[2]);
		lng = Double.parseDouble(args[3]);
		LatLng SE = new LatLng(lat, lng);

		int size[] = HexagonalGrid.calculateSize(NW, SE, Integer
				.parseInt(args[4]));
		int columns = size[0];
		int rows = size[1];

		System.out.println("Area: NW" + NW.toString() + " SE" + SE.toString());
		System.out.println("Hexagon size: " + args[4]);
		System.out.println("Grid size: " + columns + "x" + rows + " -> "
				+ columns * rows);
		System.out.println("Grid size with the extra border: " + (columns + 2)
				+ "x" + (rows + 2) + " -> " + (columns + 2) * (rows + 2));
		// + " [" + (((columns + 2) * (rows + 2)) * 0.3) / (60 * 60 * 24) +
		// " días]");
	}

}
