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

public class GridSizeCalculator {

	public static void main(String[] args) {
		if (args.length != 6)
			System.out.println("Usage: " + args[0]
					+ " NorthWestLatitude NorthWestLongitude "
					+ "SouthEastLatitude SouthEastLongitude TileSize");

		double lat = Double.parseDouble(args[1]);
		double lng = Double.parseDouble(args[2]);
		LatLng NW = new LatLng(lat, lng);

		lat = Double.parseDouble(args[3]);
		lng = Double.parseDouble(args[4]);
		LatLng SE = new LatLng(lat, lng);

		int size[] = HexagonalGrid.calculateSize(NW, SE, Integer
				.parseInt(args[5]));
		int columns = size[0];
		int rows = size[0];

		System.out.println("Tamaño del grid: " + columns + "x" + rows + " -> "
				+ columns * rows);
		System.out.println("Tamaño del grid con corona: " + (columns + 2) + "x"
				+ (rows + 2) + " -> " + (columns + 2) * (rows + 2));
	}

}
