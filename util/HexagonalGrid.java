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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import util.jcoord.LatLng;
import webservices.AltitudeWS;

public class HexagonalGrid implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Coordinates of simulation area (rectangle) NW means North West point
	 */
	protected LatLng NW = null;
	/**
	 * SE means South East point
	 */
	protected LatLng SE = null;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private int tileSize = 1;
	/**
	 * Grid data
	 */
	protected int dimX;
	protected int dimY;
	protected short[][] gridTerrain;
	/**
	 * External border
	 */
	protected short[] northTerrain;
	protected short[] southTerrain;
	protected short[] eastTerrain;
	protected short[] westTerrain;

	public HexagonalGrid(LatLng NW, LatLng SE, int tileSize) {
		this.NW = NW;
		this.SE = SE;
		
		// Calcular el tamaño de la rejilla en función de la distancia real y el
		// tamaño de los hexágonos
		double ts = tileSize;
		int x = (int) ((NW.distance(new LatLng(NW.getLat(), SE.getLng())) * 1000) / (((ts / 2.0) * Math
				.cos(Math.PI / 6.0)) * 2.0));
		int y = (int) (((NW.distance(new LatLng(SE.getLat(), NW.getLng())) * 1000) - (ts / 4.0)) / ((ts * 3.0) / 4.0));

		gridTerrain = new short[x][y];
		northTerrain = new short[x + 2];
		southTerrain = new short[x + 2];
		eastTerrain = new short[y];
		westTerrain = new short[y];
		dimX = x;
		dimY = y;
	}

	public short setTerrainValue(int x, int y, short value) {
		short old;
		if (y == -1) {
			old = northTerrain[x];
			northTerrain[x] = value;
		} else if (y == dimY) {
			old = southTerrain[x];
			southTerrain[x] = value;
		} else if (x == -1) {
			old = westTerrain[y];
			westTerrain[y] = value;
		} else if (x == dimX) {
			old = eastTerrain[y];
			eastTerrain[y] = value;
		} else {
			old = gridTerrain[x][y];
			gridTerrain[x][y] = value;
		}
		return old;
	}

	public short getTerrainValue(int x, int y) {
		short value;
		if (y == -1) {
			value = northTerrain[x];
		} else if (y == dimY) {
			value = southTerrain[x];
		} else if (x == -1) {
			value = westTerrain[y];
		} else if (x == dimX) {
			value = eastTerrain[y];
		} else {
			value = gridTerrain[x][y];
		}
		return value;
	}

	public void increaseValue(int x, int y, short increment) {
		short old = getTerrainValue(x, y);
		setTerrainValue(x, y, (short) (old + increment));
	}

	public short decreaseValue(int x, int y, short decrement) {
		short old = getTerrainValue(x, y);
		setTerrainValue(x, y, (short) (old - decrement));
		return decrement;
	}

	public short getValue(int x, int y) {
		return getTerrainValue(x, y);
	}

	public int getDimX() {
		return dimX;
	}

	public int getDimY() {
		return dimY;
	}

	/**
	 * Devuelve los índices de los hexágonos adyacentes al pedido (6 como
	 * máximo)
	 * 
	 * @param x
	 * @param y
	 * @return Una matriz cuyas filas representan las coordenadas de un hexágono
	 *         adyacente (si valen -1 es que había menos de 6 adyacentes)
	 */
	public int[][] getAdjacentsIndexes(int x, int y) {
		int[][] adjacents = new int[6][2];
		int cont = 0;

		boolean par = ((y % 2) == 0);
		// Caso fila impar
		int colIni = x;
		int colFin = x + 1;
		// Caso fila par
		if (par) {
			colIni = x - 1;
			colFin = x;
		}

		for (int fila = y - 1; fila <= y + 1; fila++) {
			for (int col = colIni; col <= colFin; col++) {
				if (fila == y && col == x) {
					if (par)
						col = x + 1;
					else
						col = x - 1;
				}
				// Comprobamos que el hexágono adyacente no está fuera de la
				// rejilla
				if (col >= -1 && col <= dimX && fila >= -1 && fila <= dimY) {
					adjacents[cont][0] = col;
					adjacents[cont][1] = fila;
					cont++;
				}
				if (fila == y && col == x - 1 && !par)
					col++;
			}
		}

		for (int i = cont; i < 6; i++) {
			adjacents[i][0] = -1;
			adjacents[i][1] = -1;
		}
		return adjacents;
	}

	/**
	 * Devuelve los hexágonos adyacentes al pedido (6 como máximo)
	 * 
	 * @param x
	 * @param y
	 * @return Una lista de arrays, cada array representa a un hexágono
	 *         adyacente y sus elementos son: columna, fila y valor.
	 */
	public ArrayList<int[]> getAdjacents(int x, int y) {
		ArrayList<int[]> result = new ArrayList<int[]>(6);
		int[] adjacent;
		int[][] indexes = getAdjacentsIndexes(x, y);
		for (int i = 0; i < 6; i++) {
			if (indexes[i][0] >= 0) {
				adjacent = new int[3];
				adjacent[0] = indexes[i][0];
				adjacent[1] = indexes[i][1];
				adjacent[2] = getValue(indexes[i][0], indexes[i][1]);
				result.add(adjacent);
			}
		}
		return result;
	}

	/**
	 * Returns a set of adjacents points
	 * 
	 * @param p
	 *            Point
	 * 
	 * @return Set<Point> adjacents to p
	 */
	public Set<Point> getAdjacents(Point p) {
		Set<Point> puntos = new TreeSet<Point>();
		for (int[] a : getAdjacents(p.getX(), p.getY())) {
			puntos.add(new Point(a[0], a[1], getValue(a[0], a[1])));
		}
		return puntos;
	}

	public LatLng[] getArea() {
		return new LatLng[] { NW, SE };
	}

	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Convert [x,y] to the corresponding LatLng Coordinate (with altitude)
	 * 
	 * @param x
	 *            lat
	 * @param y
	 *            lng
	 * @return LatLng
	 */
	public LatLng tileToCoord(int x, int y) {
		if (NW == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");
		double lng;
		double lat = (tileSize * x * 3 / 4);

		if (x % 2 != 0) {
			// odd Rows has offset.
			lng = ((tileSize / 2) + (tileSize * y));
		} else {
			lng = (tileSize * y);
		}

		return NW.metersToDegrees(lat, lng, getValue(x, y));
	}

	/**
	 * Convert from a coordinate to the position in the grid
	 * 
	 * @param coord
	 * @return
	 */
	public Point coordToTile(LatLng coord) {
		// TODO Esto no rula ni pa tras.
		return new Point(5, 5);
		
//		if (tileSize < 0)
//			throw new IllegalStateException(
//					"The size of the tiles hasn't been defined yet.");
//
//		// Aproximacion
//		int x = (int) (NW.distance(new LatLng(coord.getLat(), NW.getLng())) * 1000 / tileSize);
//		int y = (int) (NW.distance(new LatLng(NW.getLat(), coord.getLng())) * 1000 / tileSize);
//		// Try to adjust aproximation errors. 7%
//		short z = 0;
//
//		double distMin = coord.distance(tileToCoord(x, y));
//		boolean mejor = true;
//		// Dist ins given in kms, tilesize is diameter, so 1000/2=500
//		System.err.print("[" + x + "," + y + "] Dist min :" + distMin * 2000
//				+ " ? " + tileSize);
//		while ((distMin * 2000) > tileSize && mejor) {
//			// Look for all adyacents
//			mejor = false;
//			for (Point point : getAdjacents(new Point(x, y))) {
//				LatLng aux = tileToCoord(point.getX(), point.getY());
//				double dist = coord.distance(aux);
//				// Keeps the nearest
//				if (dist < distMin) {
//					distMin = dist;
//					x = point.getX();
//					y = point.getY();
//					z = point.getZ();
//					mejor = true;
//				}
//			}
//			System.err.print(" ," + distMin);
//		}
//		System.err.println();
//		return new Point(x, y, z);
	}

	/**
	 * Look for differents values of the adjacents values, if different, is
	 * border.
	 * 
	 * @return true is border, false if not
	 */
	public boolean isBorderPoint(Point p) {
		for (int[] a : getAdjacents(p.getX(), p.getY())) {
			if (p.getZ() != getValue(a[0], a[1])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Call a webservice to obtain the elevation of all tiles of the grid
	 */
	public void obtainTerrainElevation() {
		// int total = gridX * gridY;
		int cont = 0;
		for (int i = -1; i <= dimX; i++) {
			for (int j = -1; j <= dimY; j++) {
				LatLng coord = tileToCoord(i, j);
				double value = AltitudeWS.getElevation(coord);
				setTerrainValue(i, j, (short) value); // TODO
				// doubleToInner(value));
				cont++;
				// System.out.println("Obtenidas " + cont + " de " + total +
				// " alturas\r");
			}
		}
	}
}
