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

import java.util.ArrayList;

public class HexagonalGrid {
	protected short[][] gridTerrain;
	protected int dimX;
	protected int dimY;

	public HexagonalGrid(int x, int y) {
		gridTerrain = new short[x][y];
		dimX = x;
		dimY = y;
	}

	public short setTerrainValue(int x, int y, short value) {
		short old = gridTerrain[x][y];
		gridTerrain[x][y] = value;
		return old;
	}

	public short increaseValue(int x, int y, short increment) {
		gridTerrain[x][y] += increment;
		return 0;
	}

	public short decreaseValue(int x, int y, short decrement) {
		gridTerrain[x][y] -= decrement;
		return decrement;
	}

	public short getValue(int x, int y) {
		return gridTerrain[x][y];
	}

	public short getTerrainValue(int x, int y) {
		return gridTerrain[x][y];
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
				if (col >= 0 && col < dimX && fila >= 0 && fila < dimY) {
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

	// DEBUG method
	protected void printGrid() {
		for (int i = 0; i < dimX; i++) {
			if (i % 2 != 0) {
				System.out.print("  ");
			}
			for (int j = 0; j < dimY; j++) {
				System.out.print(getValue(i, j) + "  ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}
