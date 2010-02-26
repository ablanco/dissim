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

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete = false;
	private String description = "";
	private String name = "";
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
	 * Time between two simulation Steps
	 */
	private int updateTimeMinutes = 1;
	/**
	 * Periodo de actualización de los visores
	 */
	private long updateVisor = 1000L;
	/**
	 * Periodo de actualización de los generadores de KML
	 */
	private long updateKML = 5000L;
	/**
	 * Coordinates of the North West point of the simulation area
	 */
	private LatLng globalNW = null;
	/**
	 * Coordinates of the South East point of the simulation area
	 */
	private LatLng globalSE = null;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private short tileSize;
	/**
	 * Number of enviroment agents
	 */
	private int numEnv = 1; // TODO unsigned

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
		globalNW = NW;
		globalSE = SE;
		this.tileSize = tileSize;
	}

	public LatLng[] getSimulationArea() {
		return new LatLng[] { globalNW, globalSE };
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
		if (globalNW == null || globalSE == null || tileSize < 0)
			throw new IllegalStateException(
					"There are mandatory parameters that hasn't been defined yet.");

		complete = true;
	}

	public short getTileSize() {
		return tileSize;
	}

	// @Override
	// public String toString() {
	// if (complete)
	// return "Current Time: " + currentDateAndTime.toString()
	// + "\nSize [" + gridX + "," + gridY + "] NW coord :"
	// + NW.toString() + ", SE Coord: " + SE.toString()
	// + "\nTile size :" + NW.distance(tileToCoord(0, 1)) + "kms"
	// + " ~ " + tileSize + "m, Diagonal =" + NW.distance(SE)
	// + "kms";
	// else
	// return "Incomplete scenario description: " + super.toString();
	// }

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

	// /**
	// * Set time and date for the simulation
	// *
	// * @param year
	// * @param month
	// * @param dayOfMonth
	// * @param hourOfDay
	// * @param minute
	// */
	// public void setDateAndTime(int year, int month, int dayOfMonth,
	// int hourOfDay, int minute) {
	// this.currentDateAndTime = new DateAndTime(year, month, dayOfMonth,
	// hourOfDay, minute);
	// // defaultLogger.debugln("Time has been set to :"
	// // + currentDateAndTime.toString());
	// }
	//
	// /**
	// * Gets currentDateAndTime
	// *
	// * @return currentDateAndTime
	// */
	// public DateAndTime getDateAndTime() {
	// return currentDateAndTime;
	// }
	//
	// /**
	// * Updates de current time by adding updateTimeMinutes to
	// currentDateAndTime
	// */
	// public void updateTime() {
	// currentDateAndTime.updateTime(updateTimeMinutes);
	// // defaultLogger.debugln("Time updated to: "
	// // + currentDateAndTime.toString());
	// }

	/**
	 * Set the time in minutes between two steps of the simulation
	 * 
	 * @param updateTimeMinutes
	 */
	public void setUpdateTimeMinutes(int updateTimeMinutes) {
		// defaultLogger.debugln("Update Time set To " + updateTimeMinutes
		// + " min");
		this.updateTimeMinutes = updateTimeMinutes;
	}

	public int getUpdateTimeMinutes() {
		return updateTimeMinutes;
	}

	// public void setDefaultLogger(Logger defaultLogger) {
	// this.defaultLogger = defaultLogger;
	// }
	//
	// public Logger getDefaultLogger() {
	// return defaultLogger;
	// }
	//
	// public void disableDefaultLogger() {
	// defaultLogger.disable();
	// }

	public long getUpdateVisorPeriod() {
		return updateVisor;
	}

	public void setUpdateVisorPeriod(long updateVisor) {
		this.updateVisor = updateVisor;
	}

	public long getUpdateKMLPeriod() {
		return updateKML;
	}

	public void setUpdateKMLPeriod(long updateKML) {
		this.updateKML = updateKML;
	}

	public int getNumEnv() {
		return numEnv;
	}

	public void setNumEnv(int numEnv) {
		this.numEnv = numEnv;
	}

}
