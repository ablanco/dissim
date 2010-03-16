//    Flood and evacuation simulator using multi-agent technology
//    Copyright (C) 2010 Alejandro Blanco and Manuel Gomar
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package googleEarth;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import util.HexagonalGrid;
import util.Logger;
import util.Point;
import util.Snapshot;
import util.Updateable;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.PolyStyle;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class GoogleEarthFlood extends GoogleEarth implements Updateable {
	private long cont = 0;
	private Set<Short> altitudes;
	private boolean initialized;
	/**
	 * Copy of First Grid, we need it to see changes through time
	 */
	private short[][] oldGrid;
	/**
	 * Begin time of the simulation step
	 */
	protected String beginTime = null;
	/**
	 * End time of the simulation step
	 */
	protected String endTime = null;
	protected Logger kmlLog = new Logger();

	public GoogleEarthFlood() {
		super();
		initialized = false;
		altitudes = new TreeSet<Short>();
	}

	public GoogleEarthFlood(String name, String description) {
		super(name, description);
		initialized = false;
		altitudes = new TreeSet<Short>();
	}

	@Override
	public void init() {
		// Código de inicialización
	}

	@Override
	public void finish() {
		// Código de finalización
		//Aqui escribimos el archivo kml
		GoogleEarthUtils.createKmlFile(kml, folder.getName());
		GoogleEarthUtils.createKmzFile(kml, folder.getName());
	}

	/**
	 * This metohs geneate a snapshot of the current state of the simulation
	 * Needs the scenario has dateAndTime and updateTimeMinutes.
	 */
	@Override
	public void update(Object obj) {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		Snapshot snap = (Snapshot) obj;
		HexagonalGrid grid = snap.getGrid();
		GoogleEarthUtils.tileSize = grid.getTileSize();
		setOldGrid(grid);
		// Now we update the time for each update call
		beginTime = endTime;
		endTime = snap.getDateTime().toString();

		kmlLog.println("Simulation state at: " + endTime);

		HexagonalGrid g = snap.getGrid();
		
		//Seting name and description
		String name=snap.getName(); 
		if (name == null){
			System.err.println("Asignando nombre por defecto al escenario");
			setName("EscenarioUnamed");
		}else{
			setName(name);
			if (snap.getDescription() != null)
			setDescription(snap.getDescription());
		}
		

		// For each tile who has changed ever, creates hexagon
		for (int x = 0; x < g.getColumns(); x++) {
			for (int y = 0; y < g.getRows(); y++) {
				short z = (short) (g.getTerrainValue(x, y) - oldGrid[x][y]);
				if (z != 0) {
					drawWaterHexagon("HEX" + cont, snap.getGrid().tileToCoord(
							x, y), z);
					cont++;
				}

			}
		}
	}

	protected void setOldGrid(HexagonalGrid grid) {
		if (!initialized) {
			// First Grid to compare
			oldGrid = new short[grid.getColumns()][grid.getRows()];
			for (int x = 0; x < grid.getColumns(); x++) {
				for (int y = 0; y < grid.getRows(); y++) {
					oldGrid[x][y] = grid.getTerrainValue(x, y);
				}
			}
			initialized = true;
		}
	}

	/**
	 * New Water Polygon to the kml file
	 * 
	 * @param name
	 *            of the polygon
	 * @param borderLine
	 *            borders of the polygon
	 * @param z
	 *            amount of water over the ground
	 */
	public void drawWaterPolygon(String name, List<LatLng> borderLine, short z) {
		Placemark placeMark = GoogleEarthUtils.newPlaceMark(folder, name);
		GoogleEarthUtils.setTimeSpan(placeMark, beginTime, endTime);
		setWaterColorToPlaceMark(placeMark, z);

		Polygon polygon = GoogleEarthUtils.newPolygon(placeMark);

		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1:
			GoogleEarthUtils.drawHexagonBorders(polygon, borderLine.get(0));
			break;
		default:
			GoogleEarthUtils.drawPolygon(folder, name, borderLine);
			break;
		}
	}

	/**
	 * New Water Hexagon to the kml file
	 * 
	 * @param name
	 *            of the polygon
	 * @param borderLine
	 *            borders of the polygon
	 * @param z
	 *            amount of water over the ground
	 */
	public void drawWaterHexagon(String name, LatLng borderLine, short z) {
		Placemark placeMark = GoogleEarthUtils.newPlaceMark(folder, name);
		setWaterColorToPlaceMark(placeMark, z);
		GoogleEarthUtils.setTimeSpan(placeMark, beginTime, endTime);

		Polygon polygon = GoogleEarthUtils.newPolygon(placeMark);

		GoogleEarthUtils.drawHexagonBorders(polygon, borderLine);
	}

	/**
	 * Given an scenario and a SortedSet<Point> containin borders of a region of
	 * equal height returns a List of LatLng in the right order to be printed
	 * 
	 * @param region
	 * @param newSnap
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<LatLng> regionToPoligon(SortedSet<Point> region,
			Snapshot newSnap) {
		List<LatLng> borderLine = new ArrayList<LatLng>();
		// Initializating

		List<Point> adyList = new ArrayList<Point>();

		while (!region.isEmpty()) {
			Point p = (Point) (region).first();
			adyList.add(p);
			region.remove(p);
			while (!adyList.isEmpty()) {
				p = adyList.get(0);
				borderLine.add(newSnap.getGrid().tileToCoord(p.getCol(),
						p.getRow()));
				adyList.remove(p);
				Set<Point> s = newSnap.getGrid().getAdjacents(p);
				for (Point b : region) {
					// could be more than one each time
					if (s.contains(b)) {
						borderLine.add(newSnap.getGrid().tileToCoord(
								b.getCol(), b.getRow()));
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

	/**
	 * Given a list of regions of same height, looks for the region with who has
	 * adyacents points of border
	 * 
	 * @param regions
	 * @param adyacents
	 *            of border
	 * @param border
	 *            Border we want to add to his right region
	 * @return False if has no adyacents in regions. If true means it already
	 *         have added to the right region
	 */
	private boolean addBorderToRegion(List<SortedSet<Point>> regions,
			Set<Point> adyacents, Point border) {
		for (Set<Point> region : regions) {
			for (Point adyacent : adyacents) {
				if (region.contains(adyacent)) {
					region.add(border);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Looks for changes between oldGrid y newGrid and add them into a
	 * Collection of regions.
	 * 
	 * @param newSnap
	 * @return a Collection whit all regions
	 */
	@SuppressWarnings("unused")
	private Collection<List<SortedSet<Point>>> getBorderRegions(Snapshot newSnap) {
		HashMap<Short, List<SortedSet<Point>>> levelRegions = new HashMap<Short, List<SortedSet<Point>>>();
		HexagonalGrid newGrid = newSnap.getGrid();
		// TODO mirar los extremos del array
		for (int x = 0; x < 0; x++) {
			for (int y = 0; y < 0; y++) {
				// recorremos cada punto de la matriz
				short altitude = newGrid.getTerrainValue(x, y);
				// si son diferentes
				Point p = new Point(x, y, altitude);
				if (oldGrid[x][y] != altitude) {
					// System.out.print("!=, ");
					if (newSnap.getGrid().isBorderPoint(p)) {
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
							if (!addBorderToRegion(regions, newSnap.getGrid()
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

	public short[][] getOldGrid() {
		return oldGrid;
	}

	@Override
	public String getConversationId() {
		return "kml";
	}

	protected void setWaterColorToPlaceMark(Placemark placeMark, short z) {
		// Adding to BLUE
		createWaterStyleAndColor(z);
		placeMark.setStyleUrl("BLUE" + z);
	}

	protected void createWaterStyleAndColor(short z) {
		if (!altitudes.contains(z)) {
			//Si no tenemos este color/altura definido
			Style style = new Style();
			folder.getStyleSelector().add(style);
			style.setId("BLUE" + z);
			//Creamos un nuevo estilo con ese color/altura
			PolyStyle polyStyle = new PolyStyle();
			style.setPolyStyle(polyStyle);
			Color c = new Color(Color.blue.getRGB());
			//Mientras mas profunda sea el agua, mas ocuro es el azul.
			for (int i=0;i<z;i++){
				c.darker();
			}
			//Para las transparecias igual, con un minimo de 128, esto tiene un maximo de 30 metros de profundidad
			if((z*4+128) < 255){
				polyStyle.setColor(Integer.toHexString(z*4 + 128) + Integer.toHexString(c.getRGB()));	
			}else{
				polyStyle.setColor("ff" + Integer.toHexString(c.getRGB()));
			}
			
			polyStyle.setColorMode(ColorMode.NORMAL);
			altitudes.add(z);
		}
	}

}
