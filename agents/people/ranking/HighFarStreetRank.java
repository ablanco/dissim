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
import java.util.TreeSet;

import osm.OsmInf;
import util.Point;

public class HighFarStreetRank implements Ranking {

	private Hashtable<String, Integer> scores;

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

		// Si no tiene casillas secas a su alrededor está rodeado y
		// se ahoga
		// TODO Mejorar detección de muerte
		if (dry.size() == 0)
			throw new YouAreDeadException("Surrounded by water :(");

		scores = new Hashtable<String, Integer>(dry.size());

		LinkedList<Point> high = new LinkedList<Point>();
		for (Point pt : dry) {
			// Ordenamos las secas por altura
			if (high.size() == 0) {
				high.add(pt);
			} else {
				ListIterator<Point> it = high.listIterator();
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
		ListIterator<Point> it = high.listIterator();
		while (it.hasNext()) {
			// Puntuamos según la altura
			Point pt1 = it.next();
			score(pt1.toString(), points);
			Point pt2 = it.next();
			if (pt2.getZ() > pt1.getZ())
				points++;
			it.previous();
		}

		// TODO puntuar según distancia al agua

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
			if (OsmInf.getBigType(pt.getS()) == OsmInf.Roads) {
				int scr = scores.get(pt.toString()).intValue();
				if (scr > max) {
					max = scr;
					result = pt;
				}
			}
		}
		return result;
	}

}
