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

import osm.GetOSMInfo;
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
	 * Increment in degrees between hexagons
	 */
	protected double ilat;
	protected double ilng;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private int tileSize;
	/**
	 * Grid data
	 */
	protected int dimX;
	protected int dimY;
	protected int offX; // Index of the 0,0 tile
	protected int offY;
	protected short[][] gridTerrain;
	/**
	 * External border
	 */
	protected short[] northTerrain;
	protected short[] southTerrain;
	protected short[] eastTerrain;
	protected short[] westTerrain;
	/**
	 * Streets data
	 */
	protected short[][] gridStreets;
	private short[] northStreets;
	private short[] southStreets;
	private short[] eastStreets;
	private short[] westStreets;

	public HexagonalGrid(LatLng NW, LatLng SE, int offX, int offY, int tileSize) {
		this.NW = NW;
		this.SE = SE;
		this.offX = offX;
		this.offY = offY;
		this.tileSize = tileSize;

		// Calcular el tamaño de la rejilla en función de la distancia real y el
		// tamaño de los hexágonos
		int size[] = calculateSize(NW, SE, tileSize);
		int x = size[0];
		int y = size[1];

		ilat = Math.abs(NW.getLat() - SE.getLat()) / x;
		ilng = Math.abs(NW.getLng() - SE.getLng()) / y;

		gridTerrain = new short[x][y];
		northTerrain = new short[x + 2];
		southTerrain = new short[x + 2];
		eastTerrain = new short[y];
		westTerrain = new short[y];
		gridStreets = new short[x][y];
		northStreets = new short[x + 2];
		southStreets = new short[x + 2];
		eastStreets = new short[y];
		westStreets = new short[y];
		dimX = x;
		dimY = y;
	}

	public static int[] calculateSize(LatLng NW, LatLng SE, int tileSize) {
		double ts = (double) tileSize;
		double hexWidth = ((ts / 2.0) * Math.cos(Math.PI / 6.0)) * 2.0;
		int x = (int) (NW.distance(new LatLng(NW.getLat(), SE.getLng())) / hexWidth);
		int y = (int) (NW.distance(new LatLng(SE.getLat(), NW.getLng())) / ((ts * 3.0) / 4.0));
		return new int[] { x, y };
	}

	public short setTerrainValue(int x, int y, short value) {
		x -= offX;
		y -= offY;
		short old;
		if (y == -1) {
			old = northTerrain[x + 1];
			northTerrain[x + 1] = value;
		} else if (y == dimY) {
			old = southTerrain[x + 1];
			southTerrain[x + 1] = value;
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
		x -= offX;
		y -= offY;
		short value;
		if (y == -1) {
			value = northTerrain[x + 1];
		} else if (y == dimY) {
			value = southTerrain[x + 1];
		} else if (x == -1) {
			value = westTerrain[y];
		} else if (x == dimX) {
			value = eastTerrain[y];
		} else {
			value = gridTerrain[x][y];
		}
		return value;
	}

	public short setStreetValue(int x, int y, short value) {
		x -= offX;
		y -= offY;
		short old;
		if (y == -1) {
			old = northStreets[x + 1];
			northStreets[x + 1] = value;
		} else if (y == dimY) {
			old = southStreets[x + 1];
			southStreets[x + 1] = value;
		} else if (x == -1) {
			old = westStreets[y];
			westStreets[y] = value;
		} else if (x == dimX) {
			old = eastStreets[y];
			eastStreets[y] = value;
		} else {
			old = gridStreets[x][y];
			gridStreets[x][y] = value;
		}
		return old;
	}

	public short getStreetValue(int x, int y) {
		x -= offX;
		y -= offY;
		short value;
		if (y == -1) {
			value = northStreets[x + 1];
		} else if (y == dimY) {
			value = southStreets[x + 1];
		} else if (x == -1) {
			value = westStreets[y];
		} else if (x == dimX) {
			value = eastStreets[y];
		} else {
			value = gridStreets[x][y];
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

	public int getOffX() {
		return offX;
	}

	public int getOffY() {
		return offY;
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
		x -= offX;
		y -= offY;
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
					adjacents[cont][0] = col + offX;
					adjacents[cont][1] = fila + offY;
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
		if (NW == null || SE == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");

		double lat = NW.getLat();
		double lng = NW.getLng();

		if (y % 2 == 0) {
			lat += ilat / 2.0;
		} else {
			lat += ilat;
		}
		lat += ilat * x;
		lng += (4.0 / 6.0) * ilng;
		lng += ilng * y;

		return new LatLng(lat, lng, getValue(x, y));
	}

	/**
	 * Convert from a coordinate to the position in the grid
	 * 
	 * @param coord
	 * @return
	 */
	public Point coordToTile(LatLng coord) {
		if (tileSize < 0)
			throw new IllegalStateException(
					"The size of the tiles hasn't been defined yet.");
		if (!coord.isContainedIn(NW, SE))
			throw new IndexOutOfBoundsException(
					"Coordinates are outside of the simulation area.");

		// Aproximación
		int[] aprox = calculateSize(NW, coord, tileSize);
		int x = aprox[0];
		int y = aprox[1];
		x += offX;
		y += offY;

		double distMin = coord.distance(tileToCoord(x, y));
		boolean mejor = true;
		while ((distMin * 2) > tileSize && mejor) {
			// Consultamos todos los adyacentes
			mejor = false;
			for (Point point : getAdjacents(new Point(x, y))) {
				LatLng aux = tileToCoord(point.getX(), point.getY());
				double dist = coord.distance(aux);
				// Nos quedamos con el más cercano
				if (dist < distMin) {
					distMin = dist;
					x = point.getX();
					y = point.getY();
					mejor = true;
				}
			}
		}
		return new Point(x, y, getValue(x, y));
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
		int endX = offX + dimX;
		int endY = offY + dimY;
		for (int i = offX - 1; i <= endX; i++) {
			for (int j = offY - 1; j <= endY; j++) {
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

	public void obtainStreetInfo() {
		GetOSMInfo osm = new GetOSMInfo(this);
		osm.fillMatrix();
	}

	@Override
	public String toString() {
		String s = "Box: " + NW.toString() + ", " + SE.toString()
				+ ", Diagonal: " + NW.distance(SE) + "m";
		s += "\nDimensions: [" + dimX + "," + dimY + "] ,width: "
				+ NW.distance(new LatLng(NW.getLat(), SE.getLng()))
				+ "m, height: "
				+ NW.distance(new LatLng(SE.getLat(), NW.getLng())) + "m";
		s += "\nTile size: " + tileSize + "m";
		return s;
	}
}
