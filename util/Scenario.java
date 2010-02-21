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
import java.util.Set;
import java.util.TreeSet;

import util.jcoord.LatLng;
import webservices.AltitudeWS;

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete = false;
	/**
	 * Coordinates of simulation area (rectangle) NW means North West point
	 */
	protected LatLng NW = null;
	/**
	 * SE means South East point
	 */
	protected LatLng SE = null;
	protected int gridX = -1;
	protected int gridY = -1;
	protected HexagonalGrid grid = null;
	private String description = "";
	private String name = "";
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private short tileSize = 1;
	/**
	 * Increment in degrees (depends on tileSize) 1 unit means 1/precision
	 * meters
	 */
	private short precision = 10;
	/**
	 * Current Scenario showed on GUI If a Scenario is loaded (from a file), or
	 * a new one is created, this reference MUST change
	 */
	protected static Scenario current = null;
	/**
	 *Current Date and Time of the Simulation
	 */
	protected DateAndTime currentDateAndTime = null;
	/**
	 * Time between two simulation Steps
	 */
	private int updateTimeMinutes = 1;
	/**
	 * Log manager for debungin
	 */
	private Logger defaultLogger = new Logger();

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

	/**
	 * Sets the Geolocation of the simulation, and the size of the tiles
	 * 
	 * @param NW
	 *            Upper left corner
	 * @param SE
	 *            Lower right corner
	 * @param tileSize
	 *            size of the tile terrain
	 */
	public void setGeoData(LatLng NW, LatLng SE, short tileSize) {
		this.NW = NW;
		this.SE = SE;
		this.tileSize = tileSize;

		// Obtain the opposite of the square distance in km
		int y = (int) (NW.distance(new LatLng(NW.getLat(), SE.getLng())) * 1000 / tileSize);
		int x = (int) (NW.distance(new LatLng(SE.getLat(), NW.getLng())) * 1000 / tileSize);

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
	 * Convert [x,y] to the corresponding LatLng Coordinate (with altitude)
	 * 
	 * @param x
	 *            lat
	 * @param y
	 *            lng
	 * @return LatLng
	 */
	public LatLng tileToCoord(int x, int y) {
		if (NW == null)
			throw new IllegalStateException(
					"Simulation area hasn't been defined yet.");
		double lng;
		double lat = (tileSize * x * 3 / 4);

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
		// TODO Esto no rula ni pa tras.
		if (tileSize < 0)
			throw new IllegalStateException(
					"The size of the tiles hasn't been defined yet.");
		// Aproximacion
		int x = (int) (NW.distance(new LatLng(coord.getLat(), NW.getLng())) * 1000 / tileSize);
		int y = (int) (NW.distance(new LatLng(NW.getLat(), coord.getLng())) * 1000 / tileSize);
		// Try to adjust aproximation errors. 7%

		return new int[] { x, y };
	}

	/**
	 * Returns a set of adjacens of pfrom coords
	 * 
	 * @param p
	 *            Point
	 * 
	 * @return Set<Point> adyacents to p
	 */
	public Set<Point> getAdjacents(Point p) {
		Set<Point> puntos = new TreeSet<Point>();
		for (int[] a : grid.getAdjacents(p.getX(), p.getY())) {
			puntos.add(new Point(a[0], a[1], grid.getTerrainValue(a[0], a[1])));
		}
		return puntos;
	}

	/**
	 * Look for diferents values of the adyacents values, if different, is
	 * border.
	 * 
	 * @return true is border, false if not
	 */
	public boolean isBorderPoint(Point p) {
		for (int[] a : grid.getAdjacents(p.getX(), p.getY())) {
			if (p.getZ() != grid.getTerrainValue(a[0], a[1])) {
				return true;
			}
		}
		return false;
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
			return "Current Time: " + currentDateAndTime.toString()
					+ "\nSize [" + gridX + "," + gridY + "] NW coord :"
					+ NW.toString() + ", SE Coord: " + SE.toString()
					+ "\nTile size :" + NW.distance(tileToCoord(0, 1)) + "kms"
					+ " ~ " + tileSize + "m, Diagonal =" + NW.distance(SE)
					+ "kms";
		else
			return "Incomplete scenario description: " + super.toString();
	}

	/**
	 * Call a webservice to obtain the elevation of all tiles of the grid
	 */
	public void obtainTerrainElevation() {
		if (grid == null)
			throw new IllegalStateException("The grid hasn't been created yet.");

		int total = gridX * gridY;
		int cont = 0;
		for (int i = 0; i < gridX; i++) {
			for (int j = 0; j < gridY; j++) {
				LatLng coord = tileToCoord(i, j);
				double value = AltitudeWS.getElevation(coord);
				grid.setTerrainValue(i, j, doubleToInner(value));
				cont++;
				System.out.println("Obtenidas " + cont + " de " + total
						+ " alturas\r");
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

	/**
	 * Set time and date for the simulation
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 */
	public void setDateAndTime(int year, int month, int dayOfMonth,
			int hourOfDay, int minute) {
		this.currentDateAndTime = new DateAndTime(year, month, dayOfMonth,
				hourOfDay, minute);
		defaultLogger.debugln("Time has been set to :"
				+ currentDateAndTime.toString());
	}

	/**
	 * Gets currentDateAndTime
	 * 
	 * @return currentDateAndTime
	 */
	public DateAndTime getDateAndTime() {
		return currentDateAndTime;
	}

	/**
	 * Updates de current time by adding updateTimeMinutes to currentDateAndTime
	 */
	public void updateTime() {
		currentDateAndTime.updateTime(updateTimeMinutes);
		defaultLogger.debugln("Time updated to: "+currentDateAndTime.toString());
	}

	/**
	 * Set the time in minutes between two steps of the simulation
	 * 
	 * @param updateTimeMinutes
	 */
	public void setUpdateTimeMinutes(int updateTimeMinutes) {
		defaultLogger.debugln("Update Time set To " + updateTimeMinutes
				+ " min");
		this.updateTimeMinutes = updateTimeMinutes;
	}

	public int getUpdateTimeMinutes() {
		return updateTimeMinutes;
	}

	public void setDefaultLogger(Logger defaultLogger) {
		this.defaultLogger = defaultLogger;
	}

	public Logger getDefaultLogger() {
		return defaultLogger;
	}

	public void disableDefaultLogger() {
		defaultLogger.disable();
	}

}
