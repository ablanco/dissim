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
import util.flood.WaterSource;
import util.jcoord.LatLng;

public class Sim1Test {

	public static void generateScenario(int option) {
		switch (option) {
		case 0:
			smallGrid(true);
			break;
		case 1:
			smallGrid(false);
			break;
		}
	}

	private static void smallGrid(boolean waterAgents) {
		FloodScenario scen = new FloodScenario();
		scen.setWaterAgents(waterAgents);
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), (short) 800);
		// 5 x 5
		scen.obtainTerrainElevation();
		System.out.println("Obtenidas todas las alturas.");
		scen.addWaterSource(new WaterSource(new LatLng(29.9532, -90.0882),
				(short) 2, 1000L));
		scen.complete();
	}

}
