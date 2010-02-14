package test;

import gui.VisorFrame;

import java.util.HashSet;
import java.util.Set;

import kml.KmlWriter;
import kml.flood.FloodKml;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class KmlPolygonExpander {
	public static void main(String[] args) throws InterruptedException {
		Scenario newOrleans = new FloodScenario();

		//newOrleans.setArea(new LatLng(29.953260, -90.088238), new LatLng(
		//		29.918075, -90.053707));

		newOrleans.setGeoData(new LatLng(29.953260, -90.088238, (short)10), new LatLng(
				29.918075, -90.053707, (short)10), (short) 150);
		newOrleans.setName("New Orleans Hexagrams");
		newOrleans.setDescription("NW SE 1m");
		newOrleans.complete();
		
		System.out.println(newOrleans.toString());
		
		
		FloodHexagonalGrid grid = (FloodHexagonalGrid) newOrleans.getGrid();
		
		FloodKml k = new FloodKml();
		k.createDocument("Hexagam Maps", "Test of deployment hexagrams");
		
		VisorFrame v = new VisorFrame();
		v.updateGrid(k.getOldGrid());
		v.setVisible(true);
		
		Set<LatLng> s = new HashSet<LatLng>();
		LatLng a = new LatLng(1.0, 2.0);
		LatLng b = new LatLng(1.1, 2.0);
		LatLng c = new LatLng(1.0, 2.0);
		s.add(a);
		s.add(b);
		
		
		for (int i=0;i<newOrleans.getGridSize()[0];i++){
			for (int j=0;j<newOrleans.getGridSize()[1];j++){
				short x = (short)((Math.random()*100)%3);
				grid.setTerrainValue(i, j, x);				
			}
		}
		
		VisorFrame v1 = new VisorFrame();
		v1.updateGrid(grid);
		v1.setVisible(true);
		
		k.snapShot(newOrleans);
		
		if (s.contains(c)){
			System.out.println("c true");
		}
		if (c.equals(a)){
			System.out.println("e true");
		}
		
		
	}
}
