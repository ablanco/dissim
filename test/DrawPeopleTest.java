package test;

import java.util.Hashtable;

import util.DateAndTime;
import util.HexagonalGrid;
import util.Pedestrian;
import util.Snapshot;
import util.jcoord.LatLng;

public class DrawPeopleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953260,
				-90.088238, (short) 10), new LatLng(29.918075, -90.053707,
				(short) 10), 0, 0, 500);
		Snapshot newOrleans = new Snapshot("DrawPeopleTest", "",
				grid, new DateAndTime(2000, 3, 15, 15, 3),
				new Hashtable<String, Pedestrian>());

//		KmlPeople k = new KmlPeople(newOrleans.getKml());
//		k.update(newOrleans);
//		newOrleans.writeKml();
	}

}
