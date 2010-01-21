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

import java.io.Serializable;

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	protected boolean complete;
	// Coordinates of simulation area (rectangle)
	// NW means North West point
	// SE means South East point
	protected double NWlat;
	protected double NWlong;
	protected double SElat;
	protected double SElong;

	// Current Scenario showed on GUI
	// If a Scenario is loaded (from a file), or a new one is created, this
	// reference MUST change
	protected static Scenario current = null;

	public Scenario() {
		complete = false;
		current = this;
	}

	public static Scenario getCurrentScenario() {
		if (current.isComplete())
			return current;
		else
			return null;
	}

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		complete = true;
	}
}
