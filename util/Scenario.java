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

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	protected boolean complete;
	// Coordinates of simulation area (rectangle)
	// NW means North West point
	// SE means South East point
	protected double NWlat;
	protected double NWlong;
	protected double SElat;
	protected double SElong;
	protected int gridX;
	protected int gridY;
	protected String description;

	// Current Scenario showed on GUI
	// If a Scenario is loaded (from a file), or a new one is created, this
	// reference MUST change
	protected static Scenario current = null;

	// This class shouldn't be used directly
	protected Scenario() {
		complete = false;
		current = this;
	}

	public static Scenario getCurrentScenario() {
		Scenario instance = null;
		if (current != null) {
			if (current.isComplete())
				instance = current;
		}
		return instance;
	}

	public void setArea(double NWlat, double NWlong, double SElat, double SElong) {
		this.NWlat = NWlat;
		this.NWlong = NWlong;
		this.SElat = SElat;
		this.SElong = SElong;
	}

	public double[] getArea() {
		return new double[] { NWlat, NWlong, SElat, SElong };
	}

	public void setGridSize(int x, int y) {
		gridX = x;
		gridY = y;
	}

	public int[] getGridSize() {
		return new int[] { gridX, gridY };
	}

	public int[] coordToTile(double latitude, double longitude) {
		// TODO coord to tile
		
		return new int[] { 0, 0 };
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		complete = true;
	}
}
