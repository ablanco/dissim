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
import util.ModifiedTilesSet;
import util.Point;
import util.jcoord.LatLng;

public class FloodHexagonalGrid extends HexagonalGrid {

	private static final long serialVersionUID = 1L;

	private short[][] gridWater; // Nivel de agua en la casilla
	private short[] northWater;
	private short[] southWater;
	private short[] eastWater;
	private short[] westWater;

	private ModifiedTilesSet modTiles = null;

	public FloodHexagonalGrid(LatLng NW, LatLng SE, int offX, int offY,
			int tileSize) {
		super(NW, SE, offX, offY, tileSize);
		gridWater = new short[columns][rows];
		northWater = new short[columns + 2];
		southWater = new short[columns + 2];
		eastWater = new short[rows];
		westWater = new short[rows];
		// modTiles = new TreeSet<Point>();
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offX, offY);
	}

	public short setWaterValue(int x, int y, short value) {
		x -= offX;
		y -= offY;
		short old;
		if (y == -1) {
			old = northWater[x + 1];
			northWater[x + 1] = value;
		} else if (y == rows) {
			old = southWater[x + 1];
			southWater[x + 1] = value;
		} else if (x == -1) {
			old = westWater[y];
			westWater[y] = value;
		} else if (x == columns) {
			old = eastWater[y];
			eastWater[y] = value;
		} else {
			old = gridWater[x][y];
			gridWater[x][y] = value;
		}
		return old;
	}

	public short getWaterValue(int col, int row) {
		col -= offX;
		row -= offY;
		short value;
		if (row == -1) {
			value = northWater[col + 1];
		} else if (row == rows) {
			value = southWater[col + 1];
		} else if (col == -1) {
			value = westWater[row];
		} else if (col == columns) {
			value = eastWater[row];
		} else {
			value = gridWater[col][row];
		}
		return value;
	}

	@Override
	public void increaseValue(int x, int y, short increment) {
		short old = getWaterValue(x, y);
		setWaterValue(x, y, (short) (old + increment));
		modTiles.add(new Point(x, y));
	}

	@Override
	public short decreaseValue(int x, int y, short decrement) {
		short result;
		short old = getWaterValue(x, y);
		// El nivel de agua no puede ser menor que cero
		if (old >= decrement) {
			old -= decrement;
			result = decrement;
		} else {
			result = old;
			old = 0;
		}
		setWaterValue(x, y, old);
		modTiles.add(new Point(x, y));
		return result;
	}

	@Override
	public short getValue(int x, int y) {
		return (short) (getTerrainValue(x, y) + getWaterValue(x, y));
	}

	@Override
	public TreeSet<Point> getAdjacents(Point p) {
		TreeSet<Point> result = new TreeSet<Point>();
		int[][] indexes = getAdjacentsIndexes(p.getCol(), p.getRow());
		for (int i = 0; i < indexes.length; i++) {
			int[] tile = indexes[i];
			Point adj = new Point(tile[0], tile[1], getValue(tile[0], tile[1]),
					getWaterValue(tile[0], tile[1]), getStreetValue(tile[0],
							tile[1]));
			result.add(adj);
		}
		return result;
	}

	public Set<Point> getModCoordAndReset() {
		ModifiedTilesSet result = modTiles;
		// modTiles = new TreeSet<Point>();
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offX, offY);
		return result.withoutNulls();
	}

}
