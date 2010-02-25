package test;

import util.Point;
import util.Scenario;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class PositionGridTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scenario newOrleans = new FloodScenario();
		newOrleans.setGeoData(new LatLng(29.953260, -90.088238, (short)10), new LatLng(
				29.918075, -90.053707, (short)10), (short) 100);
		newOrleans.setName("Position Grid Test");
		newOrleans.setDescription("NW SE 1m");
		newOrleans.setDateAndTime(2000, 3, 15, 15, 3);
		newOrleans.setUpdateTimeMinutes(1);
		newOrleans.complete();
		
		System.out.println(newOrleans.toString());
		int err = 0;
		int[] dim = newOrleans.getGridSize();

		for (int i=0;i<dim[0];i++){
			for (int j=0;j<dim[1];j++){
				LatLng c =newOrleans.tileToCoord(i, j); 
				Point p = newOrleans.coordToTile(c);
				if ((i!=p.getX()) || (j!=p.getY())){
					System.err.println("["+i+","+j+"] != ["+p.getX()+","+p.getY()+"]");
					err++;
				}
			}
		}
		System.err.println();
		System.err.println("*** Errores ="+(double)err/(dim[0]*dim[1]));
	}

}
