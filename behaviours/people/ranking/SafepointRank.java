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

package behaviours.people.ranking;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import osm.Osm;
import util.HexagonalGrid;
import util.NoDuplicatePointsSet;
import util.Point;

public class SafepointRank implements Ranking {

	private Random rnd = new Random(System.currentTimeMillis());
	private int direction = RankingUtils.randomDirection(rnd);

	@Override
	public Point choose(Set<Point> adjacents, Point position, int vision,
			int speed) throws YouAreDeadException, YouAreSafeException {

		if (position != null) {
			adjacents = RankingUtils.filterByStreetView(adjacents, position);
			adjacents.add(position);

			// Si ya está en un refugio no se mueve ni muere ni nada
			if (Osm.getBigType(position.getS()) == Osm.SafePoint)
				throw new YouAreSafeException(position);
		}

		// Separar casillas inundadas de las secas, y buscar refugios
		Set<Point> water = new NoDuplicatePointsSet(adjacents.size());
		Set<Point> dry = new NoDuplicatePointsSet(adjacents.size());
		Set<Point> safe = new NoDuplicatePointsSet(adjacents.size());
		for (Point pt : adjacents) {
			if (pt.getW() > 0)
				water.add(pt);
			else if (Osm.getBigType(pt.getS()) == Osm.SafePoint)
				safe.add(pt);
			else if (Osm.getBigType(pt.getS()) == Osm.Roads)
				dry.add(pt);
			// Las casillas que no son calles ni refugios se ignoran
		}

		if (RankingUtils.detectFloodDeath(dry, position))
			throw new YouAreDeadException("Surrounded by water :(");

		if (position == null) {
			return RankingUtils.farInSetFromSet(dry, water);
		} else {
			if (safe.size() == 0) {
				// Caso en que no tiene refugio a la vista
				Point p = null;

				if (water.size() != 0) {
					p = RankingUtils.farInSetFromSet(dry, water);
					p = RankingUtils.accessible(adjacents, position, p, speed);
				}

				// Si no ha visto agua se mueve al azar, pero manteniendo una
				// dirección
				if (p == null) {
					int intentos = 3;

					// Si está en una intersección escoge una calle al azar
					if (RankingUtils.intersection(position, direction, dry))
						direction = RankingUtils
								.randomDirection(rnd, direction);

					while (intentos > 0 && p == null) {
						p = RankingUtils.getPointByDirection(dry, direction);
						p = RankingUtils.accessible(adjacents, position, p,
								speed);
						// Si no puede avanzar en esa dirección cambia a otra al
						// azar
						if (p == null)
							direction = RankingUtils.randomDirection(rnd);
						intentos--;
					}
				}

				direction = RankingUtils.getDirection(position, p);
				return p;
			} else {
				// Caso en que ve uno o más refugios

				LinkedList<Point> sortedSafe = new LinkedList<Point>();
				for (Point pt : safe) {
					// Ordenamos los refugios por distancia
					if (sortedSafe.size() == 0) {
						sortedSafe.add(pt);
					} else {
						ListIterator<Point> it = sortedSafe.listIterator();
						int d = HexagonalGrid.distance(position, pt);
						while (it.hasNext()) {
							Point spt = it.next();
							if (HexagonalGrid.distance(position, spt) > d) {
								it.previous();
								it.add(pt);
								break;
							} else if (!it.hasNext()) {
								// Está más lejos que todos los de la lista
								// hasta ese momento
								it.add(pt);
							}
						}
					}
				}

				Point result = null;
				// Buscamos el refugio más cercano que esté accesible
				for (Point pt : sortedSafe) {
					result = RankingUtils.accessible(adjacents, position, pt,
							speed);
					if (result != null)
						break;
				}
				return result;
			}
		}
	}
}
