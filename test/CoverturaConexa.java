package test;

import jade.core.AID;

import java.util.Hashtable;

import kml.KmlBase;
import util.DateAndTime;
import util.Pedestrian;
import util.Snapshot;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;

public class CoverturaConexa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Nueva Orleans NW: 29.953260, -90.088238 SE: 29.918075, -90.053707 UTM
		 * NW: 29.952869, -90.063764 SE: 29.952158, -90.062461
		 */

		FloodHexagonalGrid grid = new FloodHexagonalGrid(new LatLng(29.953260,
				-90.088238, (short) 10), new LatLng(29.918075, -90.053707,
				(short) 10), 0, 0, 600);
		// FloodHexagonalGrid grid = new FloodHexagonalGrid(new
		// LatLng(29.953260,
		// -90.088238, (short) 10), new LatLng(29.952260, -90.087238,
		// (short) 10), 0, 0, 3);
		Snapshot newOrleans = new Snapshot("ConvexCoverture", "", grid,
				new DateAndTime(2000, 3, 15, 15, 3),
				new Hashtable<String, Pedestrian>());

		KmlBase k = new KmlBase();

		// int c = newOrleans.getGrid().getColumns();
		// int r = newOrleans.getGrid().getRows();
		// for (int rep = 0; rep < 6; rep++) {
		// for (int i = 0; i < c; i++) {
		// for (int j = 0; j < r; j++) {
		// short x = (short) ((Math.random() * 100) % 2);
		// grid.setWaterValue(i, j, x);
		// }
		// }
		//
		// List<Pedestrian> people = newOrleans.getPeople();
		// for (int i = 0; i < 6; i++) {
		// people.add(new Pedestrian(new Point(
		// (int) ((Math.random() * 100) % c), (int) ((Math
		// .random() * 100) % r))));
		// }
		//
		// k.update(newOrleans, new AID());
		// newOrleans.getPeople().clear();
		// newOrleans.updateTime(3);
		// }

		k.update(newOrleans, new AID());
		newOrleans.updateTime(1);
		grid.setWaterValue(4, 4, (short) 5);
//		[[(5,6) 1], [(5,7) 1], [(4,5) 1], [(4,7) 1], [(3,4) 1], [(3,5) 1], [(3,6) 1], [(3,7) 1], [(2,5) 1], [(2,6) 1], [(2,7) 1], [(1,5) 1]]
		grid.setWaterValue(5,6 ,(short) 5);
		grid.setWaterValue(5,7 ,(short) 5);
		grid.setWaterValue(5,7 ,(short) 5);
		grid.setWaterValue(4,7 ,(short) 5);
		grid.setWaterValue( 3,4,(short) 5);
		grid.setWaterValue(3,5 ,(short) 5);
		grid.setWaterValue( 3,6,(short) 5);
		grid.setWaterValue(3,7 ,(short) 5);
		grid.setWaterValue(2,5 ,(short) 5);
		grid.setWaterValue(2,6 ,(short) 5);
		grid.setWaterValue(2,7 ,(short) 5);
		grid.setWaterValue(1,5 ,(short) 5);
		
//		 [[(2,2) 1], [(1,0) 1], [(1,1) 1], [(1,2) 1], [(0,2) 1], [(0,3) 1]]
		grid.setWaterValue(2,2 ,(short) 5);
		grid.setWaterValue(1,0 ,(short) 5);
		grid.setWaterValue(1,1 ,(short) 5);
		grid.setWaterValue(1,2 ,(short) 5);
		grid.setWaterValue(0,2 ,(short) 5);
		grid.setWaterValue( 0,3,(short) 5);
//	  [[(5,2) 1], [(5,3) 1], [(5,4) 1], [(4,1) 1], [(4,2) 1], [(4,3) 1], [(3,0) 1], [(3,1) 1], [(3,3) 1], [(2,0) 1], [(2,1) 1], [(2,3) 1], [(2,4) 1], 
		//[(1,0) 1], [(1,2) 1], [(1,4) 1], [(0,1) 1], [(0,2) 1]]
		grid.setWaterValue(5,2 ,(short) 5);
		grid.setWaterValue(5,3 ,(short) 5);
		grid.setWaterValue(5,4 ,(short) 5);
		grid.setWaterValue(4,1 ,(short) 5);
		grid.setWaterValue(4,2 ,(short) 5);
		grid.setWaterValue(4,3 ,(short) 5);
		grid.setWaterValue( 3,0,(short) 5);
		grid.setWaterValue(3,1 ,(short) 5);
		grid.setWaterValue( 3,3,(short) 5);
		grid.setWaterValue(2,0 ,(short) 5);
		grid.setWaterValue(2,1 ,(short) 5);
		grid.setWaterValue( 2,3,(short) 5);
		grid.setWaterValue(2,4 ,(short) 5);
		grid.setWaterValue( 1,0,(short) 5);
		grid.setWaterValue(1,2 ,(short) 5);
		grid.setWaterValue(1,4 ,(short) 5);
		grid.setWaterValue(0,1 ,(short) 5);
		grid.setWaterValue(0,2 ,(short) 5);
		
		
		k.update(newOrleans, new AID());
		newOrleans.updateTime(3);

		
		k.finish();
	}

}
