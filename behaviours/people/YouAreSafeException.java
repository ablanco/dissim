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

import util.Point;

/**
 * {@link Exception} that represents that the {@link PedestrianBehav} has
 * reached a safepoint.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class YouAreSafeException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Safepoint reached
	 */
	private Point position;

	/**
	 * {@link YouAreSafeException} constructor
	 * 
	 * @param position
	 *            {@link Point} Safepoint reached
	 */
	public YouAreSafeException(Point position) {
		super();
		this.position = position;
	}

	/**
	 * {@link YouAreSafeException} constructor
	 * 
	 * @param position
	 *            {@link Point} Safepoint reached
	 * @param msg
	 *            {@link String} Message of the {@link Exception}
	 */
	public YouAreSafeException(Point position, String msg) {
		super(msg);
		this.position = position;
	}

	/**
	 * Returns the safepoint reached
	 * 
	 * @return {@link Point}
	 */
	public Point getPosition() {
		return position;
	}

}
