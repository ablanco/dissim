package test;

import gui.Map.VisorMap;
import jade.core.AID;
import osm.GetOSMInfo;
import osm.OsmMap;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Snapshot;
import util.jcoord.LatLng;

public class OSMMapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.95, -90.088,
//				(short) 10), new LatLng(29.948, -90.080, (short) 10), 0, 0, 10);

//		 HexagonalGrid grid = new HexagonalGrid(new LatLng(30.093681,
//		 -90.446724, (short)10), new LatLng(
//		 30.083244, -90.434048, (short)10),0,0, (short) 10);
		 
			HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953, -90.088, (short)10), new LatLng(
					29.940, -90.070, (short)10), 0,0,10);
			
		DateAndTime dateTime = new DateAndTime(2008, 12, 13, 12, 5);
		Snapshot snapShot = new Snapshot(new AID(), grid, dateTime);
		GetOSMInfo osmInfo = new GetOSMInfo(grid);

		OsmMap osmMap = osmInfo.getOsmMap();

		osmMap.setMapInfo(grid);
		// Mostando info por pantalla
		System.err.println(grid.toString());
		System.err.println(osmMap);

		VisorMap v = new VisorMap();
		v.update(snapShot);
		v.setVisible(true);

		// VisorFrame v2 = new VisorFrame();
		// v2.update(snapShot);
		// v2.setVisible(true);
	}

}
