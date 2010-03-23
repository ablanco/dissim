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

import java.util.Hashtable;

import kml.KmlFlood;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Point;
import util.Snapshot;
import util.jcoord.LatLng;

public class TimeStampTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Nueva Orleans NW: 29.953260, -90.088238 SE: 29.918075, -90.053707 UTM
		 * NW: 29.952869, -90.063764 SE: 29.952158, -90.062461
		 */

		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953260,
				-90.088238, (short) 10), new LatLng(29.918075, -90.053707,
				(short) 10), 0, 0, 500);
		Snapshot newOrleans = new Snapshot("TimeStampTest", "", 
				grid, new DateAndTime(2000, 3, 15, 15, 3),
				new Hashtable<String, Point>());

		
//		KmlFlood k = new KmlFlood(newOrleans.getKml());
//
//		for (int rep = 0; rep < 6; rep++) {
//			for (int i = 0; i < grid.getColumns(); i++) {
//				for (int j = 0; j < grid.getRows(); j++) {
//					short x = (short) ((Math.random() * 100) % 24);
//					grid.setTerrainValue(i, j, x);
//				}
//			}
//			k.update(newOrleans);
//			newOrleans.updateTime(30);
//		}
//		newOrleans.writeKml();
	}

}
