package kml.flood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import kml.KmlWriter;
import util.HexagonalGrid;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class FloodKml extends KmlWriter{

	private FloodScenario scene;
	private FloodHexagonalGrid grid;
	private FloodScenario newScene;
	private FloodHexagonalGrid newGrid;
	
	public FloodKml(FloodScenario scene) {
		this.scene = scene;
		this.grid = (FloodHexagonalGrid) scene.getGrid();
	}
	
	public void snapShot(FloodScenario newScene) {
			createDocument("Flooding State Level", "RainFalling Motherfuckers");
			ArrayList<LatLng> tiles = getModTiles(newScene);
			
			HashMap<Short, ArrayList<LatLng>> polygons = getSameHigthPolygons(tiles);
			
			ArrayList<ArrayList<LatLng>> polygon = getIndependentPolygones(polygons);
			
			ArrayList<ArrayList<LatLng>> polygonBorders = getPolygonBorders(polygon);
			
			long cont =0;
			for (ArrayList<LatLng> pol : polygonBorders){
				createPolygon("Flood "+cont, "", pol);
			}
			createKmlFile(scene.getName());
	}
	
	private ArrayList<ArrayList<LatLng>> getPolygonBorders(
			ArrayList<ArrayList<LatLng>> polygon) {
		ArrayList<ArrayList<LatLng>> borders = new ArrayList<ArrayList<LatLng>>();
		for (ArrayList<LatLng> pol : polygon){
			ArrayList<LatLng> border = new ArrayList<LatLng>();
			Iterator<LatLng> it = pol.iterator();
			while (it.hasNext()){
				LatLng c = it.next();
				if (!pol.containsAll(newScene.getAdyacents(c))){
					border.add(c);
				}
			}
			//TODO ordenadr los bordes de tal forma que al imprimirlos se vean bien
			Collections.sort(border);
			borders.add(border);
		}
		
		return borders;
	}
/**
 * From a list of coords with ecual elevation returns a list of adyacent coords with ecual altitude
 * @param polygons
 * @return list of related coords
 */
	private ArrayList<ArrayList<LatLng>> getIndependentPolygones(
			HashMap<Short, ArrayList<LatLng>> polygons) {
		ArrayList<ArrayList<LatLng>> polygonList = new ArrayList<ArrayList<LatLng>>();
		for (ArrayList<LatLng> coords : polygons.values()){
			ArrayList<LatLng> onePolygon = new ArrayList<LatLng>();
			while (!coords.isEmpty()){
				onePolygon.add(coords.remove(0));
				int size = 0;
				int newsize = 1;
				while ((size != newsize) && !coords.isEmpty()){
					size = onePolygon.size();
					for (LatLng c : coords){
						if (newScene.getAdyacents(c).contains(onePolygon)){
							onePolygon.add(c);
							coords.remove(c);
						}
					}
					newsize = onePolygon.size();
				}
				polygonList.add(onePolygon);
				onePolygon = new ArrayList<LatLng>();				
			}
		}
		return polygonList;
	}
/**
 * returns modified tiles from previous scene
 * @param newScene
 * @return
 */
	private ArrayList<LatLng> getModTiles(FloodScenario newScene) {
		ArrayList<LatLng> modTiles = new ArrayList<LatLng>();
		int dims[] = scene.getGridSize();
		int x=dims[0];
		int y=dims[1];
		HexagonalGrid newGrid = newScene.getGrid();
		for (int i=0;i<x;i++){
			for (int j=0;j<y;j++){
				if (grid.getTerrainValue(x, y) != newGrid.getTerrainValue(x, y)){
					modTiles.add(newScene.tileToCoord(x, y));
				}
			}
		}
		return modTiles;
	}
	public void printPolygons(HashSet<int[]> mods){
		
	}
	
	public HashMap<Short, ArrayList<LatLng>> getSameHigthPolygons(ArrayList<LatLng> tiles){
		HashMap<Short, ArrayList<LatLng>> polygons = new HashMap<Short, ArrayList<LatLng>>();
		for (LatLng tile : tiles){
			ArrayList<LatLng> polygon = polygons.get(tile.getAltitude());
			if (polygon == null){
				polygon = new ArrayList<LatLng>();
				polygons.put(tile.getAltitude(), polygon);
			}
			polygon.add(tile);			
		}		
		return polygons;		
	}

	
}
