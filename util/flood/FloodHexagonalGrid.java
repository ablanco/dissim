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

import java.util.ArrayList;

import util.HexagonalGrid;

public class FloodHexagonalGrid extends HexagonalGrid {

	protected double[][] gridWater; // Nivel de agua en la casilla
	protected boolean useGridMod;
	protected boolean[][] gridMod;

	public FloodHexagonalGrid(int x, int y, boolean useAgents) {
		super(x, y);
		gridWater = new double[x][y];
		useGridMod = !useAgents;
		if (useGridMod)
			gridMod = new boolean[x][y];
	}

	public double setWaterValue(int x, int y, double value) {
		double old = gridWater[x][y];
		gridWater[x][y] = value;
		return old;
	}

	@Override
	public double increaseValue(int x, int y, double increment) {
		double offset = 0;
		// La primera capa de agua se pone al nivel del resto, hay pues que
		// restar la parte no entera de la altura de terreno
		if (gridWater[x][y] == 0) // TODO not general
			offset = gridTerrain[x][y] - ((int) gridTerrain[x][y]);
		gridWater[x][y] += increment - offset;

		if (useGridMod)
			gridMod[x][y] = true;

		printGrid(); // TODO Debug
		return offset;
	}

	@Override
	public double decreaseValue(int x, int y, double decrement) {
		double result;
		// El nivel de agua no puede ser menor que cero
		if (gridWater[x][y] >= decrement) {
			gridWater[x][y] -= decrement;
			result = decrement;
		} else {
			result = gridWater[x][y];
			gridWater[x][y] = 0;
		}

		if (useGridMod)
			gridMod[x][y] = true;
		return result;
	}

	@Override
	public double getValue(int x, int y) {
		return gridTerrain[x][y] + gridWater[x][y];
	}

	public double getWaterValue(int x, int y) {
		return gridWater[x][y];
	}

	public ArrayList<int[]> getModCoordAndReset() {
		if (!useGridMod)
			return null;

		ArrayList<int[]> result = new ArrayList<int[]>();
		for (int i = 0; i < gridMod.length; i++) {
			for (int j = 0; j < gridMod[i].length; j++) {
				if (gridMod[i][j]) {
					result.add(new int[] { i, j });
					gridMod[i][j] = false;
				}
			}
		}
		return result;
	}

}
