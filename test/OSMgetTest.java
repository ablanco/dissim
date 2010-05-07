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

import osm.OsmMap;
import util.HexagonalGrid;
import util.jcoord.LatLng;

public class OSMgetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953, -90.088, (short)10), new LatLng(
				29.940, -90.070, (short)10), 0,0,10);
		
//		HexagonalGrid grid = new HexagonalGrid(new LatLng(30.093681, -90.446724, (short)10), new LatLng(
//				30.083244, -90.434048, (short)10), (short) 10);
//		DateAndTime dateTime = new DateAndTime(2008, 12, 13, 12, 5);
//		Snapshot snapShot = new Snapshot(new AID(), grid, dateTime);
		
		OsmMap osmMap = OsmMap.getMap(grid);
		
		//Mostando info por pantalla
		System.err.println(osmMap);
		
	}
	
	
 
}
