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

package behaviours.people.flood;

import jade.core.AID;
import jade.core.Agent;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import behaviours.people.PedestrianBehav;
import behaviours.people.PedestrianUtils;
import behaviours.people.YouAreDeadException;
import behaviours.people.YouAreSafeException;

import util.HexagonalGrid;
import util.Point;
import util.Scenario;

@SuppressWarnings("serial")
public class FarFromWaterPedestrianBehav extends PedestrianBehav {

	public FarFromWaterPedestrianBehav(Agent a, long period, AID env,
			Scenario scen, double lat, double lng, int d, int s) {
		super(a, period, env, scen, lat, lng, d, s);
	}

	@Override
	protected Point choose(Set<Point> adjacents) throws YouAreDeadException,
			YouAreSafeException {
		if (position != null)
			adjacents.add(position);

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
		if (PedestrianUtils.detectFloodDeath(dry, position))
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

	@Override
	public void chooseArgs(Object[] args) {
	}

}
