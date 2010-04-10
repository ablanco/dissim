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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import util.Point;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;
import util.jcoord.LatLngComparator;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class KmlFlood {
	/**
	 * Here are all floodLevels
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
	 * Kml Folder where put all inundation info
	 */
	private Folder container;
	private Folder folder;

	public KmlFlood(Folder folder) {
		altitudes = new TreeSet<Short>();
		this.container = folder;
	}

	public void update(short[][] oldGrid, FloodHexagonalGrid fGrid,
			String name, String beginTime, String endTime) {
		// incs for this snapshot
		double[] incs = fGrid.getIncs();
		double ilat = incs[0]*3/5;
		double ilng = incs[1]/2;
		// int tileSize = fGrid.getTileSize();
		// Now we update the time for each update call
		this.endTime = endTime;
		this.beginTime = beginTime;
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		// For each tile who has changed ever, creates hexagon
		int offCol = fGrid.getOffX();
		int offRow = fGrid.getOffY();
		Collection<Point> flooded = new TreeSet<Point>();
		// Collection<Point> flooded = new SortedSet<Point>();
		for (int col = 0; col < fGrid.getColumns(); col++) {
			for (int row = 0; row < fGrid.getRows(); row++) {
				int c = col + offCol;
				int r = row + offRow;
				if (fGrid.getWaterValue(c, r) > 0) {
					flooded.add(new Point(c, r, fGrid.getValue(c, r)));
				}
			}
		}
		System.err.println("Sectores inundados "+flooded);
		// Mientras tengamos casillas inundadas
		while (!flooded.isEmpty()) {
			// Obtenemos el primer punto
			boolean adyacents = true;
			// Vamos rellenando el primer sector
			Collection<Point> sector = new TreeSet<Point>();
			while (adyacents) {
				// Mientras tenga un nuevo adyacente
				adyacents = false;
				Iterator<Point> it = flooded.iterator();
				if (sector.isEmpty() && !flooded.isEmpty()) {
					// Significa que es un nuevo sector
					sector.add(it.next());
					it.remove();
				}
				while (it.hasNext()) {
					// Mientras nos queden casillas por visitar
					Point p = it.next();
					for (Point pp : sector){	
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
			System.err.println("Sector inundado, puntos "+sector);
			for (Point p : sector) {
				deep += p.getZ();
				vertices.add(fGrid.tileToCoord(p));
			}
			deep = (short) (deep / sector.size());

			Kpolygon kp = new Kpolygon(Kpolygon.WaterType, vertices, ilat, ilng);
			kp.setDeep(deep);
			System.err.println("Poligono Dibujado");

			if (altitudes.add(deep)) {
				createWaterStyleAndColor(deep);
			}

			drawWater(kp);
		}

	}

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
	 * Creates an style for level floodLevel but with deep z
	 * 
	 * @param floodLevel
	 *            Level
	 * @param z
	 *            deep
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
