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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import agents.people.PedestrianAgent;

import osm.Osm;
import util.HexagonalGrid;
import util.Point;

/**
 * Set of utilities for {@link PedestrianAgent} in the form of static methods.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class PedestrianUtils {

	/**
	 * North direction
	 */
	public static final int NORTH = 0;
	/**
	 * South direction
	 */
	public static final int SOUTH = 1;
	/**
	 * East direction
	 */
	public static final int EAST = 2;
	/**
	 * West direction
	 */
	public static final int WEST = 3;
	private static final int NUM_DIR = 4;

	/**
	 * Private constructor. No instances of this class.
	 */
	private PedestrianUtils() {
		// No instanciable
	}

	/**
	 * Returns a Set with the tiles in adjacents that can be viewed from
	 * position, through streets and safepoints
	 * 
	 * @param adjacents
	 *            {@link Set}<{@link Point}>
	 * @param position
	 *            {@link Point}
	 * @return The tiles in adjacents that can be viewed from position, through
	 *         streets and safepoints
	 */
	public static Set<Point> filterByStreetView(Set<Point> adjacents,
			Point position) {
		// Sólo las casillas que puede ver desde su calle
		Set<Point> aux = new HashSet<Point>(adjacents.size() + 1);
		for (Point pt : adjacents) {
			Point auxpt = position;
			// Buscamos que llegue en línea a través de una calle
			while (!pt.equals(auxpt)) {
				auxpt = findHexagon(adjacents, HexagonalGrid.nearestHexagon(
						auxpt, pt));
				if (Osm.getGenericType(auxpt.getS()) != Osm.Roads
						&& Osm.getGenericType(auxpt.getS()) != Osm.SafePoint) {
					auxpt = null;
					break;
				}
			}
			// Si ha podido llegar nos quedamos el punto
			if (pt.equals(auxpt))
				aux.add(pt);
		}
		return aux;
	}

	/**
	 * Find the point in adjacents that has the same position that pt
	 * 
	 * @param adjacents
	 *            {@link Set}<{@link Point}>
	 * @param pt
	 *            {@link Point}
	 * @return The point in adjacents that has the same position that pt
	 */
	public static Point findHexagon(Set<Point> adjacents, Point pt) {
		for (Point apt : adjacents) {
			if (pt.equals(apt)) {
				pt = apt;
				break;
			}
		}
		return pt;
	}

	/**
	 * Returns the point from adjacents that is in destination direction
	 * considering speed if there aren't obstacles, returns null in other case
	 * 
	 * @param adjacents
	 *            {@link Set}<{@link Point}>
	 * @param position
	 *            {@link Point}
	 * @param destination
	 *            {@link Point}
	 * @param speed
	 *            in tiles
	 * @return The point from adjacents that is in destination direction
	 *         considering speed if there aren't obstacles, returns null in
	 *         other case
	 */
	public static LinkedList<Point> accessible(Set<Point> adjacents,
			Point position, Point destination, int speed) {
		LinkedList<Point> result = new LinkedList<Point>();
		Point aux = position;
		int cont = 0;
		// Recorremos hexágonos hasta llegar a él
		while (!destination.equals(aux)) {
			cont++;
			aux = findHexagon(adjacents, HexagonalGrid.nearestHexagon(aux,
					destination));
			// Mientras la velocidad lo permita vamos avanzando hexágonos
			if (cont <= speed)
				result.add(aux);
			// Miramos que no nos encontremos con obstáculos en el camino
			if (!destination.equals(aux)) {
				if (aux.getW() > 0
						|| Osm.getGenericType(aux.getS()) != Osm.Roads) {
					result = new LinkedList<Point>();
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns the point from in that is farthest from the point from
	 * 
	 * @param in
	 *            {@link Set}<{@link Point}>
	 * @param from
	 *            {@link Point}
	 * @return The point from in that is farthest from the point from
	 */
	public static Point farInSetFromPoint(Set<Point> in, Point from) {
		Point result = null;
		int max = Integer.MIN_VALUE;
		for (Point pt : in) {
			int dist = HexagonalGrid.distance(from, pt);
			if (dist > max) {
				result = pt;
				max = dist;
			}
		}
		return result;
	}

	/**
	 * Returns the point from in that is farthest from the points in from
	 * 
	 * @param in
	 *            {@link Set}<{@link Point}>
	 * @param from
	 *            {@link Set}<{@link Point}>
	 * @return The point from in that is farthest from the points in from
	 */
	public static Point farInSetFromSet(Set<Point> in, Set<Point> from) {
		if (from.size() == 0)
			return randomFromSet(new Random(System.currentTimeMillis()), in);

		Point result = null;
		int max = Integer.MIN_VALUE;
		for (Point inpt : in) {
			int dist = 0;
			for (Point frpt : from) {
				dist += HexagonalGrid.distance(inpt, frpt);
			}
			if (from.size() != 0)
				dist /= from.size();
			if (dist > max) {
				result = inpt;
				max = dist;
			}
		}
		return result;
	}

	/**
	 * Returns the point from in that is the nearest to the point to
	 * 
	 * @param in
	 *            {@link Set}<{@link Point}>
	 * @param to
	 *            {@link Point}
	 * @return The point from in that is the nearest to the point to
	 */
	public static Point nearInSetToPoint(Set<Point> in, Point to) {
		Point result = null;
		int min = Integer.MAX_VALUE;
		for (Point pt : in) {
			int dist = HexagonalGrid.distance(to, pt);
			if (dist < min) {
				result = pt;
				min = dist;
			}
		}
		return result;
	}

	/**
	 * Returns the point from in that is the nearest to the points from to
	 * 
	 * @param in
	 *            {@link Set}<{@link Point}>
	 * @param to
	 *            {@link Set}<{@link Point}>
	 * @return The point from in that is the nearest to the points from to
	 */
	public static Point nearInSetToSet(Set<Point> in, Set<Point> to) {
		if (to.size() == 0)
			return randomFromSet(new Random(System.currentTimeMillis()), in);

		Point result = null;
		int min = Integer.MAX_VALUE;
		for (Point inpt : in) {
			int dist = 0;
			for (Point frpt : to) {
				dist += HexagonalGrid.distance(inpt, frpt);
			}
			if (to.size() != 0)
				dist /= to.size();
			if (dist < min) {
				result = inpt;
				min = dist;
			}
		}
		return result;
	}

	/**
	 * Returns true if the position is flooded and surrounded by water
	 * 
	 * @param dry
	 *            {@link Set}<{@link Point}>
	 * @param position
	 *            {@link Point}
	 * @return True if the position is flooded and surrounded by water
	 */
	public static boolean detectFloodDeath(Set<Point> dry, Point position) {
		// Si no tiene casillas secas a su alrededor está rodeado y
		// se ahoga
		Set<Point> freeAdjc = new HashSet<Point>(7);
		if (position != null) {
			// Sólo las adyacentes a distancia 1
			for (Point pt : dry) {
				if (HexagonalGrid.distance(position, pt) <= 1)
					freeAdjc.add(pt);
			}
		} else {
			freeAdjc = dry;
		}

		if (freeAdjc.size() == 0)
			return true;

		return false;
	}

	/**
	 * Returns a random point from the set
	 * 
	 * @param rnd
	 *            {@link Random}
	 * @param points
	 *            {@link Set}<{@link Point}>
	 * @return A random point from the set
	 */
	public static Point randomFromSet(Random rnd, Set<Point> points) {
		int aux = rnd.nextInt(points.size());
		Iterator<Point> it = points.iterator();
		Point p = it.next();
		while (aux > 0) {
			p = it.next();
			aux--;
		}
		return p;
	}

	/**
	 * Returns the direction to follow from position to destination
	 * 
	 * @param position
	 *            {@link Point}
	 * @param destination
	 *            {@link Point}
	 * @return The direction to follow from position to destination
	 */
	public static int getDirection(Point position, Point destination) {
		if (destination == null)
			return randomDirection(new Random(System.currentTimeMillis()));

		int dir = -1;
		int difCol = position.getCol() - destination.getCol();
		int difRow = position.getRow() - destination.getRow();
		if (Math.abs(difCol) > Math.abs(difRow)) {
			if (difCol > 0)
				dir = WEST;
			else
				dir = EAST;
		} else {
			if (difRow > 0)
				dir = NORTH;
			else
				dir = SOUTH;
		}
		return dir;
	}

	/**
	 * Returns the point of the set that is farthest in the given direction
	 * 
	 * @param points
	 *            {@link Set}<{@link Point}>
	 * @param direction
	 * @return The point of the set that is farthest in the given direction
	 */
	public static Point getPointByDirection(Set<Point> points, int direction) {
		Point result = null;
		int value = -10; // Kinda ugly :P
		for (Point pt : points) {
			switch (direction) {
			case NORTH:
				if (pt.getRow() < value || value == -10) {
					value = pt.getRow();
					result = pt;
				}
				break;
			case SOUTH:
				if (pt.getRow() > value || value == -10) {
					value = pt.getRow();
					result = pt;
				}
				break;
			case WEST:
				if (pt.getCol() < value || value == -10) {
					value = pt.getCol();
					result = pt;
				}
				break;
			case EAST:
				if (pt.getCol() > value || value == -10) {
					value = pt.getCol();
					result = pt;
				}
				break;
			}
		}
		return result;
	}

	/**
	 * Returns a random direction
	 * 
	 * @param rnd
	 *            {@link Random}
	 * @return A random direction
	 */
	public static int randomDirection(Random rnd) {
		return rnd.nextInt(NUM_DIR);
	}

	/**
	 * Returns a random direction excluding the opposite one
	 * 
	 * @param rnd
	 *            {@link Random}
	 * @param direction
	 * @return A random direction excluding the opposite one
	 */
	public static int randomDirection(Random rnd, int direction) {
		int oppo = oppositeDirection(direction);
		int result = rnd.nextInt(NUM_DIR - 1);
		if (result == oppo)
			result = NUM_DIR - 1;
		return result;
	}

	/**
	 * Returns the opposite direction to the given one
	 * 
	 * @param direction
	 * @return The opposite direction to the given one
	 */
	public static int oppositeDirection(int direction) {
		switch (direction) {
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		case EAST:
			return WEST;
		default:
			return randomDirection(new Random(System.currentTimeMillis()));
		}
	}

}
