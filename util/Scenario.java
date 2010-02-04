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
import java.util.ArrayList;

import util.jcoord.LatLng;
import util.jcoord.UTMRef;
import webservices.AltitudeWS;

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete = false;
	// Coordinates of simulation area (rectangle)
	// NW means North West point
	protected LatLng NW = null;
	// SE means South East point
	protected LatLng SE = null;
	protected int gridX = -1;
	protected int gridY = -1;
	protected HexagonalGrid grid = null;
	private String description = "";
	// Diameter of the circle circunflex of the hexagon in meters
	protected short tileSize = 1;
	//Increment in degrees (depends on tileSize)
	
	protected double latInc; // 1 unit means 1/precision meters
	protected double lngInc;

	private short precision =100;
	// Current Scenario showed on GUI
	// If a Scenario is loaded (from a file), or a new one is created, this
	// reference MUST change
	protected static Scenario current = null;

	// This class shouldn't be used directly, that's why the constructor is
	// protected
	protected Scenario() {
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

	public void setGeoData(LatLng NW, LatLng SE, short tileSize) {
		this.NW = NW;
		this.SE = SE;
		this.tileSize = tileSize;

		// Obtain the opposite of the square
		LatLng NE = new LatLng(NW.getLat(), SE.getLng());
		LatLng SW = new LatLng(SE.getLat(), NW.getLng());
		// Obtain the distance from the square and fix with precision
		int x = (int) (NW.distance(NE) * 1000 / tileSize);
		int y = (int) (SE.distance(SW) * 1000 / tileSize);
		// Set grid size +1 because the dimension thing
		createGrid(x + 1, y + 1);
		// Set offset in degrees between two hexagramas
		latInc = Math.abs((Math.abs(tileToCoord(0, 0).getLat()) - Math.abs(tileToCoord(0, 1).getLat()))/2);
		lngInc = Math.abs((Math.abs(tileToCoord(0, 0).getLng()) - Math.abs(tileToCoord(2, 0).getLng()))/4);
	}

	public LatLng[] getArea() {
		return new LatLng[] { NW, SE };
	}

	protected void createGrid(int x, int y) {
		gridX = x;
		gridY = y;
		grid = new HexagonalGrid(x, y);
	}

	public int[] getGridSize() {
		return new int[] { gridX, gridY };
	}

	public HexagonalGrid getGrid() {
		return grid;
	}

	/**
	 * Convert from a coordinate to the position in the grid
	 * 
	 * @param coord
	 * @return
	 */
	public int[] coordToTile(LatLng coord) {
		if (tileSize < 0)
			throw new IllegalStateException(
					"The size of the tiles hasn't been defined yet.");

		// Obtain the opposite sides
		LatLng xCoord = new LatLng(NW.getLat(), coord.getLng());
		LatLng yCoord = new LatLng(coord.getLat(), NW.getLng());
		// Obtain the distances from the sides to NW(0,0)
		int x = (int) ((NW.distance(xCoord)) * 1000 / tileSize);
		int y = (int) ((NW.distance(yCoord)) * 1000 / tileSize);
		// Obtain adyacents and looks for closer.
		ArrayList<int[]> adyacents = grid.getAdjacents(x, y);
		double distance = coord.distance(tileToCoord(x, y));
		for (int[] a : adyacents) {
			if (distance > coord.distance(tileToCoord(a[0], a[1]))) {
				x = a[0];
				y = a[1];
			}
		}
		return new int[] { x, y };
	}

	public LatLng coordToTileCentrum(LatLng coord) {
		int c[] = coordToTile(coord);
		return tileToCoord(c[0], c[1]);
	}

	/**
	 * Convert form the grid to hexagram coordinate
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public LatLng tileToCoord(int x, int y) {
		if (NW == null || SE == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");
		// Convert to UTM, cause is in meters
		UTMRef coordUTM = NW.toUTMRef();
		UTMRef coordAUX;
		// Odd Rows has offset.
		if (x % 2 == 0) {
			// Just add the distance (x * tileSize) and get the new coordinate
			coordAUX = new UTMRef(coordUTM.getEasting() + (x * tileSize),
					coordUTM.getNorthing() + (y * tileSize), coordUTM
							.getLatZone(), coordUTM.getLngZone());
		} else {
			// Just add the distance (x * tileSize) and get the new coordinate
			// plus offset
			coordAUX = new UTMRef(coordUTM.getEasting() + (x * tileSize),
					coordUTM.getNorthing() + (y * tileSize) + tileSize / 2,
					coordUTM.getLatZone(), coordUTM.getLngZone());
		}
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
		if (NW == null || SE == null || grid == null || tileSize < 0)
			throw new IllegalStateException(
					"There are mandatory parameters that hasn't been defined yet.");

		complete = true;
	}

	public short getTileSize() {
		return tileSize;
	}

	@Override
	public String toString() {
		if (complete)
			return "\nSize [" + gridX + "," + gridY + "] NW coord :"
					+ NW.toString() + ", SE Coord: " + SE.toString()
					+ " Tile size :" + NW.distance(tileToCoord(0, 1)) + "kms";
		else
			return "Incomplete scenario description: " + super.toString();
	}

	public void obtainTerrainElevation() {
		if (grid == null)
			throw new IllegalStateException("The grid hasn't been created yet.");

		for (int i = 0; i < gridX; i++) {
			for (int j = 0; j < gridY; j++) {
				LatLng coord = tileToCoord(i, j);
				double value = AltitudeWS.getElevation(coord);
				grid.setTerrainValue(i, j, doubleToInner(value));
			}
		}
	}

	public void setPrecision(short precision) {
		this.precision  = precision;
	}

	public short getPrecision() {
		return precision;
	}

	public short doubleToInner(double d) {
		return (short) (d * precision);
	}

	public double innerToDouble(short s) {
		return ((double) s) / precision;
	}
	
	public double getLatInc(){
		return latInc;
	}
	public double getLngInc(){
		return lngInc;
	}
}
