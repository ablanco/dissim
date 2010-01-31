package pruebas;

import kml.KmlWriter;
import util.Scenario;
import util.jcoord.LatLng;

public class PruebaKmlWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Nueva Orleans
		 * NW:  29.953260, -90.088238
		 * SE:  29.918075, -90.053707
		 * UTM
		 */
		
		Scenario newOrleans = Scenario.getCurrentScenario();
		
		newOrleans.setArea(new LatLng(29.953260, -90.088238), new LatLng(29.918075, -90.053707));
		newOrleans.setTileSize(50);
		
		System.out.println(newOrleans.toString());
		KmlWriter kmle = new KmlWriter("Nueva Orleans - Escenario", newOrleans);
	}

}
