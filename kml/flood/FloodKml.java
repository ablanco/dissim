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
import util.Point;
import util.Scenario;
import util.Updateable;
import util.jcoord.LatLng;

public class FloodKml extends KmlWriter implements Updateable {
	private long cont = 0;

	
	public FloodKml() {
		super();
		openFolder("Flooding", "All these sectors are flooded");
	}

	public void update(Object obj) {
		if (!(obj instanceof Scenario))
			throw new IllegalArgumentException(
					"Object is not an instance of Scenario");
		Scenario newScene = (Scenario) obj;
		beginTime = newScene.getDateAndTime().toString();
		newScene.updateTime();
		endTime = newScene.getDateAndTime().toString();
		
		HexagonalGrid g = newScene.getGrid();

		for (int x = 0; x < g.getDimX(); x++) {
			for (int y = 0; y < g.getDimY(); y++) {
				if (g.getTerrainValue(x, y) != oldGrid.getTerrainValue(x, y)) {
					createHexagon("HEX" + cont, newScene.tileToCoord(x, y));
					cont++;
				}

			}
		}
		System.out.println("Hexagonos Creados" + cont);
	}

	public void snapShot2(Scenario newScene) {
		openFolder("Flooding State Level", "RainFalling Motherfuckers");
		long cont = 0;
		for (List<SortedSet<Point>> regions : getBorderRegions(newScene)) {
			for (SortedSet<Point> region : regions) {
				// TODO region podría no estar ordenados y salir cosas raras
				// Collections.sort(region);
				createPolygon("Pol" + cont, regionToPoligon(region, newScene));
				cont++;
				if (region.size() > 10) {
					for (Point c : region) {
						System.out.print("[" + c.x + "," + c.y + "] ");
					}
					System.out.println();
				}
			}
		}
		System.out.println("Regiones detectadas" + cont);
		createKmzFile(newScene.getName());
	}

	private List<LatLng> regionToPoligon(SortedSet<Point> region,
			Scenario newScene) {
		List<LatLng> borderLine = new ArrayList<LatLng>();
		// Initializating

		List<Point> adyList = new ArrayList<Point>();

		while (!region.isEmpty()) {
			Point p = (Point) (region).first();
			adyList.add(p);
			region.remove(p);
			while (!adyList.isEmpty()) {
				p = adyList.get(0);
				borderLine.add(newScene.tileToCoord(p.x, p.y));
				adyList.remove(p);
				Set<Point> s = newScene.getAdjacents(p);
				for (Point b : region) {
					// could be more than one each time
					if (s.contains(b)) {
						borderLine.add(newScene.tileToCoord(b.x, b.y));
						adyList.add(b);
					}
				}
				for (Point r : adyList) {
					region.remove(r);
				}
			}

		}

		return borderLine;
	}

	private boolean addBorderToRegion(List<SortedSet<Point>> regions,
			Set<Point> adyacents, Point border) {
		for (Set<Point> region : regions) {
			for (Point adyacent : adyacents) {
				if (region.contains(adyacent)) {
					region.add(border);
					// System.out.print(", Borde añadido");
					return true;
				}
			}
		}
		return false;
	}

	private Collection<List<SortedSet<Point>>> getBorderRegions(
			Scenario newScene) {
		HashMap<Short, List<SortedSet<Point>>> levelRegions = new HashMap<Short, List<SortedSet<Point>>>();
		HexagonalGrid newGrid = newScene.getGrid();
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(x, y);
				// si son diferentes
				Point p = new Point(x, y, altitude);
				if (oldGrid.getTerrainValue(x, y) != altitude) {
					// System.out.print("!=, ");
					if (newScene.isBorderPoint(p)) {
						// Solo añadimos los bordes
						// System.out.print(", Borde");
						// System.out.print("(" + x + "," + y + ") Region :" +
						// altitude);
						List<SortedSet<Point>> regions = levelRegions
								.get(altitude);
						if (regions == null) {
							// si no existe la region la creamos y añadimos el
							// borde
							// System.out.print(", Nueva Lista Regiones Creada");
							regions = new ArrayList<SortedSet<Point>>();
							SortedSet<Point> region = new TreeSet<Point>();
							region.add(p);
							regions.add(region);
							levelRegions.put(altitude, regions);
						} else {
							if (!addBorderToRegion(regions, newScene
									.getAdjacents(p), p)) {
								// si no pertenece a una region creada, creamos
								// una nueva region
								// System.out.print(", Nueva Lista Region Creada");
								SortedSet<Point> region = new TreeSet<Point>();
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
