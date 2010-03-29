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

import util.Scenario;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import util.jcoord.LatLng;

public class SimulationTest {

	public static void main(String[] args) {
		generateScenario(0, new String[] { Boolean.toString(false) });
	}

	public static Scenario generateScenario(int option, String[] arguments) {
		Scenario scen = null;
		switch (option) {
		case 0:
			scen = smallGrid();
			break;
		case 1:
			scen = randomGrid(Integer.parseInt(arguments[0]), Integer
					.parseInt(arguments[1]));
			break;
		}
		return scen;
	}

	private static Scenario smallGrid() {
		FloodScenario scen = new FloodScenario();
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), (short) 800);
		scen.setPrecision((short) 10);
		scen.addWaterSource(new WaterSource(new LatLng(29.945, -90.085), scen
				.doubleToInner(4)));
		scen.setFloodUpdateTime(100L);
		scen.setWaterSourceUpdateTime(200L);
		scen.setWaterSourceMinutes(5);
		scen.addPeople(new LatLng(29.946, -90.085));
		// scen.obtainTerrainElevation();
		scen.setStartTime(2010, 2, 26, 20, 32);
		scen.complete();
		return scen;
	}

	private static Scenario randomGrid(int tileSize, int numEnv) {
		FloodScenario scen = new FloodScenario();
		scen.setName("AlturasAleatorias");
		scen.setDescription("Test con alturas del terreno aleatorias");
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), tileSize);
		scen.setPrecision((short) 10);
		scen.addWaterSource(new WaterSource(new LatLng(29.937, -90.065), scen
				.doubleToInner(20)));
		scen.addPeople(new LatLng(29.937, -90.068));
		scen.setFloodUpdateTime(50);
		scen.setPeopleUpdateTime(50);
		scen.setWaterSourceUpdateTime(20L);
		scen.setWaterSourceMinutes(10);
		// scen.disableDefaultLogger();
		scen.setNumEnv(numEnv);
		scen.setStartTime(2010, 2, 26, 20, 32);
		scen.complete();
		return scen;
	}

}
