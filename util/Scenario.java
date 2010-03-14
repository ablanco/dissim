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
import java.util.Iterator;
import java.util.LinkedList;

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
	 * Periodo de actualización de los visores
	 */
	private long updateVisor = 1000L;
	/**
	 * Periodo de actualización de los generadores de KML
	 */
	private long updateKML = 5000L;
	/**
	 * Periodo de actualización de los agentes persona
	 */
	private long updatePeople = 300L;
	/**
	 * Coordinates of the North West point of the simulation area
	 */
	protected LatLng globalNW = null;
	/**
	 * Coordinates of the South East point of the simulation area
	 */
	protected LatLng globalSE = null;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private int tileSize = -1;
	/**
	 * Number of enviroment agents
	 */
	private int numEnv = 1;
	private ArrayList<LatLng[]> envAreas = null;
	private ArrayList<int[]> envSizes = null;

	// TODO Mejorar, demasiado limitado
	private LinkedList<LatLng> people = new LinkedList<LatLng>();

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
	public void setGeoData(LatLng NW, LatLng SE, int tileSize) {
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

	public int getTileSize() {
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

	public long getUpdatePeople() {
		return updatePeople;
	}

	public void setUpdatePeople(long updatePeople) {
		this.updatePeople = updatePeople;
	}

	public int getNumEnv() {
		return numEnv;
	}

	public void setNumEnv(int numEnv) {
		// TODO En realidad los que no valen son los primos
		if (!(numEnv == 1 || (numEnv % 2) == 0))
			throw new IllegalArgumentException(
					"Number of enviroments must be one or even");

		this.numEnv = numEnv;
	}

	public LatLng[] getEnvArea(int index) {
		if (globalNW == null || globalSE == null || tileSize < 0)
			throw new IllegalStateException(
					"Geographical data hasn't been initialized yet.");
		if (envAreas == null)
			divideAreaBetweenEnvs();
		return envAreas.get(index);
	}

	public int[] getEnvSize(int index) {
		if (globalNW == null || globalSE == null || tileSize < 0)
			throw new IllegalStateException(
					"Geographical data hasn't been initialized yet.");
		if (envSizes == null)
			divideAreaBetweenEnvs();
		return envSizes.get(index);
	}

	private void divideAreaBetweenEnvs() {
		envAreas = new ArrayList<LatLng[]>(numEnv);
		envSizes = new ArrayList<int[]>(numEnv);

		if (numEnv == 1) {
			envAreas.add(new LatLng[] { globalNW, globalSE });
			int[] size = HexagonalGrid.calculateSize(globalNW, globalSE,
					tileSize);
			envSizes.add(new int[] { size[0], size[1], 0, 0 });
			return;
		}

		double diflng = Math.abs(globalNW.getLng() - globalSE.getLng());
		double diflat = Math.abs(globalNW.getLat() - globalSE.getLat());

		System.out.println("GLOBAL: NW " + globalNW.toString() + " SE "
				+ globalSE.toString());

		// TODO Mejorar
		int mitt = numEnv / 2;
		diflat = diflat / 2.0;
		diflng = diflng / ((double) mitt);
		int offX = 0;
		int offY = 0;
		double lat = globalNW.getLat();
		for (int i = 0; i < numEnv; i++) {
			if (i == mitt) {
				lat -= diflat;
				offX = 0;
				offY = envSizes.get(i - 1)[1];
			}

			LatLng NW = new LatLng(lat, globalNW.getLng()
					+ (diflng * Math.abs(i % mitt)));
			LatLng SE = new LatLng(lat - diflat, globalNW.getLng() + diflng
					+ (diflng * Math.abs(i % mitt)));
			int[] size = HexagonalGrid.calculateSize(NW, SE, tileSize);

			System.out.println("ENV" + i + ": NW " + NW.toString() + " SE "
					+ SE.toString());

			envAreas.add(i, new LatLng[] { NW, SE });
			envSizes.add(i, new int[] { size[0], size[1], offX, offY });

			offX += size[0];
		}
	}

	public int getEnviromentByCoord(LatLng coord) {
		if (envAreas == null)
			throw new IllegalStateException(
					"Enviroments haven't been initialized");

		int idx = 0;
		for (LatLng[] envCoords : envAreas) {
			if (coord.isContainedIn(envCoords[0], envCoords[1]))
				return idx;
			idx++;
		}
		return -1;
	}

	public int getEnviromentByPosition(int x, int y) {
		if (envSizes == null)
			throw new IllegalStateException(
					"Enviroments haven't been initialized");

		int idx = 0;
		for (int[] envSize : envSizes) {
			// System.out.println("x: " + x + " y: " + y + " sx: " + envSize[0]
			// + " sy: " + envSize[1] + " ox: " + envSize[2] + " oy: "
			// + envSize[3]);
			if (x < (envSize[0] + envSize[2]) && x >= envSize[2]
					&& y < (envSize[1] + envSize[3]) && y >= envSize[3])
				return idx;
			idx++;
		}
		return -1;
	}

	public boolean addPeople(LatLng pos) {
		boolean result = false;
		if (pos.isContainedIn(globalNW, globalSE))
			result = people.add(pos);
		return result;
	}

	public Iterator<LatLng> getPeopleIterator() {
		return people.iterator();
	}

}
