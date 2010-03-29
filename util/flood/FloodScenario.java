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

package util.flood;

import java.util.LinkedList;
import java.util.ListIterator;

import util.Scenario;
import util.jcoord.LatLng;

public class FloodScenario extends Scenario {

	private static final long serialVersionUID = 1L;

	/**
	 * Points where enters water into the simulation
	 */
	private LinkedList<WaterSource> waterSources;
	/**
	 * Milliseconds between every update of the flooded water position
	 */
	private long floodUpdateTime = 50L;
	/**
	 * Milliseconds between messages from water sources
	 */
	private long waterSourceUpdateTime = 75L;
	/**
	 * Real simulation time (minutes) between water entrances
	 */
	private int waterSourceMinutes = -1;

	public FloodScenario() {
		super();
		waterSources = new LinkedList<WaterSource>();
	}

	@Override
	public void complete() {
		if (waterSourceMinutes < 0)
			throw new IllegalStateException(
					"There are mandatory parameters that hasn't been defined yet.");

		super.complete();
	}

	public boolean addWaterSource(WaterSource ws) {
		if (globalNW == null || globalSE == null)
			throw new IllegalStateException(
					"Geographical data hasn't been initialized");

		boolean result = false;
		LatLng coord = ws.getCoord();

		if (coord.isContainedIn(globalNW, globalSE))
			result = waterSources.add(ws);

		return result;
	}

	public ListIterator<WaterSource> waterSourcesIterator() {
		return waterSources.listIterator();
	}

	public int waterSourcesSize() {
		return waterSources.size();
	}

	public void setFloodUpdateTime(long floodUpdateTime) {
		this.floodUpdateTime = floodUpdateTime;
	}

	public long getFloodUpdateTime() {
		return floodUpdateTime;
	}

	public long getWaterSourceUpdateTime() {
		return waterSourceUpdateTime;
	}

	public void setWaterSourceUpdateTime(long waterSourceUpdateTime) {
		this.waterSourceUpdateTime = waterSourceUpdateTime;
	}

	public int getWaterSourceMinutes() {
		return waterSourceMinutes;
	}

	public void setWaterSourceMinutes(int waterSourceMinutes) {
		if (waterSourceMinutes <= 0)
			throw new IllegalArgumentException(
					"Time between water entrance cannot be zero or negative");

		this.waterSourceMinutes = waterSourceMinutes;
	}

}
