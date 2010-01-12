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

import java.util.ArrayList;

public class HexagonalGrid {

	private int[][] grid;

	public HexagonalGrid(int x, int y) {
		grid = new int[x][y];
	}

	public int setValue(int x, int y, int value) {
		int old = grid[x][y];
		grid[x][y] = value;
		return old;
	}

	public int getValue(int x, int y) {
		return grid[x][y];
	}

	public ArrayList<int[]> getAdjacents(int x, int y) {
		//TODO
		return null;
	}
	
}
