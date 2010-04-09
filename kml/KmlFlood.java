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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
		// int tileSize = fGrid.getTileSize();
		// Now we update the time for each update call
		this.endTime = endTime;
		this.beginTime = beginTime;
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		TreeMap<Short, SortedSet<LatLng>> floodSectors = new TreeMap<Short, SortedSet<LatLng>>();
		// For each tile who has changed ever, creates hexagon
		int offCol = fGrid.getOffX();
		int offRow = fGrid.getOffY();
		SortedSet<LatLng> sector;
		for (int col = 0; col < fGrid.getColumns(); col++) {
			for (int row = 0; row < fGrid.getRows(); row++) {
				int c = col + offCol;
				int r = row + offRow;
				short floodLevel = -1;
				floodLevel = fGrid.getWaterValue(c, r);

				if (floodLevel > 0) {
					short key = fGrid.getValue(c, r);
					// Solo dibujamos aquellos poligonos que estan inundados
					if (altitudes.add(floodLevel)) {
						// Si no tenemos esta altitud añadimos un nuevo estilo
						// con la profundidad solo de este punto
						createWaterStyleAndColor(key, floodLevel);
					}
					// Nos interesan las zonas inundadas que estan al mismo
					// nivel

					sector = floodSectors.get(key);
					if (sector == null) {
						sector = new TreeSet<LatLng>(new LatLngComparator());
						floodSectors.put(key, sector);
					}
					// Añadimos la casilla en su nivel correspondiente
					sector.add(fGrid.tileToCoord(c, r));
				}
			}
		}

		for (short key : floodSectors.keySet()) {
			SortedSet<LatLng> sectors = floodSectors.get(key);
			// Por cada lista de puntos al mismo nivel, identifico los
			// diferentes poligonos
			List<Kpolygon> Kpolygons = separatePolygons(sectors, fGrid
					.getTileSize() * 1.1, fGrid.getIncs());
			// Una vez que tengo los poligonos, los pinto en el kml
			for (Kpolygon kpolygon : Kpolygons) {
				drawWater(kpolygon, key, incs);
			}

		}

	}

	/**
	 * Este metodo recibe una lista de vertices que estan al mismo nivel, su
	 * mision es separarlos en conjuntos de vertices adyacentes y crear tantos
	 * poligonos como conjuntos haya
	 * 
	 * @param sectors
	 * @param d
	 * @return
	 */
	private List<Kpolygon> separatePolygons(SortedSet<LatLng> sectors,
			double d, double[] incs) {
		List<Kpolygon> kpolygons = new ArrayList<Kpolygon>();
		while (!sectors.isEmpty()) {
			// Cada vez creamos una lista con el primer elemento
			SortedSet<LatLng> sector = new TreeSet<LatLng>(new LatLngComparator());
			LatLng aux = sectors.first();
			// Guardamos el primer elemento
			sector.add(aux);
			// Lo borramos de la lista
			sectors.remove(aux);
			boolean adyacentes = false;
			// Mientras nos queden coordenadas
			while (!adyacentes) {
				System.err.println("Buscando adyacentes");
				// No hay adyacentes a esta coordenada, es un punto aislado
				adyacentes = true;
				// TODO mejorar la eficiencia
				// Por cada coordenada que quede
				for (LatLng c1 : sectors) {
					// Cogemos la lista de los que son menores, nos quedamos con
					// el ultimo y comparamos
					LatLng c2 = sector.headSet(c1).last();
					// En caso de ser adyacente, la añadimos y dejamos de
					// mirar
					if (Math.abs(c1.distance(c2)) <= d) {
						System.err.println(c1 + " Es adyacente a " + c2);
						adyacentes = false;
						sector.add(c1);
					}
				}
				// Borramos todas las coordenadas adyacentes de la lista
				if (!sectors.removeAll(sector)) {
					throw new IllegalArgumentException(
							"El remove no ha funcionado");
				}
			}
			// Añadimos a la lista de poligonos el correspondiente a la lista de
			// adyacentes
			System.err.println("Dibujando poligono " + sector);
			kpolygons.add(new Kpolygon(Kpolygon.WaterType, sector, incs));
		}
		return kpolygons;
	}

	private void drawWater(Kpolygon kp, int floodLevel, double[] incs) {
		if (kp == null) {
			throw new IllegalArgumentException("El polygono no puede ser nulo");
		}
		Placemark placeMark = KmlBase.newPlaceMark(folder, String
				.valueOf(floodLevel));
		KmlBase.setTimeSpan(placeMark, beginTime, endTime);
		placeMark.setStyleUrl(water + floodLevel);
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
	protected void createWaterStyleAndColor(short floodLevel, short z) {
		// Le damos un color medio para que se parezca a agua
		int blue = 125;
		// Mientras mas profunda sea el agua, mas ocuro es el azul.
		blue += z;
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
