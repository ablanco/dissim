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

import java.util.HashSet;
import java.util.Set;

import osm.Osm;

import util.HexagonalGrid;
import util.Point;
import util.java.ModifiedTilesSet;
import util.jcoord.LatLng;

/**
 * Flood version of {@link HexagonalGrid}, has specific methods for managing the
 * flood
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class FloodHexagonalGrid extends HexagonalGrid {

	private static final long serialVersionUID = 1L;

	/**
	 * Water levels
	 */
	private short[][] gridWater; // Nivel de agua en la casilla
	private short[] northWater;
	private short[] southWater;
	private short[] eastWater;
	private short[] westWater;

	private ModifiedTilesSet modTiles = null;

	/**
	 * New {@link FloodHexagonalGrid}
	 * 
	 * @param NW
	 *            Upper Left corner
	 * @param SE
	 *            Lower Right corner
	 * @param offCol
	 *            column offset
	 * @param offRow
	 *            row offset
	 * @param tileSize
	 *            tile size
	 */
	public FloodHexagonalGrid(LatLng NW, LatLng SE, int offCol, int offRow,
			int tileSize) {
		super(NW, SE, offCol, offRow, tileSize);
		gridWater = new short[columns][rows];
		northWater = new short[columns + 2];
		southWater = new short[columns + 2];
		eastWater = new short[rows];
		westWater = new short[rows];
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offCol, offRow);
	}

	/**
	 * Sets a water value in the specified position, also updates the external
	 * border
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @param value
	 *            quantity of water
	 * @return previous value
	 */
	public short setWaterValue(int col, int row, short value) {
		col -= offCol;
		row -= offRow;
		short old;
		if (row == -1) {
			old = northWater[col + 1];
			northWater[col + 1] = value;
		} else if (row == rows) {
			old = southWater[col + 1];
			southWater[col + 1] = value;
		} else if (col == -1) {
			old = westWater[row];
			westWater[row] = value;
		} else if (col == columns) {
			old = eastWater[row];
			eastWater[row] = value;
		} else {
			old = gridWater[col][row];
			gridWater[col][row] = value;
		}
		return old;
	}

	/**
	 * Gets water value of a specified position of the grid
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @return value
	 */
	public short getWaterValue(int col, int row) {
		col -= offCol;
		row -= offRow;
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

	/**
	 * Gets the short[][] water matrix
	 * 
	 * @return water matrix
	 */
	public short[][] getGridWater() {
		return gridWater;
	}

	@Override
	/**
	 * Increases the water value
	 * @param col Absolute position column
	 * @param row Absolute position row
	 * @param increment
	 */
	public void increaseValue(int x, int y, short increment) {
		short old = getWaterValue(x, y);
		setWaterValue(x, y, (short) (old + increment));
		modTiles.add(new Point(x, y));
	}

	@Override
	/**
	 * Decreases the water value
	 * @param col Absolute position column 
	 * @param row Absolute position row
	 * @param decrement
	 * @return actual decrement
	 */
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
	/**
	 * Gets water+terrain value
	 * @param x Absolute position column
	 * @param y Absolute position row
	 * @return water+terrain value
	 */
	public short getValue(int x, int y) {
		return (short) (getTerrainValue(x, y) + getWaterValue(x, y));
	}

	@Override
	/**
	 * Gets adjacents of the grid, adjacents contains values of water+terrain
	 * @param p point we want to know the adjacents of
	 * @return set of adjacents
	 */
	public HashSet<Point> getAdjacents(Point p) {
		int[][] indexes = getAdjacentsIndexes(p.getCol(), p.getRow());
		HashSet<Point> result = new HashSet<Point>(indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			int[] tile = indexes[i];
			Point adj = new Point(tile[0], tile[1], getValue(tile[0], tile[1]),
					getWaterValue(tile[0], tile[1]), getStreetValue(tile[0],
							tile[1]));
			result.add(adj);
		}
		return result;
	}

	/**
	 * Obtains street data from Open Street Maps, and sets as regular water
	 * rivers, lakes, etc.
	 */
	@Override
	public void obtainStreetInfo() {
		super.obtainStreetInfo();

		int endCol = offCol + columns;
		int endRow = offRow + rows;
		for (int i = offCol - 1; i <= endCol; i++) {
			for (int j = offRow - 1; j <= endRow; j++) {
				short s = getStreetValue(i, j);
				if (Osm.getType(s) == Osm.Waterway) {
					// Es un río, lago, mar, etc...
					short t = getTerrainValue(i, j);
					// 20% de profundidad
					if (t < 0) {
						setWaterValue(i, j, (short) (t * -0.2));
						setTerrainValue(i, j, (short) (t * 1.2));
					} else {
						setWaterValue(i, j, (short) (t * 0.2));
						setTerrainValue(i, j, (short) (t * 0.8));
					}
				}
			}
		}
	}

	/**
	 * Gets the tiles that have been modified during simulation step
	 * 
	 * @return set of modified {@link Point}s
	 */
	public Set<Point> getModCoordAndReset() {
		ModifiedTilesSet result = modTiles;
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offCol, offRow);
		return result.withoutNulls();
	}

}
