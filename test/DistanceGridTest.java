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

import util.HexagonalGrid;

public class DistanceGridTest {

	public static void main(String[] args) {
		int d = HexagonalGrid.distance(0, 0, 3, 3);
		System.out.println("Distance 0,0 a 3,3 | Expected 5 | Generated " + d);

		d = HexagonalGrid.distance(0, 5, 2, 2);
		System.out.println("Distance 0,5 a 2,2 | Expected 3 | Generated " + d);

		d = HexagonalGrid.distance(4, 1, 0, 3);
		System.out.println("Distance 4,1 a 0,3 | Expected 5 | Generated " + d);

		d = HexagonalGrid.distance(0, 0, 1, 1);
		System.out.println("Distance 0,0 a 1,1 | Expected 2 | Generated " + d);

		d = HexagonalGrid.distance(4, 4, 3, 3);
		System.out.println("Distance 4,4 a 3,3 | Expected 1 | Generated " + d);

		d = HexagonalGrid.distance(5, 4, 2, 1);
		System.out.println("Distance 5,4 a 2,1 | Expected 4 | Generated " + d);

		d = HexagonalGrid.distance(0, 0, 1, 5);
		System.out.println("Distance 0,0 a 1,5 | Expected 5 | Generated " + d);

		d = HexagonalGrid.distance(1, 5, 0, 0);
		System.out.println("Distance 1,5 a 0,0 | Expected 5 | Generated " + d);

		d = HexagonalGrid.distance(1, 4, 1, 4);
		System.out.println("Distance 1,4 a 1,4 | Expected 0 | Generated " + d);
	}

}
