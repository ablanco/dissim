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

import java.awt.Dimension;
import java.awt.Toolkit;

import util.flood.FloodHexagonalGrid;
import gui.VisorFrame;

public class VisorTest {

	public static void main(String[] args) throws InterruptedException {
		FloodHexagonalGrid grid = new FloodHexagonalGrid(3, 3, false);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (dim.width * 0.9);
		int height = (int) (dim.height * 0.9);
		int radiusX = (width / grid.getDimX()) / 2;
		int radiusY = (height / grid.getDimY()) / 2;
		int radius;
		if (radiusX < radiusY)
			radius = radiusX;
		else
			radius = radiusY;
		if (radius < 30)
			radius = 30;

		VisorFrame v = new VisorFrame();
		grid.setTerrainValue(0, 0, (short) -2);
		grid.setTerrainValue(0, 1, (short) -4);
		grid.setTerrainValue(0, 2, (short) -5);
		grid.setTerrainValue(1, 0, (short) 0);
		grid.setTerrainValue(1, 1, (short) 1);
		grid.setTerrainValue(1, 2, (short) -2);
		grid.setTerrainValue(2, 0, (short) 2);
		grid.setTerrainValue(2, 1, (short) 4);
		grid.setTerrainValue(2, 2, (short) 1);
		grid.setWaterValue(0, 0, (short) 4);
		grid.setWaterValue(0, 1, (short) 6);
		grid.setWaterValue(0, 2, (short) 6);
		grid.setWaterValue(1, 0, (short) 1);
		v.update(grid);
		v.setVisible(true);

		Thread.sleep(2000L);

		grid.setWaterValue(1, 2, (short) 2);
		v.update(grid);
	}

}
