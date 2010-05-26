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

package kml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import util.Point;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

/**
 * This class manages the flood updates and generates a kml flood
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class KmlFlood {
	/**
	 * Here are all the flood levels
	 */
	private Set<Short> altitudes;
	/**
	 * Begin time of the simulation step
	 */
	protected String beginTime = null;
	/**
	 * End time of the simulation step
	 */
	protected String endTime = null;
	// no poner : que se ralla :D
	protected final String water = "Water";
	/**
	 * Kml {@link Folder} where put all the flood info
	 */
	private Folder container;
	private Folder folder;

	/**
	 * Builds and initializes parameters, needs a folder for showing information
	 * in an ordered way
	 * 
	 * @param folder
	 *            root folder
	 */
	public KmlFlood(Folder folder) {
		altitudes = new TreeSet<Short>();
		this.container = folder;
	}

	/**
	 * For each new call, it sees which tiles have changed and updates de kml
	 * 
	 * @param oldGrid
	 *            previous grid
	 * @param fGrid
	 *            new grid
	 * @param name
	 *            of the grid
	 * @param beginTime
	 *            when begins
	 * @param endTime
	 *            when ends
	 */
	public void update(short[][] oldGrid, FloodHexagonalGrid fGrid,
			String name, String beginTime, String endTime) {
		// incs for this snapshot
		double[] incs = fGrid.getIncs();
		double ilat = incs[0] * 4 / 6;
		double ilng = incs[1] / 2;
		short precision = fGrid.getPrecision();
		// int tileSize = fGrid.getTileSize();
		// Now we update the time for each update call
		this.endTime = endTime;
		this.beginTime = beginTime;
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		// For each tile who has changed ever, creates hexagon
		int offCol = fGrid.getOffCol();
		int offRow = fGrid.getOffRow();
		Map<Short, Collection<Point>> floods = new TreeMap<Short, Collection<Point>>();
		Collection<Point> flooded;
		// Collection<Point> flooded = new SortedSet<Point>();
		// Para toda la matriz
		for (int col = 0; col < fGrid.getColumns(); col++) {
			for (int row = 0; row < fGrid.getRows(); row++) {
				int c = col + offCol;
				int r = row + offRow;
				if (fGrid.getWaterValue(c, r) > 0) {
					// Nos fijamos solo en los sectores inundados
					short key = fGrid.getValue(c, r);
					flooded = floods.get(key);
					// Los ordenamos por Niveles, para reducir la longitud de
					// las listas lo maximo posible
					if (flooded == null) {
						flooded = new TreeSet<Point>();
						floods.put(key, flooded);
					}
					flooded.add(new Point(c, r, fGrid.getValue(c, r)));
				}
			}
		}
		// Mientras tengamos casillas inundadas
		for (short key : floods.keySet()) {
			// Vamos iterando sobre este sector para obtener los adyacentes
			Collection<Point> waterTiles = floods.get(key);
			while (!waterTiles.isEmpty()) {
				// Obtenemos el primer punto
				boolean adyacents = true;
				// Vamos rellenando el primer sector
				Collection<Point> sector = new TreeSet<Point>();
				while (adyacents) {
					// Mientras tenga un nuevo adyacente
					adyacents = false;
					Iterator<Point> it = waterTiles.iterator();
					if (sector.isEmpty() && !waterTiles.isEmpty()) {
						// Significa que es un nuevo sector
						sector.add(it.next());
						it.remove();
					}
					while (it.hasNext()) {
						// Mientras nos queden casillas por visitar
						Point p = it.next();
						for (Point pp : sector) {
							// Miro si es adyacente a alguno de los que ya se
							// que son adyacentes
							if (pp.isAdyacent(p)) {
								sector.add(p);
								it.remove();
								adyacents = true;
								break;
							}
						}
					}
				}
				// Creamos una lista de vertices
				List<LatLng> vertices = new ArrayList<LatLng>();
				short deep = 0;
				// Aprobechamos para sacar la altura media
				// System.err.println("Sector inundado, puntos " + sector);
				for (Point p : sector) {

					// Tenemos que obtener las coordenadas, pero pasando a
					// altura real
					LatLng c = fGrid.tileToCoord(p);
					double altitude = Scenario.innerToDouble(precision, p
							.getZ());
					deep += altitude;
					c.setAltitude(altitude);
					vertices.add(c);
				}
				// La media de las alturas, esto es para la opacidad
				deep = (short) (deep / sector.size());

				Kpolygon kp = new Kpolygon(Kpolygon.WaterType, vertices, ilat,
						ilng);
				kp.setDeep(deep);
				// System.err.println("Poligono Dibujado");

				if (altitudes.add(deep)) {
					createWaterStyleAndColor(deep);
				}
				drawWater(kp);
			}
		}

	}

	/**
	 * Draws a polygon into the kml
	 * 
	 * @param kp
	 *            Polygon we want to draw
	 * @throws IllegalArgumentException
	 *             if is a null polygon
	 */
	private void drawWater(Kpolygon kp) {
		if (kp == null) {
			throw new IllegalArgumentException("El polygono no puede ser nulo");
		}
		Placemark placeMark = KmlBase.newPlaceMark(folder, String.valueOf(kp
				.getDeep()));
		KmlBase.setTimeSpan(placeMark, beginTime, endTime);
		placeMark.setStyleUrl(water + kp.getDeep());
		KmlBase.drawPolygon(placeMark, kp);

	}

	/**
	 * Creates and adds to the container a style and a color, considering
	 * deepness of water
	 * 
	 * @param floodLevel
	 *            Deepness of water
	 */
	protected void createWaterStyleAndColor(short floodLevel) {
		// Le damos un color medio para que se parezca a agua
		int blue = 125;
		// Mientras mas profunda sea el agua, mas ocuro es el azul.
		blue += floodLevel;
		// Kml uses aabbggrr "5500" le da un color azul bonito
		String abgr;
		if (blue > 255) {
			abgr = "ff" + "ff" + "55" + "00";
		} else {
			abgr = "aa" + Integer.toHexString(blue) + "55" + "00";
		}
		// le doy el mismo color de azul que transparencia
		container.createAndAddStyle().withId(water + floodLevel)
				.createAndSetPolyStyle().withColor(abgr);
		// polyStyle.setColorMode(ColorMode.NORMAL);
	}

}
