package test;

import jade.core.AID;
import osm.GetOSMInfo;
import osm.OsmMap;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Snapshot;
import util.jcoord.LatLng;

public class OSMgetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String url="http://api.openstreetmap.org/api/0.6/map?bbox=11.54,48.14,11.543,48.145";
		
		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953, -90.088, (short)10), new LatLng(
				29.940, -90.070, (short)10), (short) 100);
		DateAndTime dateTime = new DateAndTime(2008, 12, 13, 12, 5);
		Snapshot snapShot = new Snapshot(new AID(), grid, dateTime);
		GetOSMInfo osmInfo = new GetOSMInfo(snapShot);
		
		OsmMap osmMap = osmInfo.getOsmMap();
		
		osmMap.setMapInfo(snapShot.getGrid());
		
	}
	
	
 
}
