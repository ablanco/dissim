package test;

import jade.core.AID;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import kml.KmlBase;
import util.DateAndTime;
import util.Pedestrian;
import util.Point;
import util.Snapshot;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;
import util.jcoord.LatLngComparator;

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
				(short) 10), 0, 0, 30);
		// FloodHexagonalGrid grid = new FloodHexagonalGrid(new
		// LatLng(29.953260,
		// -90.088238, (short) 10), new LatLng(29.952260, -90.087238,
		// (short) 10), 0, 0, 3);
		Snapshot newOrleans = new Snapshot("ConvexCoverture", "", grid,
				new DateAndTime(2000, 3, 15, 15, 3),
				new Hashtable<String, Pedestrian>());

		KmlBase k = new KmlBase();
		int c = newOrleans.getGrid().getColumns();
		int r = newOrleans.getGrid().getRows();

		SortedSet<LatLng> sl = new TreeSet<LatLng>(new LatLngComparator());
		HashSet<LatLng> ss = new HashSet<LatLng>();

		for (int rep = 0; rep < 6; rep++) {
			for (int i = 0; i < c; i++) {
				for (int j = 0; j < r; j++) {
					sl.add(newOrleans.getGrid().tileToCoord(i, j));
					ss.add(newOrleans.getGrid().tileToCoord(i, j));

					short x = (short) ((Math.random() * 100) % 2);
					grid.setWaterValue(i, j, x);
				}
			}

			List<Pedestrian> people = newOrleans.getPeople();
			for (int i = 0; i < 6; i++) {
				people.add(new Pedestrian(new Point(
						(int) ((Math.random() * 100) % c), (int) ((Math
								.random() * 100) % r))));
			}

			k.update(newOrleans, new AID());
			newOrleans.getPeople().clear();
			newOrleans.updateTime(3);
		}
		k.finish();
	}

}
