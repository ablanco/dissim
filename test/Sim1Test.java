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

import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class Sim1Test {

	public static void generateScenario(int option) {
		switch (option) {
		case 0:
			smallGrid();
			break;
		}

	}

	private static void smallGrid() {
		FloodScenario scen = new FloodScenario();
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), 150);

		/*
		 * grid.setTerrainValue(0, 0, 9); grid.setTerrainValue(0, 2, 8);
		 * grid.setTerrainValue(1, 0, 7.8); grid.setTerrainValue(1, 1, 6);
		 * grid.setTerrainValue(1, 2, 8); grid.setTerrainValue(2, 0, 5);
		 * grid.setTerrainValue(2, 1, 5.3); grid.setTerrainValue(2, 2, 3);
		 */
	}

}
