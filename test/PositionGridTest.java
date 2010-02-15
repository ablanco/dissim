package test;

import util.HexagonalGrid;
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
				29.918075, -90.053707, (short)10), (short) 10);
		newOrleans.setName("New Orleans Hexagrams");
		newOrleans.setDescription("NW SE 1m");
		newOrleans.complete();
		
		System.out.println(newOrleans.toString());
		int ierr = 0;
		int jerr = 0;
		int[] dim = newOrleans.getGridSize();
		HexagonalGrid grid = newOrleans.getGrid();
		for (int i=0;i<dim[0];i++){
			for (int j=0;j<dim[1];j++){
				LatLng c =newOrleans.tileToCoord(i, j); 
				int d[] = newOrleans.coordToTile(c);
				if ((i!=d[0]) || (j!=d[1])){
					System.err.println();
					System.err.print("["+i+","+j+"] ");
					if (i!=d[0]){
						System.err.print("i: "+i+"!= "+d[0]+" ");
						ierr++;
					}
					if (j!=d[1]){
						System.err.print("j: "+j+"!= "+d[1]);
						jerr++;
					}
				}
			}
		}
		System.err.println();
		System.err.println("*** Errores en i:"+ierr+", Errores en j:"+jerr+" Aciertos :"+(dim[0]*dim[1]-(ierr+jerr)));
	}

}
