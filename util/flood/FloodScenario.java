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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import util.Scenario;
import util.jcoord.LatLng;

/**
 * Flood version of {@link Scenario}, contains methods for easily manage the
 * flooding
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class FloodScenario extends Scenario {

	private static final long serialVersionUID = 1L;

	/**
	 * Points where enters water into the simulation
	 */
	private LinkedList<WaterSource> waterSources;

	/**
	 * New {@link FloodScenario}
	 */
	public FloodScenario() {
		super();
		waterSources = new LinkedList<WaterSource>();
	}

	/**
	 * Loads the {@link FloodScenario} data from a text file
	 * 
	 * @param content
	 *            of the text file
	 */
	@Override
	protected void loadScenarioData(ArrayList<String> data) {
		super.loadScenarioData(data);
		for (String s : data) {
			if (!s.startsWith("#")) {
				String[] pair = s.split("=");
				if (pair[0].equals("waterSource")) {
					String[] ws = decodeScenArray(pair[1]);
					addWaterSource(new WaterSource(new LatLng(Double
							.parseDouble(ws[0]), Double.parseDouble(ws[1])),
							Short.parseShort(ws[2])));
				}
			}
		}
	}

	/**
	 * Add a new water source into the {@link FloodScenario}
	 * 
	 * @param ws
	 *            water source
	 * @return true if is contained in the simulation area
	 * @throws IllegalStateException
	 *             If geographical scenario data hasn't been initialized
	 */
	public boolean addWaterSource(WaterSource ws) {
		if (globalNW == null || globalSE == null)
			throw new IllegalStateException(
					"Geographical scenario data hasn't been initialized");

		boolean result = false;
		LatLng coord = ws.getCoord();

		if (coord.isContainedIn(globalNW, globalSE))
			result = waterSources.add(ws);

		return result;
	}

	/**
	 * Get an {@link Iterator} over water sources in the {@link FloodScenario}
	 * 
	 * @return water source iterator
	 */
	public ListIterator<WaterSource> waterSourcesIterator() {
		return waterSources.listIterator();
	}

	/**
	 * Get the number of water sources contained in the {@link FloodScenario}
	 * 
	 * @return number of water sources
	 */
	public int waterSourcesSize() {
		return waterSources.size();
	}

}
