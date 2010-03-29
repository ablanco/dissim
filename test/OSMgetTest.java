package test;

import osm.Osm;
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
		
		OsmMap osmMap = Osm.getMap(grid);
		
		//Mostando info por pantalla
		System.err.println(osmMap);
		
	}
	
	
 
}
