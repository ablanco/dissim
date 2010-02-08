package test;

import kml.KmlWriter;
import util.Scenario;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class hexagramTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scenario newOrleans = new FloodScenario();

		//newOrleans.setArea(new LatLng(29.953260, -90.088238), new LatLng(
		//		29.918075, -90.053707));

		newOrleans.setGeoData(new LatLng(29.953260, -90.088238, (short)10), new LatLng(
				29.918075, -90.053707, (short)10), (short) 150);
		newOrleans.complete();
		
		System.out.println(newOrleans.toString());
		KmlWriter k = new KmlWriter();
		k.createDocument("Hexagam Maps", "Test of deployment hexagrams");
		
		LatLng c = newOrleans.tileToCoord(0, 0);
		LatLng b = newOrleans.tileToCoord(11, 7);
		
		int a[] = newOrleans.coordToTile(b);
		int d[] = newOrleans.coordToTile(c);
		
		System.out.println("[11][7] = "+b+", "+b+" = ["+a[0]+"]["+a[1]+"]");
		System.out.println("[0][0] = "+c+", "+c+" = ["+d[0]+"]["+d[1]+"]");
		
		for (int i=0;i<newOrleans.getGridSize()[0];i++){
			for (int j=0;j<newOrleans.getGridSize()[1];j++){
				k.createHexagon(newOrleans.tileToCoord(i, j));				
			}
		}
		k.createKmlFile("New Orleans Hexagram");

	}

}
