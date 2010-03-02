package test;

import util.DateAndTime;
import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;

public class PositionGridTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new DateAndTime(2000, 3, 15, 15, 3);
//		LatLng NW
		HexagonalGrid grid = new HexagonalGrid(new LatLng(29.953260, -90.088238, (short)10), new LatLng(
				29.918075, -90.053707, (short)10),0,0, 30);
		long err =0;
		System.out.println(grid.toString());

		for (int x=0;x<grid.getDimX();x++){
			for (int y=0;y<grid.getDimY();y++){
				LatLng c =grid.tileToCoord(x, y);
				Point p = grid.coordToTile(c);
				if (x!=p.getX() || y!=p.getY()){
					System.err.println(c.toString()+"["+x+","+y+"] != "+p.toString());
					err++;
				}else{
//					System.err.println("******["+x+","+y+"] == ["+p.getX()+","+p.getY()+"]");
				}
			}
		}
		
		System.err.println("*** Errores ="+(double)err/(grid.getDimX()*grid.getDimY()));
			}

}
