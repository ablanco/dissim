package kml.flood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kml.KmlWriter;
import util.HexagonalGrid;
import util.Punto;
import util.Scenario;
import util.jcoord.LatLng;

public class FloodKml extends KmlWriter {

	public FloodKml() {
		super();
	}

	public void snapShot(Scenario newScene) {
		createDocument("Flooding State Level", "RainFalling Motherfuckers");
		long cont = 0;
		for (List<Set<Punto>> regions : getBorderRegions(newScene)) {
			for (Set<Punto> region : regions) {
				// TODO region podría no estar ordenados y salir cosas raras
				// Collections.sort(region);
				SortedSet<LatLng> borderLine = new TreeSet<LatLng>();
				for (Punto c : region) {
					borderLine.add(newScene.tileToCoord(c.x, c.y));
				}
				createPolygon("Pol"+cont, borderLine);
				cont++;
				if (region.size() > 10){
					for (Punto c: region){
						System.out.print("["+c.x+","+c.y+"] ");
					}
					System.out.println();
				}
			}
		}
		System.out.println("Regiones detectadas"+cont);
		createKmzFile(newScene.getName());
	}

	private boolean addBorderToRegion(List<Set<Punto>> regions,
			Set<Punto> adyacents, Punto border) {
		for (Set<Punto> region : regions) {
			for (Punto adyacent : adyacents) {
				if (region.contains(adyacent)) {
					region.add(border);
//					System.out.print(", Borde añadido");
					return true;
				}
			}
		}
		return false;
	}

	private Collection<List<Set<Punto>>> getBorderRegions(Scenario newScene) {
		HashMap<Short, List<Set<Punto>>> levelRegions = new HashMap<Short, List<Set<Punto>>>();
		HexagonalGrid newGrid = newScene.getGrid();
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(x, y);
				// si son diferentes
				Punto p = new Punto(x,y,altitude); 
				if (oldGrid.getTerrainValue(x, y) != altitude) {
//					 System.out.print("!=, ");
					if (newScene.isBorderPoint(p)) {
						// Solo añadimos los bordes
						// System.out.print(", Borde");
//						System.out.print("(" + x + "," + y + ") Region :" + altitude);
						List<Set<Punto>> regions = levelRegions.get(altitude);
						if (regions == null) {
							// si no existe la region la creamos y añadimos el
							// borde
//							 System.out.print(", Nueva Lista Regiones Creada");
							regions = new ArrayList<Set<Punto>>();
							Set<Punto> region = new TreeSet<Punto>();
							region.add(p);
							regions.add(region);
							levelRegions.put(altitude, regions);
						} else {
							if (!addBorderToRegion(regions, newScene
									.getAdjacents(p), p)) {
								// si no pertenece a una region creada, creamos
								// una nueva region
//								 System.out.print(", Nueva Lista Region Creada");
								Set<Punto> region = new TreeSet<Punto>();
								region.add(p);
								regions.add(region);
							}
						}		
					} else {
						// System.out.print(", no es un borde");
					}
//					System.out.println();
				} else {
					// System.out.print(", no son distintos");
				}
			}
		}
		return levelRegions.values();
	}

	public HexagonalGrid getOldGrid() {
		return oldGrid;
	}


}
