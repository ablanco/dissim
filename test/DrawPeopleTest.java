package test;

import jade.core.AID;

import java.util.Hashtable;

import kml.KmlFlood;
import kml.KmlPeople;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Point;
import util.Snapshot;
import util.jcoord.LatLng;

public class DrawPeopleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953260,
				-90.088238, (short) 10), new LatLng(29.918075, -90.053707,
				(short) 10), 0, 0, 500);
		Snapshot newOrleans = new Snapshot("DrawPeopleTest", "", new AID(),
				grid, new DateAndTime(2000, 3, 15, 15, 3),
				new Hashtable<String, Point>());

//		KmlPeople k = new KmlPeople(newOrleans.getKml());
//		k.update(newOrleans);
//		newOrleans.writeKml();
	}

}
