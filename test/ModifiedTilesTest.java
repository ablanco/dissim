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

package test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import util.Point;
import util.flood.FloodHexagonalGrid;

public class ModifiedTilesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FloodHexagonalGrid grid = new FloodHexagonalGrid(3, 3, false);

		grid.setTerrainValue(0, 0, (short) -2);
		grid.setTerrainValue(0, 1, (short) -4);
		grid.setTerrainValue(0, 2, (short) -5);
		grid.setTerrainValue(1, 0, (short) 0);
		grid.setTerrainValue(1, 1, (short) 1);
		grid.setTerrainValue(1, 2, (short) -2);
		grid.setTerrainValue(2, 0, (short) 2);
		grid.setTerrainValue(2, 1, (short) 4);
		grid.setTerrainValue(2, 2, (short) 1);
		grid.setWaterValue(0, 0, (short) 4);
		grid.setWaterValue(0, 1, (short) 6);
		grid.setWaterValue(0, 2, (short) 6);
		grid.setWaterValue(1, 0, (short) 1);

		int s1 = grid.getModCoordAndReset().size();

		grid.increaseValue(0, 2, (short) 5);
		grid.increaseValue(2, 1, (short) 2);

		int s2 = grid.getModCoordAndReset().size();

		grid.decreaseValue(0, 2, (short) 3);
		grid.increaseValue(2, 0, (short) 2);
		grid.increaseValue(1, 1, (short) 2); // Trick here
		grid.increaseValue(1, 1, (short) 3); // Same tile
		grid.increaseValue(2, 2, (short) 2);

		Set<Point> set = grid.getModCoordAndReset();
		int s3 = set.size();

		System.out.println("Expected output:  0 - 2 - 4");
		System.out.println("Generated output: " + s1 + " - " + s2 + " - " + s3);

		System.out.println("\nContent of last set:");
		Iterator<Point> it = set.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}

		int[] a1 = new int[] { 0, 2 };
		int[] a2 = new int[] { 0, 2 };
		System.out.println("\nint[] a1 = new int[] { 0, 2 };");
		System.out.println("int[] a2 = new int[] { 0, 2 };");
		System.out.println("FAIL -> a1.equals(a2) = " + a1.equals(a2));

		Point p1 = new Point(0, 2);
		Point p2 = new Point(0, 2);
		System.out.println("\nPoint p1 = new Point(0, 2);");
		System.out.println("Point p2 = new Point(0, 2);");
		System.out.println("WIN -> p1.equals(p2) = " + p1.equals(p2));
		System.out.println("Adding p1 and p2 to a HashSet, asking size:");
		set = new HashSet<Point>();
		set.add(p1);
		set.add(p2);
		System.out.println("Expected: 1 -> Generated: " + set.size());
		System.out.println("Adding p1 and p2 to a TreeSet, asking size:");
		set = new TreeSet<Point>();
		set.add(p1);
		set.add(p2);
		System.out.println("Expected: 1 -> Generated: " + set.size());
	}

}
