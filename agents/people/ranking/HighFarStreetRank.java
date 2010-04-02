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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import osm.OsmInf;
import util.HexagonalGrid;
import util.NoDuplicatePointsSet;
import util.Point;

public class HighFarStreetRank implements Ranking {

	private Hashtable<String, Integer> scores;

	@Override
	public Point choose(Set<Point> adjacents, Point position, int vision,
			int speed) throws YouAreDeadException {
		if (position != null)
			adjacents.add(position);

		// Separar casillas inundadas de las secas
		Set<Point> water = new NoDuplicatePointsSet(adjacents.size());
		Set<Point> dry = new NoDuplicatePointsSet(adjacents.size());
		for (Point pt : adjacents) {
			if (pt.getW() > 0)
				water.add(pt);
			else if (OsmInf.getBigType(pt.getS()) == OsmInf.Roads)
				dry.add(pt);
			// Las casillas que no son calles se ignoran
		}

		// Si no tiene casillas secas a su alrededor está rodeado y
		// se ahoga
		Set<Point> freeAdjc = new NoDuplicatePointsSet(6);
		if (position != null) {
			// Sólo las adyacentes a distancia 1
			for (Point pt : dry) {
				if (HexagonalGrid.distance(position, pt) == 1)
					freeAdjc.add(pt);
			}
		} else {
			freeAdjc = dry;
		}
		if (freeAdjc.size() == 0)
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

		double factor = dry.size() / (vision * 2);
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

		return getBest(dry);
	}

	private void score(String key, int points) {
		Integer prev = scores.get(key);
		if (prev != null)
			points += prev.intValue();
		scores.put(key, new Integer(points));
	}

	private Point getBest(Set<Point> dry) {
		Point result = null;
		int max = Integer.MIN_VALUE;
		for (Point pt : dry) {
			// Sólo se mueve por calles
			int scr = scores.get(pt.toString()).intValue();
			if (scr > max) {
				max = scr;
				result = pt;
			}
		}
		return result;
	}

}
