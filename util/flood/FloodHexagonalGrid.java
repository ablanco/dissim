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
import util.jcoord.LatLng;

public class FloodHexagonalGrid extends HexagonalGrid {

	private static final long serialVersionUID = 1L;

	private short[][] gridWater; // Nivel de agua en la casilla
	private short[] northWater;
	private short[] southWater;
	private short[] eastWater;
	private short[] westWater;

	private TreeSet<Point> modTiles = null;

	// TODO TreeSet no acaba de funcionar bien

	public FloodHexagonalGrid(LatLng NW, LatLng SE, int offX, int offY,
			int tileSize) {
		super(NW, SE, offX, offY, tileSize);
		gridWater = new short[dimX][dimY];
		northWater = new short[dimX + 2];
		southWater = new short[dimX + 2];
		eastWater = new short[dimY];
		westWater = new short[dimY];
		modTiles = new TreeSet<Point>();
	}

	public short setWaterValue(int x, int y, short value) {
		x -= offX;
		y -= offY;
		short old;
		if (y == -1) {
			old = northWater[x + 1];
			northWater[x + 1] = value;
		} else if (y == dimY) {
			old = southWater[x + 1];
			southWater[x + 1] = value;
		} else if (x == -1) {
			old = westWater[y];
			westWater[y] = value;
		} else if (x == dimX) {
			old = eastWater[y];
			eastWater[y] = value;
		} else {
			old = gridWater[x][y];
			gridWater[x][y] = value;
		}
		return old;
	}

	public short getWaterValue(int x, int y) {
		x -= offX;
		y -= offY;
		short value;
		if (y == -1) {
			value = northWater[x + 1];
		} else if (y == dimY) {
			value = southWater[x + 1];
		} else if (x == -1) {
			value = westWater[y];
		} else if (x == dimX) {
			value = eastWater[y];
		} else {
			value = gridWater[x][y];
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

	public Set<Point> getModCoordAndReset() {
		TreeSet<Point> result = modTiles;
		modTiles = new TreeSet<Point>();
		return result;
	}

}
