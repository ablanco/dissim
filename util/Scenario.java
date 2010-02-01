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

import util.jcoord.LatLng;
import util.jcoord.UTMRef;

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete;
	// Coordinates of simulation area (rectangle)
	// NW means North West point
	protected LatLng NW;
	// SE means South East point
	protected LatLng SE;
	private int gridX;
	private int gridY;
	private String description;

	// Current Scenario showed on GUI
	// If a Scenario is loaded (from a file), or a new one is created, this
	// reference MUST change
	protected static Scenario current = null;

	// Tile size in m^2
	private int tileSize;

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

	public void setArea(LatLng NW, LatLng SE) {
		this.NW = NW;
		this.SE = SE;
	}

	public LatLng[] getArea() {
		return new LatLng[] { NW, SE };
	}

	private void setGridSize(int x, int y) {
		gridX = x;
		gridY = y;
	}

	public int[] getGridSize() {
		return new int[] { gridX, gridY };
	}

	/**
	 * Convert from a coordinate to the position in the grid
	 * 
	 * @param coord
	 * @return
	 */
	public int[] coordToTile(LatLng coord) {
		// Obtain the opposite sides
		LatLng xCoord = new LatLng(NW.getLat(), coord.getLng());
		LatLng yCoord = new LatLng(coord.getLat(), NW.getLng());
		// Obtain the distances from the sides to NW(0,0)
		int x = (int) ((NW.distance(xCoord)) * 1000 / tileSize);
		int y = (int) ((NW.distance(yCoord)) * 1000 / tileSize);
		// DEBUG
		// System.out.println(coord.toString()+
		// "x("+NW.distance(xCoord)+"):"+x+", y("+NW.distance(yCoord)+"):"+y);

		return new int[] { x, y };
	}

	/**
	 * Convert form the grid to coordinate
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public LatLng tileToCoord(int x, int y) {
		// Convert to UTM, cause is in meters
		UTMRef coordUTM = NW.toUTMRef();
		// Just add the distance (x * tileSize) and get the new coordinate
		UTMRef coordAUX = new UTMRef(coordUTM.getEasting() + (x * tileSize),
				coordUTM.getNorthing() + (y * tileSize), coordUTM.getLatZone(),
				coordUTM.getLngZone());
		return coordAUX.toLatLng();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		complete = true;
	}

	/**
	 * Now we can set the precision of the grid
	 * 
	 * @param tileSize
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
		// Obtain the opposite of the square
		LatLng NE = new LatLng(NW.getLat(), SE.getLng());
		LatLng SW = new LatLng(SE.getLat(), NW.getLng());
		// Obtain the distance from the square and fix with precision
		int x = (int) (NW.distance(NE) * 1000 / tileSize);
		int y = (int) (SE.distance(SW) * 1000 / tileSize);
		// Set grid size +1 because the dimension thing
		setGridSize(x + 1, y + 1);
		// setDescription(tileSize + "," + NW.getLat() + "," + NW.getLng() + ","
		// + SE.getLat() + "," + SE.getLng());
	}

	public int getTileSize() {
		return tileSize;
	}

	public String toString() {
		return "\nSize [" + gridX + "," + gridY + "] NW coord :"
				+ NW.toString() + ", SE Coord: " + SE.toString()
				+ " Tile size :" + NW.distance(tileToCoord(0, 1)) + "kms";
	}
}
