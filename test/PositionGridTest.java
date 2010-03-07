package test;

import util.DateAndTime;
import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;

public class PositionGridTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new DateAndTime(2000, 3, 15, 15, 3);
		HexagonalGrid grid = new HexagonalGrid(
				new LatLng(29.953260, -90.088238), new LatLng(29.918075,
						-90.053707), 0, 0, 50);
		long err = 0;
		System.out.println(grid.toString());

		for (int x = 0; x < grid.getDimX(); x++) {
			for (int y = 0; y < grid.getDimY(); y++) {
				LatLng c = grid.tileToCoord(x, y);
				try {
					Point p = grid.coordToTile(c);
					if (x != p.getX() || y != p.getY()) {
						System.err.println(c.toString() + " [" + x + "," + y
								+ "] != " + p.toString());
						err++;
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println(c + "[" + x + "," + y
							+ "] Index out of bounds");
					err++;
				}
			}
		}

		System.err.println("*** Errores = " + ((double) err)
				/ ((double) grid.getDimX() * grid.getDimY()));
	}

}
