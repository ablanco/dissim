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
import java.util.HashSet;
import java.util.Set;

import util.jcoord.LatLng;
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
	private String name = "";
	// Diameter of the circunflex circle of the hexagon in meters
	private short tileSize = 1;
	// Increment in degrees (depends on tileSize)
	// 1 unit means 1/precision meters
	private short precision = 10;
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

		// Obtain the opposite of the square distance in km
		int y = (int) (NW.distance(new LatLng(NW.getLat(), SE.getLng())) * 1000 / tileSize) / 2;
		int x = (int) (NW.distance(new LatLng(SE.getLat(), NW.getLng())) * 1000 / tileSize) / 2;

		// Set grid size
		createGrid(x + 1, y + 1);
		// Set offset in degrees between two hexagramas
		// System.out.println(NW.toString()+SE.toString()+", x:"+x+", y:"+y+"lat inc ="+latInc+", long inc ="+lngInc);
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
	 * Convert form the grid to hexagram coordinate
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public LatLng tileToCoord(int x, int y) {
		if (NW == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");
		double lng;
		double lat = (tileSize * x *3/4);

		if (x % 2 != 0) {
			// odd Rows has offset.
			lng = ((tileSize / 2) + (tileSize * y));
		} else {
			lng = (tileSize * y);
		}

		return NW.metersToDegrees(lat, lng, grid.getTerrainValue(x, y));
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
		// Aproximacion
		int y = (int) (NW.distance(new LatLng(NW.getLat(), coord.getLng())) * 1000 / tileSize);
		int x = (int) (NW.distance(new LatLng(coord.getLat(), NW.getLng())) * 1000 / tileSize);
		// Try to adjust aproximation errors. 7%

		double minDist = coord.distance(tileToCoord(x, y));
		for (int[] tile : grid.getAdjacents(x, y)) {
			double dist = coord.distance(tileToCoord(tile[0], tile[1]));
			if (dist <= minDist) {
				minDist = dist;
				x = tile[0];
				y = tile[1];
				for (int[] t : grid.getAdjacents(tile[0], tile[1])) {
					dist = coord.distance(tileToCoord(t[0], t[1]));
					if (dist < minDist) {
						minDist = dist;
						x = t[0];
						y = t[1];
					}
				}
			}
		}

		return new int[] { x, y };
	}

	public LatLng coordToTileCentrum(LatLng coord) {
		int c[] = coordToTile(coord);
		return tileToCoord(c[0], c[1]);
	}

	/**
	 * Returns a list of adjacens from coords
	 * 
	 * @param c
	 *            <LatLng>
	 * @return ArrayList<LatLng>
	 */
	public Set<LatLng> getAdjacents(LatLng c) {
		HashSet<LatLng> coordsAdjacents = new HashSet<LatLng>();
		int ind[] = coordToTile(c);
		for (int[] adjacent : grid.getAdjacents(ind[0], ind[1])) {
			coordsAdjacents.add(tileToCoord(adjacent[0], adjacent[1]));
		}
		return coordsAdjacents;
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
					+ " Tile size :" + NW.distance(tileToCoord(0, 1)) + "kms"
					+ ", Diagonal =" + NW.distance(SE) + "kms";
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
		this.precision = precision;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
