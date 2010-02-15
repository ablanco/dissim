package kml.flood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import kml.KmlWriter;
import util.HexagonalGrid;
import util.Scenario;
import util.jcoord.LatLng;

public class FloodKml extends KmlWriter {

	public FloodKml() {
		super();
	}

	public void snapShot(Scenario newScene) {
		createDocument("Flooding State Level", "RainFalling Motherfuckers");
		long cont = 0;
		for (List<Set<LatLng>> regions : getBorderRegions(newScene)) {
			for (Set<LatLng> region : regions) {
				// TODO region podría no estar ordenados y salir cosas raras
				// Collections.sort(region);
				System.out.println("Region Detectada:" + cont + " tamaño: "
						+ region.size());
				if (region.size() == 1) {
					createHexagon((LatLng) (region.toArray())[0]);
				} else {
					createPolygon("Flood " + cont, "", region);
				}
				cont++;
				for (LatLng c : region) {
					int a[] = newScene.coordToTile(c);
					System.out.print("[" + a[0] + "," + a[1] + "] ");
				}
				System.out.println();
			}

		}

		createKmlFile(newScene.getName());
	}

	private boolean isBorderRegion(Set<LatLng> adyacents, short altitude) {
		// es borde si los adyacentes tienen distinto valor
		for (LatLng c : adyacents) {
			if (c.getAltitude() != altitude) {
				return true;
			}
		}
		return false;
	}

	private boolean addBorderToRegion(List<Set<LatLng>> regions,
			Set<LatLng> adyacents, LatLng border) {
		for (Set<LatLng> region : regions) {
			for (LatLng adyacent : adyacents) {
				if (region.contains(adyacent)) {
					region.add(border);
					return true;
				}
			}
		}
		return false;
	}

	private Collection<List<Set<LatLng>>> getBorderRegions(Scenario newScene) {
		HashMap<Short, List<Set<LatLng>>> levelRegions = new HashMap<Short, List<Set<LatLng>>>();
		HexagonalGrid newGrid = newScene.getGrid();
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(i, j);
				// System.out.print("(" + i + "," + j + ") Region :" +
				// altitude);
				// si son diferentes
				if (oldGrid.getTerrainValue(i, j) != altitude) {
					// System.out.print(", !=");
					LatLng coord = newScene.tileToCoord(i, j);
					if (isBorderRegion(newScene.getAdjacents(coord), altitude)) {
						// Solo añadimos los bordes
						// System.out.print(", Borde");
						List<Set<LatLng>> regions = levelRegions.get(altitude);
						if (regions == null) {
							// si no exixte la region la creamos y añadimos el
							// borde
							// System.out.print(", Nueva Lista Regiones Creada");
							regions = new ArrayList<Set<LatLng>>();
							Set<LatLng> region = new TreeSet<LatLng>();
							region.add(coord);
							regions.add(region);
							levelRegions.put(altitude, regions);
						} else {
							if (!addBorderToRegion(regions, newScene
									.getAdjacents(coord), coord)) {
								// si no pertenece a una region creada, creamos
								// una nueva region
								// System.out.print(", Nueva Lista Regiones Creada");
								Set<LatLng> region = new TreeSet<LatLng>();
								region.add(coord);
								regions.add(region);

							}
						}
					} else {
						// System.out.print(", no es un borde");
					}
				} else {
					// System.out.print(", no son distintos");
				}
				// System.out.println();
			}
		}
		return levelRegions.values();
	}

	public HexagonalGrid getOldGrid() {
		return oldGrid;
	}

}
