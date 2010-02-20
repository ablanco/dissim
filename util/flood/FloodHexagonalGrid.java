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

import java.util.Set;
import java.util.TreeSet;

import util.HexagonalGrid;
import util.Point;

public class FloodHexagonalGrid extends HexagonalGrid {

	private static final long serialVersionUID = 1L;

	private short[][] gridWater; // Nivel de agua en la casilla
	private boolean useModifications;
	private TreeSet<Point> modTiles = null;

	public FloodHexagonalGrid(int x, int y, boolean useAgents) {
		super(x, y);
		gridWater = new short[x][y];
		useModifications = !useAgents;
		if (useModifications)
			modTiles = new TreeSet<Point>();
	}

	public short setWaterValue(int x, int y, short value) {
		short old = gridWater[x][y];
		gridWater[x][y] = value;
		return old;
	}

	@Override
	public void increaseValue(int x, int y, short increment) {
		gridWater[x][y] += increment;

		if (useModifications)
			modTiles.add(new Point(x, y));
	}

	@Override
	public short decreaseValue(int x, int y, short decrement) {
		short result;
		// El nivel de agua no puede ser menor que cero
		if (gridWater[x][y] >= decrement) {
			gridWater[x][y] -= decrement;
			result = decrement;
		} else {
			result = gridWater[x][y];
			gridWater[x][y] = 0;
		}

		if (useModifications)
			modTiles.add(new Point(x, y));

		return result;
	}

	@Override
	public short getValue(int x, int y) {
		return (short) (gridTerrain[x][y] + gridWater[x][y]);
	}

	public short getWaterValue(int x, int y) {
		return gridWater[x][y];
	}

	public Set<Point> getModCoordAndReset() {
		TreeSet<Point> result = modTiles;
		modTiles = new TreeSet<Point>();
		return result;
	}

}
