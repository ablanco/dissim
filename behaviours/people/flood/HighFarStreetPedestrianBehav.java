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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import osm.Osm;
import util.HexagonalGrid;
import util.Point;
import util.Scenario;
import behaviours.people.PedestrianBehav;
import behaviours.people.PedestrianUtils;
import behaviours.people.YouAreDeadException;
import behaviours.people.YouAreSafeException;

@SuppressWarnings("serial")
public class HighFarStreetPedestrianBehav extends PedestrianBehav {

	private Hashtable<String, Integer> scores;

	public HighFarStreetPedestrianBehav(Agent a, long period, AID env,
			Scenario scen, double lat, double lng, int d, int s) {
		super(a, period, env, scen, lat, lng, d, s);
	}

	@Override
	protected Point choose(Set<Point> adjacents) throws YouAreDeadException,
			YouAreSafeException {
		if (position != null) {
			adjacents = PedestrianUtils.filterByStreetView(adjacents, position);
			adjacents.add(position);
		}

		// Separar casillas inundadas de las secas
		Set<Point> water = new HashSet<Point>(adjacents.size());
		Set<Point> dry = new HashSet<Point>(adjacents.size());
		for (Point pt : adjacents) {
			if (pt.getW() > 0)
				water.add(pt);
			else if (Osm.getBigType(pt.getS()) == Osm.Roads)
				dry.add(pt);
			// Las casillas que no son calles se ignoran
		}

		if (PedestrianUtils.detectFloodDeath(dry, position))
			throw new YouAreDeadException("Surrounded by water :(");

		scores = new Hashtable<String, Integer>(dry.size());

		LinkedList<Point> high = new LinkedList<Point>();
		ListIterator<Point> it;
		for (Point pt : dry) {
			// Ordenamos las secas por altura
			if (high.size() == 0) {
				high.add(pt);
			} else {
				it = high.listIterator();
				while (it.hasNext()) {
					Point hpt = it.next();
					if (hpt.getZ() > pt.getZ()) {
						it.previous();
						it.add(pt);
						break;
					} else if (!it.hasNext()) {
						// Está más alto que todos los de la lista hasta ese
						// momento
						it.add(pt);
					}
				}
			}
		}
		int points = 0;
		it = high.listIterator();
		while (it.hasNext()) {
			// Puntuamos según la altura
			Point pt1 = it.next();
			score(pt1.toString(), points);
			if (it.hasNext()) {
				Point pt2 = it.next();
				if (pt2.getZ() > pt1.getZ())
					points++;
				it.previous();
			}
		}

		// Equilibramos ambas escalas de puntos
		double factor = dry.size() / (d * 2);
		factor *= 2; // Damos preferencia a huir del agua
		// Puntuamos según la distancia media al agua
		if (water.size() > 0) {
			for (Point pt : dry) {
				int dist = 0;
				for (Point w : water) {
					dist += HexagonalGrid.distance(pt, w);
				}
				dist /= water.size();
				score(pt.toString(), (int) (dist * factor));
			}
		}

		return getBest(adjacents, dry, position, s);
	}

	private void score(String key, int points) {
		Integer prev = scores.get(key);
		if (prev != null)
			points += prev.intValue();
		scores.put(key, new Integer(points));
	}

	private Point getBest(Set<Point> adjacents, Set<Point> dry, Point position,
			int speed) {
		Point result = null;
		int max = Integer.MIN_VALUE;

		for (Point pt : dry) {
			boolean ok = true;
			Point aux = position;
			// Comprobamos si el hexágono es apto
			if (position != null) {
				// Sólo avanza lo que le permita su velocidad
				for (int i = 0; i < speed; i++) {
					// Se puede llegar a él?
					aux = HexagonalGrid.nearestHexagon(aux, pt);
					aux = PedestrianUtils.findHexagon(adjacents, aux);
					// No entra en casillas con agua y sólo se mueve por calles
					if (aux.getW() > 0
							|| Osm.getBigType(aux.getS()) != Osm.Roads) {
						ok = false;
						break;
					}
				}
			} else {
				aux = pt;
			}

			if (ok) {
				// Si es apto miramos su puntuación
				int scr = scores.get(pt.toString()).intValue();
				if (scr > max) {
					max = scr;
					result = aux;
				}
			}
		}

		return result;
	}

	@Override
	public void chooseArgs(Object[] args) {
	}

}
