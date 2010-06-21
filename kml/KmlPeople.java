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

import java.util.Collection;

import kml.KmlBase.Pedestrians;
import util.Pedestrian;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

/**
 * This class manages all the information of pedestrians and generates a kml
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class KmlPeople {

	/**
	 * Means the pedestrian is alive
	 */
	public final static String RUNNING = "running";
	/**
	 * Means the pedestrian is at a safepoint
	 */
	public final static String SAFE = "safe";
	/**
	 * Means the pedestrian is dead
	 */
	public final static String DEAD = "dead";
	private Folder container;
	private Folder folder;
	private String begin;
	private String end;

	/**
	 * Builds and initializes parameters, needs a folder for showing information
	 * in an ordered way
	 * 
	 * @param folder
	 *            root folder
	 */
	public KmlPeople(Folder folder) {
		this.container = folder;
		createPeopleStyle();
	}

	/**
	 * Creates, and sets to the container, a style for pedestrians. By default:
	 * RUNNING, SAFE, DEAD
	 */
	protected void createPeopleStyle() {
		// Verde
		String safeABGR = "ff" + "00" + "ff" + "00";
		// Gris
		String deadABGR = "ff" + "aa" + "aa" + "aa";
		// Amarillo
		String runningABGR = "ff" + "00" + "ff" + "f0";
		container.createAndAddStyle().withId(RUNNING).createAndSetPolyStyle()
				.withColor(runningABGR);
		container.createAndAddStyle().withId(SAFE).createAndSetPolyStyle()
				.withColor(safeABGR);
		container.createAndAddStyle().withId(DEAD).createAndSetPolyStyle()
				.withColor(deadABGR);
		// polyStyle.setColorMode(ColorMode.NORMAL);
	}

	/**
	 * Update method for each new snapshot from the simulation
	 * 
	 * @param people
	 *            list of pedestrians
	 * @param name
	 *            name of the environment
	 * @param beginTime
	 *            time of the simulation
	 * @param endTime
	 *            time of the simulation
	 * @param incs
	 *            of the enviroment
	 */
	public void update(Collection<Pedestrians> people, String name,
			String beginTime, String endTime, double[] incs) {
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		this.begin = beginTime;
		this.end = endTime;
		double ilat = incs[0] * 4 / 6;
		double ilng = incs[1] / 2;
		for (Pedestrians p : people) {
			// Por cada uno de ellos, creamos un poligono
			drawPedestrian(new Kpolygon(Kpolygon.Pedestrian, p.getList(), ilat,
					ilng), p.status, p.amount);
		}
	}

	/**
	 * This class draws a pedestrian in the given position
	 * 
	 * @param kp
	 *            Polygon that represents the pedestrian at position
	 * @param status
	 *            of the pedestrian
	 * @param contador
	 * @throws IllegalArgumentException
	 *             if the polygon is null
	 */
	public void drawPedestrian(Kpolygon kp, int status, int contador) {
		if (kp == null) {
			throw new IllegalArgumentException("El polygono no puede ser nulo");
		}
		// Notese que la altura depende de la gente que haya
		Placemark placeMark = KmlBase.newPlaceMark(folder, String.valueOf(kp
				.getDeep()));
		// Le damos un intervalo de tiempo
		KmlBase.setTimeSpan(placeMark, begin, end);
		// Le añadimos informacion extra
		placeMark.createAndSetExtendedData().createAndAddData(
				"Hay " + contador + " personas en esta casilla");
		// Le añadimos un estado
		switch (status) {
		case Pedestrian.DEAD:
			placeMark.setStyleUrl(DEAD);
			break;
		case Pedestrian.SAFE:
			placeMark.setStyleUrl(SAFE);
			break;
		case Pedestrian.HEALTHY:
			placeMark.setStyleUrl(RUNNING);
			break;
		default:
			break;
		}
		// Dibujamos el poligono
		KmlBase.drawPolygon(placeMark, kp);
	}
}
