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

package behaviours.people;

import jade.core.AID;
import jade.core.Agent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import osm.Osm;

import util.HexagonalGrid;
import util.Point;
import util.java.NoDuplicatePointsSet;
import util.jcoord.LatLng;

@SuppressWarnings("serial")
public class KnownSafepointPedestrianBehav extends PedestrianBehav {

	private Set<Point> objectives = new HashSet<Point>(0);

	public KnownSafepointPedestrianBehav(Agent a, long period, AID env, double lat,
			double lng, int d, int s) {
		super(a, period, env, lat, lng, d, s);
	}

	@Override
	protected Point choose(Set<Point> adjacents) throws YouAreDeadException,
			YouAreSafeException {
		Point result = null;

		if (position != null) {
			adjacents = PedestrianUtils.filterByStreetView(adjacents, position);
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

		if (PedestrianUtils.detectFloodDeath(dry, position))
			throw new YouAreDeadException("Surrounded by water :(");

		// Primera ejecución
		if (position == null)
			return PedestrianUtils.nearInSetToSet(dry, objectives);

		if (objectives.size() > 0 || safe.size() > 0) {
			if (safe.size() < 0) {
				// No hay ningún refugio a la vista
				int best = Integer.MIN_VALUE;

				// Puntuamos cada punto
				for (Point pt : dry) {
					Point nearObj = null;
					int objective = Integer.MAX_VALUE;
					// Buscamos el objetivo más cercano y la distancia al mismo
					for (Point obj : objectives) {
						int dist = HexagonalGrid.distance(pt, obj);
						if (dist < objective) {
							objective = dist;
							nearObj = obj;
						}
					}

					int wasser = 0;
					// Distancia a las casillas inundadas
					for (Point wpt : water) {
						wasser += HexagonalGrid.distance(pt, wpt);
					}

					int score = score(objective, wasser, (int) pt.getZ());

					if (score > best) {
						best = score;
						result = nearObj;
					}
				}

				result = PedestrianUtils
						.accessible(adjacents, position, result, s);
			} else {
				// Hay refugio a la vista
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

				// Buscamos el refugio más cercano que esté accesible
				for (Point pt : sortedSafe) {
					result = PedestrianUtils
							.accessible(adjacents, position, pt, s);
					if (result != null)
						break;
				}
			}
		}

		if (result == null) {
			// En el caso en que no sepa donde hay refugios o no sea posible
			// acceder a ninguno
			// TODO result = fallbackRank.choose(adjacents, position, vision, speed);
		}

		return result;
	}

	private int score(int objective, int water, int elevation) {
		int score = 0;
		return score;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void chooseArgs(Object[] args) {
		Set<LatLng> geoObj = (Set<LatLng>) args[0];
		// TODO pasar a point
	}

}
