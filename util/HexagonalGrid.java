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
import java.util.HashSet;
import java.util.Random;

import osm.Osm;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;
import elevation.Elevation;

/**
 * To simulate the {@link Scenario} we need to discretize the real world, we
 * decided to use an hexagonal map discretization, this class has methods for
 * managing easily an hexagonal world
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class HexagonalGrid implements Serializable {

	private static final long serialVersionUID = 1L;

	private LatLngBox box;
	/**
	 * Coordinates of simulation area (rectangle) NW means North West point
	 */
	protected LatLng NW = null;
	/**
	 * SE means South East point
	 */
	protected LatLng SE = null;
	/**
	 * Increment in latitude in degrees between hexagons
	 */
	protected double ilat;
	/**
	 * Increment in longitude in degrees between hexagons
	 */
	protected double ilng;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private int tileSize;
	protected int columns;
	protected int rows;
	/**
	 * Global column of the 0,0 tile
	 */
	protected int offCol;
	/**
	 * Global row of the 0,0 tile
	 */
	protected int offRow;
	/**
	 * Terrain data
	 */
	protected short[][] gridTerrain;
	/**
	 * North external border
	 */
	protected short[] northTerrain;
	/**
	 * South external border
	 */
	protected short[] southTerrain;
	/**
	 * East external border
	 */
	protected short[] eastTerrain;
	/**
	 * West external border
	 */
	protected short[] westTerrain;
	/**
	 * Streets data
	 */
	protected short[][] gridStreets;
	/**
	 * North external border
	 */
	private short[] northStreets;
	/**
	 * South external border
	 */
	private short[] southStreets;
	/**
	 * East external border
	 */
	private short[] eastStreets;
	/**
	 * West external border
	 */
	private short[] westStreets;
	/**
	 * 1 elevation unit means (1/precision) meters
	 */
	private short precision = -1;

	/**
	 * New hexagonal grid given Upper Left (NW) and Lower Right (SE) corners of
	 * the map, offsets distinct from 0 if there are more than one environment
	 * and tile size for the simulation
	 * 
	 * @param NW
	 *            Upper Left corner
	 * @param SE
	 *            Lower Right corner
	 * @param offCol
	 *            offset
	 * @param offRow
	 *            offset
	 * @param tileSize
	 *            size of hexagons
	 */
	public HexagonalGrid(LatLng NW, LatLng SE, int offCol, int offRow,
			int tileSize) {
		this.NW = NW;
		this.SE = SE;
		this.offCol = offCol;
		this.offRow = offRow;
		this.tileSize = tileSize;

		// Calcular el tamaño de la rejilla en función de la distancia real y el
		// tamaño de los hexágonos
		int size[] = calculateSize(NW, SE, tileSize);
		int col = size[0];
		int row = size[1];

		// Debería funcionar tanto en el hemisferio norte como en el sur
		ilat = LatLng.round(Math.abs(NW.getLat() - SE.getLat()) / row);
		ilng = LatLng.round(Math.abs(NW.getLng() - SE.getLng()) / col);

		// Necesitamos el box para facilitar el manejo con datos de OSM que no
		// estan en nuestro grid pero que influyen en nuestro mapa
		box = new LatLngBox(NW, SE, tileSize);

		gridTerrain = new short[col][row];
		northTerrain = new short[col + 2];
		southTerrain = new short[col + 2];
		eastTerrain = new short[row];
		westTerrain = new short[row];
		gridStreets = new short[col][row];
		northStreets = new short[col + 2];
		southStreets = new short[col + 2];
		eastStreets = new short[row];
		westStreets = new short[row];
		columns = col;
		rows = row;
	}

	/**
	 * Estimates aproximate size in tiles from NW to SE, use carefully, could
	 * returns negative values
	 * 
	 * @param NW
	 *            Upper Left corner
	 * @param SE
	 *            Lower Right corner
	 * @param tileSize
	 *            size of the tile for the simulation
	 * @return size in tiles of the simulation area
	 */
	public static int[] calculateSize(LatLng NW, LatLng SE, int tileSize) {
		double ts = (double) tileSize;
		double hexWidth = ((ts / 2.0) * Math.cos(Math.PI / 6.0)) * 2.0;
		int x = (int) (NW.distance(new LatLng(NW.getLat(), SE.getLng())) / hexWidth);
		int y = (int) (NW.distance(new LatLng(SE.getLat(), NW.getLng())) / ((ts * 3.0) / 4.0));
		return new int[] { x, y };
	}

	/**
	 * Sets an elevation value for a position in the grid
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @param value
	 *            new terrain elevation
	 * @return previous terrain elevation
	 */
	public short setTerrainValue(int col, int row, short value) {
		col -= offCol;
		row -= offRow;
		short old;
		if (row == -1) {
			old = northTerrain[col + 1];
			northTerrain[col + 1] = value;
		} else if (row == rows) {
			old = southTerrain[col + 1];
			southTerrain[col + 1] = value;
		} else if (col == -1) {
			old = westTerrain[row];
			westTerrain[row] = value;
		} else if (col == columns) {
			old = eastTerrain[row];
			eastTerrain[row] = value;
		} else {
			old = gridTerrain[col][row];
			gridTerrain[col][row] = value;
		}
		return old;
	}

	/**
	 * Gets terrain value from the grid
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @return terrain value
	 */
	public short getTerrainValue(int col, int row) {
		col -= offCol;
		row -= offRow;
		short value;
		if (row == -1) {
			value = northTerrain[col + 1];
		} else if (row == rows) {
			value = southTerrain[col + 1];
		} else if (col == -1) {
			value = westTerrain[row];
		} else if (col == columns) {
			value = eastTerrain[row];
		} else {
			value = gridTerrain[col][row];
		}
		return value;
	}

	/**
	 * Sets a street value for a position in the grid
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @param value
	 *            new street value
	 * @return previous street value
	 */
	public short setStreetValue(int col, int row, short value) {
		col -= offCol;
		row -= offRow;
		short old;
		if (row == -1) {
			old = northStreets[col + 1];
			northStreets[col + 1] = value;
		} else if (row == rows) {
			old = southStreets[col + 1];
			southStreets[col + 1] = value;
		} else if (col == -1) {
			old = westStreets[row];
			westStreets[row] = value;
		} else if (col == columns) {
			old = eastStreets[row];
			eastStreets[row] = value;
		} else {
			old = gridStreets[col][row];
			gridStreets[col][row] = value;
		}
		return old;
	}

	/**
	 * Sets a street value for a position in the grid. Also updates the
	 * surrounding crown. Watch out the offsets
	 * 
	 * @param p
	 *            point
	 * @param value
	 *            new street value
	 * @return previous street value
	 */
	public short setStreetValue(Point p, short value) {
		return setStreetValue(p.getCol(), p.getRow(), value);
	}

	/**
	 * Gets street value from the grid. Watch out the offsets
	 * 
	 * @param col
	 * @param row
	 * @return street value
	 */
	public short getStreetValue(int col, int row) {
		col -= offCol;
		row -= offRow;
		short value;
		if (row == -1) {
			value = northStreets[col + 1];
		} else if (row == rows) {
			value = southStreets[col + 1];
		} else if (col == -1) {
			value = westStreets[row];
		} else if (col == columns) {
			value = eastStreets[row];
		} else {
			value = gridStreets[col][row];
		}
		return value;
	}

	/**
	 * Gets street value from the grid
	 * 
	 * @param p
	 *            Absolute {@link Point}
	 * @return street value
	 */
	public short getStreetValue(Point p) {
		return getStreetValue(p.getCol(), p.getRow());
	}

	/**
	 * Increase current terrain elevation of given position
	 * 
	 * @param x
	 *            Absolute position column
	 * @param y
	 *            Absolute position row
	 * @param increment
	 *            for current value
	 */
	public void increaseValue(int x, int y, short increment) {
		short old = getTerrainValue(x, y);
		setTerrainValue(x, y, (short) (old + increment));
	}

	/**
	 * Decrease current terrain elevation of given position
	 * 
	 * @param x
	 *            Absolute position column
	 * @param y
	 *            Absolute position row
	 * @param decrement
	 *            for current value
	 * @return previous value
	 */
	public short decreaseValue(int x, int y, short decrement) {
		short old = getTerrainValue(x, y);
		setTerrainValue(x, y, (short) (old - decrement));
		return decrement;
	}

	/**
	 * Gets current terrain elevation
	 * 
	 * @param x
	 *            Absolute position column
	 * @param y
	 *            Absolute position row
	 * @return terrain elevation
	 */
	public short getValue(int x, int y) {
		return getTerrainValue(x, y);
	}

	/**
	 * Gets the box of coodinates
	 * 
	 * @return box
	 */
	public LatLngBox getBox() {
		return box;
	}

	/**
	 * Gets the number of columns of the grid
	 * 
	 * @return columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Gets the number of row of the grid
	 * 
	 * @return rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Gets the columns offset of the grid
	 * 
	 * @return offset
	 */
	public int getOffCol() {
		return offCol;
	}

	/**
	 * Gets the rows offset of the grid
	 * 
	 * @return offset
	 */
	public int getOffRow() {
		return offRow;
	}

	/**
	 * Returns the indexes of the adjacent tiles of the given one
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @return adjacents indexes array
	 */
	public int[][] getAdjacentsIndexes(int col, int row) {
		col -= offCol;
		row -= offRow;
		int[][] adjacents = new int[6][2];
		int cont = 0;

		boolean par = ((row % 2) == 0);
		// Caso fila impar
		int colIni = col;
		int colFin = col + 1;
		// Caso fila par
		if (par) {
			colIni = col - 1;
			colFin = col;
		}

		for (int rowAux = row - 1; rowAux <= row + 1; rowAux++) {
			for (int colAux = colIni; colAux <= colFin; colAux++) {
				if (rowAux == row && colAux == col) {
					if (par)
						colAux = col + 1;
					else
						colAux = col - 1;
				}
				// Comprobamos que el hexágono adyacente no está fuera de la
				// rejilla
				if (colAux >= -1 && colAux <= columns && rowAux >= -1
						&& rowAux <= rows) {
					adjacents[cont][0] = colAux;
					adjacents[cont][1] = rowAux;
					cont++;
				}
				if (rowAux == row && colAux == col - 1 && !par)
					colAux++;
			}
		}

		int[][] result = new int[cont][2];
		for (int i = 0; i < cont; i++) {
			result[i][0] = adjacents[i][0] + offCol;
			result[i][1] = adjacents[i][1] + offRow;
		}
		return result;
	}

	/**
	 * Returns the indexes of the adjacent tiles of the given one
	 * 
	 * @param col
	 *            Absolute position column
	 * @param row
	 *            Absolute position row
	 * @return adjacents indexes list
	 */
	public ArrayList<int[]> getAdjacents(int col, int row) {
		ArrayList<int[]> result = new ArrayList<int[]>(6);
		int[] adjacent;
		int[][] indexes = getAdjacentsIndexes(col, row);
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
	 * Returns the adjacents {@link Point}s of the given one
	 * 
	 * @param p
	 *            Absolute {@link Point}
	 * @return adjacents {@link Point}s set
	 */
	public HashSet<Point> getAdjacents(Point p) {
		int[][] indexes = getAdjacentsIndexes(p.getCol(), p.getRow());
		HashSet<Point> result = new HashSet<Point>(indexes.length);
		for (int i = 0; i < indexes.length; i++) {
			result.add(new Point(indexes[i][0], indexes[i][1], getValue(
					indexes[i][0], indexes[i][1])));
		}
		return result;
	}

	/**
	 * Gets simulation geographical area
	 * 
	 * @return simulation geographical area
	 */
	public LatLng[] getArea() {
		return new LatLng[] { NW, SE };
	}

	/**
	 * Gets the size of tiles in meters
	 * 
	 * @return the size of tiles in meters
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Convert grid coordinates to the corresponding geographical coordinates
	 * (with elevation data)
	 * 
	 * @param col
	 *            Aboslute position column
	 * @param row
	 *            Aboslute position row
	 * @return {@link LatLng} geographical coordinate
	 */
	public LatLng tileToCoord(int col, int row) {
		if (NW == null || SE == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");
		col -= offCol;
		row -= offRow;

		double lat = NW.getLat();
		double lng = NW.getLng();

		if (row % 2 == 0) {
			lng += ilng / 2.0;
		} else {
			lng += ilng;
		}
		lat -= ilat * (2.0 / 3.0);

		lng += ilng * col;
		lat -= ilat * row;

		return new LatLng(lat, lng, getValue(col + offCol, row + offRow));
	}

	/**
	 * Convert a point in the grid into a coordinate in the real world
	 * 
	 * @param p
	 *            Abosulte grid {@link Point}
	 * @return {@link LatLng} geographical coordinate
	 */
	public LatLng tileToCoord(Point p) {
		return tileToCoord(p.getCol(), p.getRow());
	}

	/**
	 * Convert geographical coordinates to the corresponding grid coordinates
	 * (with elevation data)
	 * 
	 * @param coord
	 *            Geographical coordinate
	 * @return grid coordinate
	 */
	public Point coordToTile(LatLng coord) {
		if (tileSize < 0)
			throw new IllegalStateException(
					"The size of the tiles hasn't been defined yet.");
		int[] aprox = calculateSize(NW, coord, tileSize);
		int col = aprox[0];
		int row = aprox[1];
		col += offCol;
		row += offRow;
		// Buscamos la minima distancia
		double distMin = coord.distance(tileToCoord(col, row));
		boolean mejor = true;
		while ((distMin * 2) > tileSize && mejor) {
			// Consultamos todos los adyacentes
			mejor = false;
			for (Point point : getAdjacents(new Point(col, row))) {
				LatLng aux = tileToCoord(point);
				double dist = coord.distance(aux);
				// Nos quedamos con el más cercano
				if (dist < distMin) {
					distMin = dist;
					col = point.getCol();
					row = point.getRow();
					mejor = true;
				}
			}
		}
		return new Point(col, row, getValue(col, row));
	}

	/**
	 * Obtains (or generates a random one) the elevation data for this grid
	 * 
	 * @param random
	 *            Generate random terrain elevation or not
	 * @param server
	 *            Url of the DataBase server
	 * @param port
	 *            DataBase port
	 * @param db
	 *            Name of the DataBase
	 * @param user
	 *            DataBase user
	 * @param pass
	 *            DataBase password of the user
	 * @param driver
	 *            JDBC driver to use
	 * @throws IllegalStateException
	 */
	public void obtainTerrainElevation(boolean random, String server, int port,
			String db, String user, String pass, String driver)
			throws IllegalStateException {

		if (!random) {
			if (precision <= 0)
				throw new IllegalStateException(
						"Precision hasn't been defined yet.");

			Elevation.getElevations(this, server, port, db, user, pass, driver);
		} else {
			// Alturas aleatorias
			int endX = offCol + columns;
			int endY = offRow + rows;
			Random rnd = new Random(System.currentTimeMillis());
			for (int i = offCol - 1; i <= endX; i++) {
				for (int j = offRow - 1; j <= endY; j++) {
					setTerrainValue(i, j, (short) rnd.nextInt(200));
				}
			}
		}
	}

	/**
	 * Obtains street data from Open Street Maps
	 */
	public void obtainStreetInfo() {
		Osm.setOsmMapInfo(this);
	}

	/**
	 * Returns degree increments in latitude and longitude
	 * 
	 * @return degree increments in latitude and longitude
	 */
	public double[] getIncs() {
		return new double[] { ilat, ilng };
	}

	@Override
	public String toString() {
		String s = "Box: " + NW.toString() + ", " + SE.toString()
				+ ", Diagonal: " + (int) NW.distance(SE) + " m ";
		s += "- Dimensions: " + columns + "x" + rows + ", Width: "
				+ (int) NW.distance(new LatLng(NW.getLat(), SE.getLng()))
				+ " m, Height: "
				+ (int) NW.distance(new LatLng(SE.getLat(), NW.getLng()))
				+ " m ";
		s += "- Tile size: " + tileSize + " m";
		return s;
	}

	/**
	 * Sets elevation precision, 1 elevation unit means (1/precision) meters
	 * 
	 * @param precision
	 */
	public void setPrecision(short precision) {
		this.precision = precision;
	}

	/**
	 * Gets elevation precision, 1 elevation unit means (1/precision) meters
	 * 
	 * @return precision
	 */
	public short getPrecision() {
		return precision;
	}

	// STATIC DATA AND METHODS

	// Relative positions from point

	/**
	 * Environment is positioned left of this
	 */
	public static final int LEFT = 0;
	/**
	 * Environment is positioned left up of this
	 */
	public static final int LEFT_UP = 1;
	/**
	 * Environment is positioned right of this
	 */
	public static final int RIGHT_UP = 2;
	/**
	 * Environment is positioned right up of this
	 */
	public static final int RIGHT = 3;
	/**
	 * Environment is positioned right down of this
	 */
	public static final int RIGHT_DOWN = 4;
	/**
	 * Environment is positioned left down of this
	 */
	public static final int LEFT_DOWN = 5;

	/**
	 * Returns the adjacent hexagon to a that is in b direction
	 * 
	 * @param a
	 * @param b
	 * @return adjacent hexagon to a that is in b direction
	 */
	public static Point nearestHexagon(Point a, Point b) {
		if (a.equals(b))
			return a;
		int key = whichHexagonalMove(a, b);
		// movimiento(key);
		return hexagonalMoveTo(a, key);
	}

	/**
	 * Returns the move to point b, from point a
	 * 
	 * @param a
	 *            where we start
	 * @param b
	 *            where we want to get
	 * @return nearest point
	 */
	public static int whichHexagonalMove(Point a, Point b) {
		int col = a.getCol() - b.getCol();
		int row = a.getRow() - b.getRow();
		if (a.getRow() % 2 == 0) {
			// Even ROW
			// System.err.print(", even row: " + a.toString());
			if (col == 0) {
				if (row > 0) {
					// Derecha Arriba
					return RIGHT_UP;
				} else {
					// Derecha Abajo
					return RIGHT_DOWN;
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
				if (row > 0) {
					return RIGHT_UP;
				} else if (row < 0) {
					return RIGHT_DOWN;
				} else {
					return RIGHT;
				}
			}
		} else {
			// ODD ROW
			// System.err.print(", odd row" + a.toString());
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
					return RIGHT;
				} else if (row > 0) {
					// Der Arriba
					return RIGHT_UP;
				} else {
					// Der Abajo
					return RIGHT_DOWN;
				}
			} else {
				if (row > 0) {
					return LEFT_UP;
				} else if (row < 0) {
					return LEFT_DOWN;
				} else {
					// Izq
					return LEFT;
				}
			}
		}
	}

	/**
	 * Returns the adyacent hexagon to a in key direction
	 * 
	 * @param a
	 *            point
	 * @param key
	 *            direction
	 * @return the adyacent hexagon to a in key direction
	 */
	public static Point hexagonalMoveTo(Point a, int key) {
		int col = a.getCol();
		int row = a.getRow();

		if (row % 2 == 0) {
			// even row
			switch (key) {
			case LEFT: // Izquierda
				col--;
				break;
			case LEFT_UP: // Izquierda Arriba
				col--;
				row--;
				break;
			case RIGHT_UP: // Derecha Arriba
				row--;
				break;
			case RIGHT: // Derecha
				col++;
				break;
			case RIGHT_DOWN: // Derecha Abajo
				row++;
				break;
			case LEFT_DOWN: // Izquierda Abajo
				col--;
				row++;
				break;
			default:
				System.err.println("Movimiento hexagonal no permitido");
				break;
			}
		} else {
			// odd row
			switch (key) {
			case LEFT: // Izquierda
				col--;
				break;
			case LEFT_UP: // Izquierda Arriba
				row--;
				break;
			case RIGHT_UP: // Derecha Arriba
				row--;
				col++;
				break;
			case RIGHT: // Derecha
				col++;
				break;
			case RIGHT_DOWN: // Derecha Abajo
				row++;
				col++;
				break;
			case LEFT_DOWN: // Izquierda Abajo
				row++;
				break;
			default:
				System.err.println("Movimiento hexagonal no permitido");
				break;
			}
		}
		return new Point(col, row);
	}

	/**
	 * Hexagonal distance between two points in a grid
	 * 
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @return distance in tiles
	 */
	public static int distance(int col1, int row1, int col2, int row2) {
		int dist = 0;
		int incC = 1;
		if (col2 < col1)
			incC = -1;
		int incR = 1;
		if (row2 < row1)
			incR = -1;

		while (col1 != col2 || row1 != row2) {
			if (row1 == row2) {
				col1 += incC;
			} else {
				boolean parR = (row1 % 2 == 0);
				row1 += incR;
				if (parR && incC < 0 && col1 != col2) {
					col1 += incC;
				} else if (!parR && incC > 0 && col1 != col2) {
					col1 += incC;
				}
			}

			dist++;
		}

		return dist;
	}

	/**
	 * Hexagonal distance between two points in a grid
	 * 
	 * @param p1
	 * @param p2
	 * @return distance in tiles
	 */
	public static int distance(Point p1, Point p2) {
		return distance(p1.getCol(), p1.getRow(), p2.getCol(), p2.getRow());
	}
}
