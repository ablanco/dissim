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
 * Flood version of hexagonal grid, has specific methods for managing the flood
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class FloodHexagonalGrid extends HexagonalGrid {

	private static final long serialVersionUID = 1L;

	private short[][] gridWater; // Nivel de agua en la casilla
	private short[] northWater;
	private short[] southWater;
	private short[] eastWater;
	private short[] westWater;

	private ModifiedTilesSet modTiles = null;

	/**
	 * New Flood hexagonal grid, like Hexagonal grid but this is better for a
	 * flooding
	 * 
	 * @param NW
	 *            Upper Left corner
	 * @param SE
	 *            Lower Right corner
	 * @param offX
	 *            column offset
	 * @param offY
	 *            row offset
	 * @param tileSize
	 *            tile size
	 */
	public FloodHexagonalGrid(LatLng NW, LatLng SE, int offX, int offY,
			int tileSize) {
		super(NW, SE, offX, offY, tileSize);
		gridWater = new short[columns][rows];
		northWater = new short[columns + 2];
		southWater = new short[columns + 2];
		eastWater = new short[rows];
		westWater = new short[rows];
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offX, offY);
	}

	/**
	 * Sets a water value in a concrete position, also updates the crown if any
	 * environment needs to update is water value
	 * 
	 * @param col
	 *            column
	 * @param row
	 *            row
	 * @param value
	 *            amount of water
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
	 * Gets water value of a concrete position of the grid, also gives values of
	 * the crown
	 * 
	 * @param col
	 *            column
	 * @param row
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
	 * Get the short[][] water matriz
	 * 
	 * @return water matrix
	 */
	public short[][] getGridWater() {
		return gridWater;
	}

	/**
	 * Is a flood border
	 * 
	 * @param col
	 * @param row
	 * @return true if adjacent has different values
	 */
	public boolean isFloodBorder(int col, int row) {
		short z = getValue(col, row);
		for (int[] a : getAdjacents(col, row)) {
			// Si está inundado y al mismo nivel de inundación
			if (getWaterValue(a[0], a[1]) != 0 && getValue(a[0], a[1]) != z) {
				return false;
			}
		}
		return true;
	}

	@Override
	/**
	 * Increase the water value by increment
	 * @param col
	 * @param row
	 * @param increment
	 */
	public void increaseValue(int x, int y, short increment) {
		short old = getWaterValue(x, y);
		setWaterValue(x, y, (short) (old + increment));
		modTiles.add(new Point(x, y));
	}

	@Override
	/**
	 * Decrease the water value by decrement
	 * @param col
	 * @param row
	 * @param decrement
	 * @return previous value 
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
	 * Get water+terrain value
	 * @param x column
	 * @param y row
	 * @return water+terrain value
	 */
	public short getValue(int x, int y) {
		return (short) (getTerrainValue(x, y) + getWaterValue(x, y));
	}

	@Override
	/**
	 * Get adjacents of the grid, adjacents contains values of water+terrain
	 * @param p point we want to know the adjacents
	 * @return list of adjacents
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

	@Override
	/**
	 * Updates grid with osm info
	 */
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
	 * Gets tiles that have been modified during simulation step
	 * 
	 * @return modified points
	 */
	public Set<Point> getModCoordAndReset() {
		ModifiedTilesSet result = modTiles;
		modTiles = new ModifiedTilesSet(columns + 2, rows + 2, offCol, offRow);
		return result.withoutNulls();
	}

}
