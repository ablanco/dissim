package test;

import gui.Map.VisorMap;
import jade.core.AID;

import java.util.Hashtable;

import osm.Osm;
import osm.OsmMap;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Pedestrian;
import util.Snapshot;
import util.jcoord.LatLng;

public class OSMMapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Map BOX
		LatLng NW;
		LatLng SE;
		// Coordenadas con un rio en medio:
		// NW = new LatLng(29.947426,-90.075409);
		// SE = new LatLng(29.925797, -90.046214);

		// Coordenadas solo rio
		NW = new LatLng(29.939898, -90.064604);
		SE = new LatLng(29.934196, -90.051663);
		// Coordenadas centro ciudad
		// NW = new LatLng(29.953, -90.088, (short)10);
		// SE =new LatLng( 29.940, -90.070, (short)10);

		HexagonalGrid grid = new HexagonalGrid(NW, SE, 0, 0, 30);

		DateAndTime dateTime = new DateAndTime(2008, 12, 13, 12, 5);
		Snapshot snapShot = new Snapshot("OSMMapTest", "", grid,
				dateTime, new Hashtable<String, Pedestrian>());

		OsmMap osmMap = Osm.getMap(grid);

		osmMap.setMapInfo(grid);
		// Mostando info por pantalla
		System.out.println(grid.toString());
		System.out.println(osmMap.toString());

		VisorMap v = new VisorMap();
		v.update(snapShot, new AID());
		v.setVisible(true);

		// VisorFrame v2 = new VisorFrame();
		// v2.update(snapShot);
		// v2.setVisible(true);
	}
}
