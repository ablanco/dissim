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

package agents.people.ranking;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import util.HexagonalGrid;
import util.Point;

public class FarFromWaterRank implements Ranking {
	
	public FarFromWaterRank() {}

	@Override
	public Point choose(Set<Point> adjacents) throws YouAreDeadException {
		// Separar casillas inundadas de las secas
		Set<Point> water = new TreeSet<Point>();
		Set<Point> dry = new TreeSet<Point>();
		for (Point pt : adjacents) {
			if (pt.getW() > 0)
				water.add(pt);
			else
				dry.add(pt);
		}

		// Por si no ve agua, que no se mueva
		Point result = null;

		// Si no tiene casillas secas a su alrededor está rodeado y
		// se ahoga
		// TODO Mejorar detección de muerte
		if (dry.size() == 0)
			throw new YouAreDeadException("Surrounded by water :(");

		if (water.size() > 0) {
			// Buscar la casilla seca más alejada de las inundadas
			int dmejor = Integer.MIN_VALUE;
			for (Point pt : dry) {
				int dist = 0;
				for (Point w : water) {
					dist += HexagonalGrid.distance(pt, w);
				}
				dist /= water.size();
				if (dist > dmejor)
					result = pt;
			}

			// Si no se ha encontrado una mejor se mueve a una seca
			// al azar
			if (result != null) {
				Point[] arrdry = new Point[dry.size()];
				arrdry = dry.toArray(arrdry);
				Random rnd = new Random(System.currentTimeMillis());
				result = arrdry[rnd.nextInt(arrdry.length)];
			}
		}

		return result;
	}

}
