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
import java.util.List;
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

		ilat = Math.abs(NW.getLat() - SE.getLat()) / y;
		ilng = Math.abs(NW.getLng() - SE.getLng()) / x;

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

	public short setStreetValue(Point p, short value) {
		return setStreetValue(p.getX(), p.getY(), value);
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

	public short getStreetValue(Point p) {
		return getStreetValue(p.getX(), p.getY());
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
	 *         adyacente
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
					adjacents[cont][0] = col;
					adjacents[cont][1] = fila;
					cont++;
				}
				if (fila == y && col == x - 1 && !par)
					col++;
			}
		}

		int[][] result = new int[cont][2];
		for (int i = 0; i < cont; i++) {
			result[i][0] = adjacents[i][0] + offX;
			result[i][1] = adjacents[i][1] + offY;
		}
		return result;
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
		for (int i = 0; i < indexes.length; i++) {
			adjacent = new int[3];
			adjacent[0] = indexes[i][0];
			adjacent[1] = indexes[i][1];
			adjacent[2] = getValue(indexes[i][0], indexes[i][1]);
			result.add(adjacent);
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
		TreeSet<Point> result = new TreeSet<Point>();
		int[][] indexes = getAdjacentsIndexes(p.getX(), p.getY());
		for (int i = 0; i < indexes.length; i++) {
			result.add(new Point(indexes[i][0], indexes[i][1], getValue(
					indexes[i][0], indexes[i][1])));
		}
		return result;
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
		x -= offX;
		y -= offY;

		double lat = NW.getLat();
		double lng = NW.getLng();

		if (y % 2 == 0) {
			lng += ilng / 2.0;
		} else {
			lng += ilng;
		}
		lat -= ilat * (2.0 / 3.0);

		lng += ilng * x;
		lat -= ilat * y;

		return new LatLng(lat, lng, getValue(x + offX, y + offY));
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
		s += "\nDimensions: [" + dimX + "," + dimY + "], Width: "
				+ NW.distance(new LatLng(NW.getLat(), SE.getLng()))
				+ "m, Height: "
				+ NW.distance(new LatLng(SE.getLat(), NW.getLng())) + "m";
		s += "\nTile size: " + tileSize + "m";
		return s;
	}

	// Relative positions from point
	public static int LEFT = 0;
	public static int LEFT_UP = 1;
	public static int RIGTH_UP = 2;
	public static int RIGTH = 3;
	public static int RIGTH_DOWN = 4;
	public static int LEFT_DOWN = 5;

	
	public static List<Point> getLineBetweenPoints(Point a, Point b){
		List<Point> line = new ArrayList<Point>();
		
		return line;
	}
	
	public static int wichtHexagonalMove(Point a, Point b) {
		int col = a.getY() - b.getY();
		int row = a.getX() - b.getX();
		if (a.getY() % 2 == 0) {
			//Even ROW
			System.err.print(", even row: "+a.toString());
			if (col == 0) {
				if (row > 0) {
					// Derecha Arriba
					return RIGTH_UP;
				} else {
					// Derecha Abajo
					return RIGTH_DOWN;
				}
			} else if (col > 0) {
				if (row == 0) {
					// Izquierda
					return LEFT;
				} else if (row > 0) {
					// Izquierda Arriba
					return LEFT_UP;
				} else {
					// Izquierda Abajo
					return LEFT_DOWN;
				}
			} else {
				// Derecha
				if(row > 0){
					return RIGTH_UP;
				}else if (row < 0){
					return RIGTH_DOWN;
				}else{
					return RIGTH;	
				}
			}
		} else {
			//ODD ROW
			System.err.print(", odd row"+a.toString());
			if (col == 0) {
				if (row > 0) {
					// Izq Arriba
					return LEFT_UP;
				} else {
					// Izq Abajo
					return LEFT_DOWN;
				}
			} else if (col < 0) {
				if (row == 0) {
					// Der
					return RIGTH;
				} else if (row > 0) {
					// Der Arriba
					return RIGTH_UP;
				} else {
					// Der Abajo
					return RIGTH_DOWN;
				}
			} else {
				if(row > 0){
					return LEFT_UP;
				}else if (row < 0){
					return LEFT_DOWN;
				}else{
					// Izq
					return LEFT;	
				}
			}
		}
	}

	public static Point hexagonalMoveTo(Point a, int key) {
		int y = a.getY();
		int x = a.getX();

		if (x % 2 == 0) {
			// even row
			switch (key) {
			case 0: // Izquierda
				y--;
				break;
			case 1: // Izquierda Arriba
				y--;
				x--;
				break;
			case 2: // Derecha Arriba
				x--;
				break;
			case 3: // Derecha
				y++;
				break;
			case 4: // Derecha Abajo
				x++;
				break;
			case 5: // Izquierda Abajo
				y--;
				x++;
				break;
			default:
				System.err.println("Movimiento hexagonal no permitido");
				break;
			}
		} else {
			// odd row
			switch (key) {
			case 0: // Izquierda
				y--;
				break;
			case 1: // Izquierda Arriba
				x--;
				break;
			case 2: // Derecha Arriba
				x--;
				y++;
				break;
			case 3: // Derecha
				y++;
				break;
			case 4: // Derecha Abajo
				x++;
				y++;
				break;
			case 5: // Izquierda Abajo
				x++;
				break;
			default:
				System.err.println("Movimiento hexagonal no permitido");
				break;
			}
		}
		return new Point(x, y);
	}
}
