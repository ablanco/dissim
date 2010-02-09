package kml.flood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kml.KmlWriter;
import util.HexagonalGrid;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class FloodKml extends KmlWriter {

	private FloodScenario scene;
	private FloodHexagonalGrid grid;

	public FloodKml(FloodScenario scene) {
		this.scene = scene;
		this.grid = (FloodHexagonalGrid) scene.getGrid();
	}

	public void snapShot(FloodScenario newScene) {
		createDocument("Flooding State Level", "RainFalling Motherfuckers");
		/*
		 * ArrayList<LatLng> tiles = getModTiles(newScene);
		 * 
		 * HashMap<Short, ArrayList<LatLng>> polygons =
		 * getSameHigthPolygons(tiles);
		 * 
		 * ArrayList<ArrayList<LatLng>> polygon =
		 * getIndependentPolygones(polygons);
		 * 
		 * ArrayList<ArrayList<LatLng>> polygonBorders =
		 * getPolygonBorders(polygon);
		 * 
		 * 
		 * 
		 * 
		 * long cont =0; for (ArrayList<LatLng> pol : polygonBorders){
		 * createPolygon("Flood "+cont, "", pol); }
		 */

		long cont = 0;
		for (List<List<LatLng>> regions : getBorderRegions(newScene)) {
			for (List<LatLng> region : regions) {
				//TODO region podría no estar ordenados y salir cosas raras
				//Collections.sort(region);
				createPolygon("Flood " + cont, "", region);
			}

		}

		createKmlFile(scene.getName());
	}





	private Collection<List<List<LatLng>>> getBorderRegions(
			FloodScenario newScene) {
		HashMap<Short, List<List<LatLng>>> levelRegions = new HashMap<Short, List<List<LatLng>>>();
		int dims[] = scene.getGridSize();
		int x = dims[0];
		int y = dims[1];
		HexagonalGrid newGrid = newScene.getGrid();
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(i, j);
				LatLng coord = newScene.tileToCoord(i, j);
				// si son diferentes
				if (grid.getTerrainValue(i, j) != altitude) {
					boolean borde = false;
					// es borde si los adyacentes tienen distinto valor
					for (LatLng c : newScene.getAdjacents(coord)) {
						if (c.getAltitude() != altitude) {
							borde = true;
							break;
						}
					}
					// si es borde lo añadimos
					if (borde) {
						List<List<LatLng>> regions = levelRegions.get(altitude);
						if (regions == null) {// si no exixte la region la
												// creamos
							regions = new ArrayList<List<LatLng>>();
						}
						// Puede haber regiones sin unir
						boolean newRegion = true;
						for (List<LatLng> region : regions) {
							List<LatLng> aux = null;
							if (!newRegion) {// si encontramos otra region a la
												// que pertenece hay que unirlas
								if (region.contains(newScene
										.getAdjacents(coord))) {
									aux.addAll(region);
									regions.removeAll(region);
								}
							} else if (region.contains(newScene
									.getAdjacents(coord))) {// pertenece a una
															// region existente,
															// añadimos el borde
								newRegion = false;
								region.add(coord);
								// referenciamos por si hay que unirla con otra
								// region
								aux = region;
							}
						}
						if (newRegion) {// es una nueva region
							List<LatLng> region = new LinkedList<LatLng>();
							region.add(coord);
							regions.add(region);
							levelRegions.put(altitude, regions); //creo la lista de regiones
						}
						
						
					}

				}
			}
		}
		return levelRegions.values();
	}
	
	/*
	private ArrayList<ArrayList<LatLng>> getPolygonBorders(
			ArrayList<ArrayList<LatLng>> polygon) {
		ArrayList<ArrayList<LatLng>> borders = new ArrayList<ArrayList<LatLng>>();
		for (ArrayList<LatLng> pol : polygon) {
			ArrayList<LatLng> border = new ArrayList<LatLng>();
			Iterator<LatLng> it = pol.iterator();
			while (it.hasNext()) {
				LatLng c = it.next();
				if (!pol.containsAll(newScene.getAdyacents(c))) {
					border.add(c);
				}
			}
			// ordenadr los bordes de tal forma que al imprimirlos se vean
			// bien
			Collections.sort(border);
			borders.add(border);
		}

		return borders;
	}

	private ArrayList<ArrayList<LatLng>> getIndependentPolygones(
			HashMap<Short, ArrayList<LatLng>> polygons) {
		ArrayList<ArrayList<LatLng>> polygonList = new ArrayList<ArrayList<LatLng>>();
		for (ArrayList<LatLng> coords : polygons.values()) {
			ArrayList<LatLng> onePolygon = new ArrayList<LatLng>();
			while (!coords.isEmpty()) {
				onePolygon.add(coords.remove(0));
				int size = 0;
				int newsize = 1;
				while ((size != newsize) && !coords.isEmpty()) {
					size = onePolygon.size();
					for (LatLng c : coords) {
						if (newScene.getAdyacents(c).contains(onePolygon)) {
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

	private ArrayList<LatLng> getModTiles(FloodScenario newScene) {
		ArrayList<LatLng> modTiles = new ArrayList<LatLng>();
		int dims[] = scene.getGridSize();
		int x = dims[0];
		int y = dims[1];
		HexagonalGrid newGrid = newScene.getGrid();
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				if (grid.getTerrainValue(x, y) != newGrid.getTerrainValue(x, y)) {
					modTiles.add(newScene.tileToCoord(x, y));
				}
			}
		}
		return modTiles;
	}
	public HashMap<Short, ArrayList<LatLng>> getSameHigthPolygons(
			ArrayList<LatLng> tiles) {
		HashMap<Short, ArrayList<LatLng>> polygons = new HashMap<Short, ArrayList<LatLng>>();
		for (LatLng tile : tiles) {
			ArrayList<LatLng> polygon = polygons.get(tile.getAltitude());
			if (polygon == null) {
				polygon = new ArrayList<LatLng>();
				polygons.put(tile.getAltitude(), polygon);
			}
			polygon.add(tile);
		}
		return polygons;
	}
*/
}
