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

import java.util.Random;

import util.HexagonalGrid;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import util.jcoord.LatLng;

public class SimulationTest {

	public static void main(String[] args) {
		generateScenario(0, new String[] { Boolean.toString(false) });
	}

	public static void generateScenario(int option, String[] arguments) {
		switch (option) {
		case 0:
			smallGrid(Boolean.parseBoolean(arguments[0]));
			break;
		case 1:
			randomGrid(Boolean.parseBoolean(arguments[0]), Short
					.parseShort(arguments[1]));
			break;
		}
	}

	private static void smallGrid(boolean waterAgents) {
		System.out.println("Usando agentes agua: " + waterAgents);
		FloodScenario scen = new FloodScenario();
		scen.setWaterAgents(waterAgents);
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), (short) 800);
		scen.setPrecision((short) 10);
		boolean ws = scen.addWaterSource(new WaterSource(new LatLng(29.9532,
				-90.0882), scen.doubleToInner(4), 1500L));
		System.out.println("Water Source dentro del área de simulación: " + ws);
		scen.obtainTerrainElevation();
		scen.complete();
	}

	private static void randomGrid(boolean waterAgents, short tileSize) {
		System.out.println("Usando agentes agua: " + waterAgents);
		FloodScenario scen = new FloodScenario();
		scen.setWaterAgents(waterAgents);
		scen.setGeoData(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707), tileSize);
		scen.setPrecision((short) 10);
		boolean ws = scen.addWaterSource(new WaterSource(new LatLng(29.9532,
				-90.0882), scen.doubleToInner(20), 100L));
		System.out.println("Water Source dentro del área de simulación: " + ws);
		HexagonalGrid grid = scen.getGrid();
		int x = grid.getDimX();
		int y = grid.getDimY();
		System.out.println("Tamaño del grid: " + x + "x" + y);
		Random rnd = new Random(System.currentTimeMillis());
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				grid.setTerrainValue(i, j, (short) (rnd.nextInt(500) - 250));
			}
		}
		scen.setFloodUpdateTime(5);
		scen.disableDefaultLogger();
		scen.complete();
	}

}
