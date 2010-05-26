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

package kml;

/**
 * Contains necessary information for the updates
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class KmlInf {

	private short[][] waterGrid;
	private String begin;
	private String end;
	private String name;
	private double[] incs;

	/**
	 * Initializes parameters of values
	 * 
	 * @param name
	 *            of the environment
	 * @param begin
	 *            time for the event
	 * @param end
	 *            time for the event
	 * @param incs
	 *            in decimal degrees for the environment
	 */
	public KmlInf(String name, String begin, String end, double[] incs) {
		this.name = name;
		this.begin = begin;
		this.end = end;
		this.incs = incs;
	}

	/**
	 * Gets begin time of this simulation step
	 * 
	 * @return begin time
	 */
	public String getBegin() {
		return begin;
	}

	/**
	 * Gets end time of this simulation step
	 * 
	 * @return end time
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * Gets the water grid from this simulation step
	 * 
	 * @return water grid
	 */
	public short[][] getWaterGrid() {
		return waterGrid;
	}

	/**
	 * Gets environment name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets degree increments for this scenario
	 * 
	 * @return degree increments for this scenario
	 */
	public double[] getIncs() {
		return incs;
	}

	/**
	 * Sets water grid
	 * 
	 * @param grid
	 */
	public void setGrid(short[][] grid) {
		this.waterGrid = grid.clone();
	}

	/**
	 * Sets new end date for the simulation step, and end date become begin date
	 * 
	 * @param end
	 *            time of the events
	 */
	public void SetNewDate(String end) {
		begin = this.end;
		this.end = end;
	}

	@Override
	public String toString() {
		return name + ", From " + begin + " to " + end;
	}
}
