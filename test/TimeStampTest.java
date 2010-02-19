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

package test;

import kml.flood.FloodKml;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class TimeStampTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Nueva Orleans NW: 29.953260, -90.088238 SE: 29.918075, -90.053707 UTM
		 * NW:  29.952869, -90.063764  SE:  29.952158, -90.062461
		 */
		
		Scenario newOrleans = new FloodScenario();

		// newOrleans.setArea(new LatLng(29.953260, -90.088238), new LatLng(
		// 29.918075, -90.053707));
		newOrleans.setGeoData(new LatLng(29.953260, -90.088238, (short) 10),
				new LatLng(29.918075, -90.053707, (short) 10), (short) 300);
		newOrleans.setName("Time inundation Colored");
		newOrleans.setDescription("NW SE 1m");
		newOrleans.setDateAndTime(2000, 3, 15, 15, 3);
		newOrleans.setUpdateTimeMinutes(1);
		newOrleans.complete();

		System.out.println(newOrleans.toString());

		FloodHexagonalGrid grid = (FloodHexagonalGrid) newOrleans.getGrid();

		FloodKml k = new FloodKml();

		for (int rep=0;rep<6;rep++){
			for (int i = 0; i < newOrleans.getGridSize()[0]; i++) {
				for (int j = 0; j < newOrleans.getGridSize()[1]; j++) {
					short x = (short) ((Math.random() * 100) % 2);
					grid.setTerrainValue(i, j, x);
				}
			}
			
			System.out.println(newOrleans.toString());	
			k.update(newOrleans);
	
		}
		k.createKmlFile(newOrleans.getName());
	}

}
