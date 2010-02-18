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

	public void snapShot2(Scenario newScene) {
		createDocument("Flooding State Level", "RainFalling Motherfuckers");
		long cont = 0;
		for (List<SortedSet<Punto>> regions : getBorderRegions(newScene)) {
			for (SortedSet<Punto> region : regions) {
				// TODO region podría no estar ordenados y salir cosas raras
				// Collections.sort(region);

				createPolygon("Pol" + cont, regionToPoligon(region, newScene));
				cont++;
				if (region.size() > 10) {
					for (Punto c : region) {
						System.out.print("[" + c.x + "," + c.y + "] ");
					}
					System.out.println();
				}
			}
		}
		System.out.println("Regiones detectadas" + cont);
		createKmzFile(newScene.getName());
	}

	public void snapShot(Scenario newScene) {
		createDocument("Flooding State Level", "RainFalling Motherfuckers");
		long cont = 0;
		HexagonalGrid g = newScene.getGrid();
		for (int x=0;x<g.getDimX();x++){
			for (int y=0;y<g.getDimY();y++){
				if (g.getTerrainValue(x, y)!=oldGrid.getTerrainValue(x, y)){
					createHexagon("HEX"+cont, newScene.tileToCoord(x, y));
					cont++;
				}
				
			}
		}
		
		System.out.println("Hexagonos Creados" + cont);
		createKmzFile(newScene.getName());
	}

	private List<LatLng> regionToPoligon(SortedSet<Punto> region,
			Scenario newScene) {
		List<LatLng> borderLine = new ArrayList<LatLng>();
		// Initializating
		
		List<Punto> adyList = new ArrayList<Punto>();
		
		while (!region.isEmpty()) {
			Punto p = (Punto) (region).first();
			adyList.add(p);
			region.remove(p);
			while (!adyList.isEmpty()) {
				p = adyList.get(0);
				borderLine.add(newScene.tileToCoord(p.x, p.y));
				adyList.remove(p);
				Set<Punto> s = newScene.getAdjacents(p);
				for (Punto b : region) {
					// could be more than one each time
					if (s.contains(b)) {
						borderLine.add(newScene.tileToCoord(b.x, b.y));
						adyList.add(b);
					}
				}
				for (Punto r : adyList){
					region.remove(r);
				}
			}
			
		}

		return borderLine;
	}

	private boolean addBorderToRegion(List<SortedSet<Punto>> regions,
			Set<Punto> adyacents, Punto border) {
		for (Set<Punto> region : regions) {
			for (Punto adyacent : adyacents) {
				if (region.contains(adyacent)) {
					region.add(border);
					// System.out.print(", Borde añadido");
					return true;
				}
			}
		}
		return false;
	}

	private Collection<List<SortedSet<Punto>>> getBorderRegions(
			Scenario newScene) {
		HashMap<Short, List<SortedSet<Punto>>> levelRegions = new HashMap<Short, List<SortedSet<Punto>>>();
		HexagonalGrid newGrid = newScene.getGrid();
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(x, y);
				// si son diferentes
				Punto p = new Punto(x, y, altitude);
				if (oldGrid.getTerrainValue(x, y) != altitude) {
					// System.out.print("!=, ");
					if (newScene.isBorderPoint(p)) {
						// Solo añadimos los bordes
						// System.out.print(", Borde");
						// System.out.print("(" + x + "," + y + ") Region :" +
						// altitude);
						List<SortedSet<Punto>> regions = levelRegions
								.get(altitude);
						if (regions == null) {
							// si no existe la region la creamos y añadimos el
							// borde
							// System.out.print(", Nueva Lista Regiones Creada");
							regions = new ArrayList<SortedSet<Punto>>();
							SortedSet<Punto> region = new TreeSet<Punto>();
							region.add(p);
							regions.add(region);
							levelRegions.put(altitude, regions);
						} else {
							if (!addBorderToRegion(regions, newScene
									.getAdjacents(p), p)) {
								// si no pertenece a una region creada, creamos
								// una nueva region
								// System.out.print(", Nueva Lista Region Creada");
								SortedSet<Punto> region = new TreeSet<Punto>();
								region.add(p);
								regions.add(region);
							}
						}
					} else {
						// System.out.print(", no es un borde");
					}
					// System.out.println();
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
