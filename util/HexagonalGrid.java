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
	private double[][] gridTerrain;
	private double[][] gridWater;
	private int dimX;
	private int dimY;

	public HexagonalGrid(int x, int y) {
		gridTerrain = new double[x][y];
		gridWater = new double[x][y];
		dimX = x;
		dimY = y;
	}

	public double setTerrainValue(int x, int y, double value) {
		double old = gridTerrain[x][y];
		gridTerrain[x][y] = value;
		return old;
	}

	public double setWaterValue(int x, int y, double value) {
		double old = gridWater[x][y];
		gridWater[x][y] = value;
		return old;
	}

	public void increaseValue(int x, int y, double increment) {
		gridWater[x][y] += increment;
		printGrid(); // TODO Debug
	}

	public void decreaseValue(int x, int y, double decrement) {
		gridWater[x][y] -= decrement;
	}

	public double getValue(int x, int y) {
		return gridTerrain[x][y] + gridWater[x][y];
	}

	public double getTerrainValue(int x, int y) {
		return gridTerrain[x][y];
	}

	public double getWaterValue(int x, int y) {
		return gridWater[x][y];
	}

	/**
	 * Devuelve los hexágonos adyacentes al pedido (6 como máximo)
	 * 
	 * @param x
	 * @param y
	 * @return Una lista de arrays, cada array representa a un hexágono
	 *         adyacente y sus elementos son: columna, fila y valor.
	 */
	public ArrayList<double[]> getAdjacents(int x, int y) {
		ArrayList<double[]> result = new ArrayList<double[]>(6);
		double[] adjacent;
		for (int fila = y - 1; fila <= y + 1; fila++) {
			for (int col = x; col <= x + 1; col++) {
				if (fila == y && col == x)
					col = x - 1;
				// Comprobamos que el hexágono adyacente no está fuera de la
				// rejilla
				if (col >= 0 && col < dimX && fila >= 0 && fila < dimY) {
					adjacent = new double[3];
					adjacent[0] = col;
					adjacent[1] = fila;
					adjacent[2] = getValue(col, fila);
					result.add(adjacent);
				}
				if (fila == y && col == x - 1)
					col++;
			}
		}
		return result;
	}

	private void printGrid() {
		for (int i = 0; i < dimX; i++) {
			if (i % 2 != 0) {
				System.out.print("   ");
			}
			for (int j = 0; j < dimY; j++) {
				System.out.print(getValue(i, j) + "  ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}
