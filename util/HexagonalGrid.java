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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import osm.Osm;
import util.java.TempFiles;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;
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

	private LatLngBox box;

	protected int columns;
	protected int rows;
	protected int offCol; // Index of the 0,0 tile
	protected int offRow;
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
	/**
	 * 1 altitude unit means (1/precision) meters
	 */
	private short precision = -1;

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

		// ilat = (NW.getLat() - SE.getLat()) / row;
		// ilng = (NW.getLng() - SE.getLng()) / col;

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
	 * Calcula el tamaño en cuadrados desde NW a SE, cuidado puede devolver
	 * distancias negativas
	 * 
	 * @param NW
	 * @param SE
	 * @param tileSize
	 * @return
	 */
	public static int[] calculateSize(LatLng NW, LatLng SE, int tileSize) {
		double ts = (double) tileSize;
		double hexWidth = ((ts / 2.0) * Math.cos(Math.PI / 6.0)) * 2.0;
		int x = (int) (NW.distance(new LatLng(NW.getLat(), SE.getLng())) / hexWidth);
		int y = (int) (NW.distance(new LatLng(SE.getLat(), NW.getLng())) / ((ts * 3.0) / 4.0));
		return new int[] { x, y };
	}

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

	public short setStreetValue(Point p, short value) {
		return setStreetValue(p.getCol(), p.getRow(), value);
	}

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

	public short getStreetValue(Point p) {
		return getStreetValue(p.getCol(), p.getRow());
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

	public LatLngBox getBox() {
		return box;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public int getOffCol() {
		return offCol;
	}

	public int getOffRow() {
		return offRow;
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
		x -= offCol;
		y -= offRow;
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
				if (col >= -1 && col <= columns && fila >= -1 && fila <= rows) {
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
			result[i][0] = adjacents[i][0] + offCol;
			result[i][1] = adjacents[i][1] + offRow;
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
	 * @return HashSet<Point> adjacents to p
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

	public LatLng[] getArea() {
		return new LatLng[] { NW, SE };
	}

	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Convert [x,y] to the corresponding LatLng Coordinate (with altitude)
	 * 
	 * @param col
	 *            lat
	 * @param row
	 *            lng
	 * @return LatLng
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

	public LatLng tileToCoord(Point p) {
		return tileToCoord(p.getCol(), p.getRow());
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
	 * Given an outside coord from the BOX: NW,SE, returns an aproximate Point
	 * inside the box
	 * 
	 * @param coord
	 * @return
	 */
	public Point aproxCoordToTile(LatLng coord) {
		int[] aprox = calculateSize(NW, coord, tileSize);
		int x = aprox[0];
		int y = aprox[1];
		x += offCol;
		y += offRow;
		return new Point(x, y);
	}

	/**
	 * Look for differents values of the adjacents values, if different, is
	 * border.
	 * 
	 * @return true is border, false if not
	 */
	public boolean isBorderPoint(Point p) {
		for (int[] a : getAdjacents(p.getCol(), p.getRow())) {
			if (p.getZ() != getValue(a[0], a[1])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Call a webservice to obtain the elevation of all tiles of the grid
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void obtainTerrainElevation(boolean random)
			throws IllegalStateException, IOException {
		if (precision <= 0)
			throw new IllegalStateException(
					"Precision hasn't been defined yet.");

		int endX = offCol + columns;
		int endY = offRow + rows;

		if (!random) {
			File tmp = TempFiles.getDefaultTempDir();
			String fname = NW.getLat() + "-" + NW.getLng() + "-" + SE.getLat()
					+ "-" + SE.getLng() + "-" + tileSize;
			File f = new File(tmp, fname);
			boolean empty = false;
			try {
				empty = f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (empty) {
				BufferedWriter bw = null;
				try {
					// Descargamos la altura y la escribimos a disco para
					// futuras simulaciones
					bw = new BufferedWriter(new FileWriter(f));
					int total = (rows + 2) * (columns + 2);
					int cont = 0;
					for (int i = offCol - 1; i <= endX; i++) {
						for (int j = offRow - 1; j <= endY; j++) {
							LatLng coord = tileToCoord(i, j);
							double value = AltitudeWS.getElevation(coord);
							short alt = Scenario
									.doubleToInner(precision, value);
							setTerrainValue(i, j, alt);
							bw.write(i + "," + j + "=" + alt + "\n");
							cont++;
							System.out.println("Obtenidas " + cont + " de "
									+ total + " alturas\r");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (bw != null)
						bw.close();
				}
			} else {
				// Si ya estaban los datos en el disco los leemos
				BufferedReader br = null;
				String[] data;
				String[] coord;
				String line;
				try {
					br = new BufferedReader(new FileReader(f));
					while ((line = br.readLine()) != null) {
						data = line.split("=");
						coord = data[0].split(",");
						setTerrainValue(Integer.parseInt(coord[0]), Integer
								.parseInt(coord[1]), Short.parseShort(data[1]));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (br != null)
						br.close();
				}
			}
		} else {
			// Alturas aleatorias
			Random rnd = new Random(System.currentTimeMillis());
			for (int i = offCol - 1; i <= endX; i++) {
				for (int j = offRow - 1; j <= endY; j++) {
					setTerrainValue(i, j, (short) rnd.nextInt(200));
				}
			}
		}
	}

	public void obtainStreetInfo() {
		Osm.setOsmMapInfo(this);
	}

	/**
	 * Returns degree increments lat, long
	 * 
	 * @return
	 */
	public double[] getIncs() {
		return new double[] { ilat, ilng };
	}

	@Override
	public String toString() {
		String s = "Box: " + NW.toString() + ", " + SE.toString()
				+ ", Diagonal: " + (int) NW.distance(SE) + " m ";
		s += "\nDimensions: " + columns + "x" + rows + ", Width: "
				+ (int) NW.distance(new LatLng(NW.getLat(), SE.getLng()))
				+ " m, Height: "
				+ (int) NW.distance(new LatLng(SE.getLat(), NW.getLng()))
				+ " m";
		s += "\nTile size: " + tileSize + "m";
		return s;
	}

	public void setPrecision(short precision) {
		this.precision = precision;
	}

	// STATIC DATA AND METHODS

	// Relative positions from point
	public static final int LEFT = 0;
	public static final int LEFT_UP = 1;
	public static final int RIGHT_UP = 2;
	public static final int RIGHT = 3;
	public static final int RIGHT_DOWN = 4;
	public static final int LEFT_DOWN = 5;

	/**
	 * Returns the adjacent hexagon to a that is in b direction
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Point nearestHexagon(Point a, Point b) {
		if (a.equals(b))
			return a;
		int key = whichHexagonalMove(a, b);
		// movimiento(key);
		return hexagonalMoveTo(a, key);
	}

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

	public static int distance(Point p1, Point p2) {
		return distance(p1.getCol(), p1.getRow(), p2.getCol(), p2.getRow());
	}
}
