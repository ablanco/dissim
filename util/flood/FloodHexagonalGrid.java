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

package util.flood;

import util.HexagonalGrid;

public class FloodHexagonalGrid extends HexagonalGrid {

	protected double[][] gridWater; // Nivel de agua en la casilla 

	public FloodHexagonalGrid(int x, int y) {
		super(x, y);
		gridWater = new double[x][y];
	}

	public double setWaterValue(int x, int y, double value) {
		double old = gridWater[x][y];
		gridWater[x][y] = value;
		return old;
	}

	@Override
	public void increaseValue(int x, int y, double increment) {
		gridWater[x][y] += increment;
		printGrid(); // TODO Debug
	}

	@Override
	public void decreaseValue(int x, int y, double decrement) {
		gridWater[x][y] -= decrement;
	}

	@Override
	public double getValue(int x, int y) {
		return gridTerrain[x][y] + gridWater[x][y];
	}

	public double getWaterValue(int x, int y) {
		return gridWater[x][y];
	}

}
