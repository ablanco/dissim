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

import java.util.ArrayList;
import java.util.Iterator;

import util.HexagonalGrid;

public class GridTest {

	public static void main(String[] args) {
		HexagonalGrid grid = new HexagonalGrid(4, 4);

		System.out.println("(0,0) Expected: (1,0) (0,1)");
		System.out.print("     Generated: ");
		ArrayList<double[]> adjacents = grid.getAdjacents(0, 0);
		Iterator<double[]> it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out.println("(0,3) Expected: (0,2) (1,2) (1,3)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(0, 3);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out.println("(3,0) Expected: (2,0) (2,1) (3,1)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(3, 0);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out.println("(3,3) Expected: (3,2) (2,3)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(3, 3);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out
				.println("(2,1) Expected: (2,0) (3,0) (1,1) (3,1) (2,2) (3,2)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(2, 1);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out
				.println("(2,2) Expected: (1,1) (2,1) (1,2) (3,2) (1,3) (2,3)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(2, 2);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out.println("(0,2) Expected: (0,1) (1,2) (0,3)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(0, 2);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

		System.out.println("(0,1) Expected: (0,0) (1,0) (1,1) (0,2) (1,2)");
		System.out.print("     Generated: ");
		adjacents = grid.getAdjacents(0, 1);
		it = adjacents.iterator();
		while (it.hasNext()) {
			double[] tile = it.next();
			System.out.print("(" + (int) tile[0] + "," + (int) tile[1] + ") ");
		}
		System.out.println("\n");

	}

}
