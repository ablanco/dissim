package test;

import gui.VisorFrame;
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

		//Map BOX
		LatLng NW;
		LatLng SE;
//		Coordenadas con un rio en medio:
//		NW = new LatLng(29.947426,-90.075409);
//		SE = new LatLng(29.925797, -90.046214);

		//Coordenadas solo rio
		 NW = new LatLng(29.939898, -90.064604);
		 SE = new LatLng(29.934196, -90.051663);
		//Coordenadas centro ciudad
//		NW = new LatLng(29.953, -90.088, (short)10); 
//		SE =new LatLng(	29.940, -90.070, (short)10);

		 
		HexagonalGrid grid = new HexagonalGrid(NW, SE, 0,0,10);
			
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

		 VisorFrame v2 = new VisorFrame();
		 v2.update(snapShot);
		 v2.setVisible(true);
	}
}
