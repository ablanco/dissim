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

import jade.core.AID;
import gui.VisorFrame;
import util.DateAndTime;
import util.Snapshot;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;

public class VisorTest {

	public static void main(String[] args) throws InterruptedException {
		FloodHexagonalGrid grid = new FloodHexagonalGrid(new LatLng(30.093681,
				-90.446724), new LatLng(30.083244, -90.434048), 0, 0, 400);
		
		System.out.println("Grid: " + grid.getDimX() + "x" + grid.getDimY());

		Snapshot snap = new Snapshot(new AID(), grid, new DateAndTime(2000, 12,
				15, 18, 55));
		VisorFrame v = new VisorFrame();
		grid.setTerrainValue(0, 0, (short) -20);
		grid.setTerrainValue(0, 1, (short) -40);
		grid.setTerrainValue(0, 2, (short) -50);
		grid.setTerrainValue(1, 0, (short) 0);
		grid.setTerrainValue(1, 1, (short) 100);
		grid.setTerrainValue(1, 2, (short) -20);
		grid.setTerrainValue(2, 0, (short) 200);
		grid.setTerrainValue(2, 1, (short) 40);
		grid.setTerrainValue(2, 2, (short) 100);
		grid.setWaterValue(0, 0, (short) 40);
		grid.setWaterValue(0, 1, (short) 60);
		grid.setWaterValue(0, 2, (short) 60);
		grid.setWaterValue(1, 0, (short) 100);
		v.update(snap);
		v.setVisible(true);

		Thread.sleep(2000L);

		grid.setWaterValue(1, 2, (short) 200);
		v.update(snap);

		Thread.sleep(2000L);
		v.dispose();
	}

}
