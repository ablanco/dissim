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

import util.Scenario;

public class FloodScenario extends Scenario {

	private static final long serialVersionUID = 1L;

	protected ArrayList<double[]> waterSources; // TODO Array o Linked??

	public FloodScenario() {
		complete = false;
		waterSources = new ArrayList<double[]>();
		current = this;
	}

	public boolean addWaterSource(double latitude, double longitude,
			double amount, long rythm) {
		boolean result = false;
		// TODO las latitudes y longitudes se pueden comparar directamente?
		if (NWlat >= latitude && NWlong >= longitude && SElat <= latitude
				&& SElong <= longitude) {
			result = waterSources.add(new double[] { latitude, longitude,
					amount, rythm });
		}
		return result;
	}

	public Iterator<double[]> getWaterSourcesIterator() {
		return waterSources.iterator();
	}

}
