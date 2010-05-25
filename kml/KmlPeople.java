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

import util.Pedestrian;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

/**
 * This class manage all the information from pedestrians in updates and
 * generate a kml
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class KmlPeople {

	public final static String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";
	public final static String RUNNING = "running";
	public final static String SAFE = "safe";
	public final static String DEAD = "dead";
	private Folder container;
	private Folder folder;
	private String begin;
	private String end;

	/**
	 * Build and initializes parameters, needs a folder for showing information
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
	 * Creates and set to container an style for pedestrians, by default
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
	 *            name of the enviroment
	 * @param beginTime
	 *            time of the simulation
	 * @param endTime
	 *            time of the simulation
	 * @param incs
	 *            to de enviroment
	 */
	public void update(List<Pedestrian> people, String name, String beginTime,
			String endTime, double[] incs) {
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		this.begin = beginTime;
		this.end = endTime;
		double ilat = incs[0] * 4 / 6;
		double ilng = incs[1] / 2;
		for (Pedestrian pedestrian : people) {
			ArrayList<LatLng> rawPolygon = new ArrayList<LatLng>();
			rawPolygon.add(pedestrian.getPos());
			drawPedestrian(new Kpolygon(Kpolygon.Pedestrian, rawPolygon, ilat,
					ilng), pedestrian.getStatus());
		}
	}

	/**
	 * This class drwas a pedestrian in the given position
	 * @param kp polygon that represents the pedestrian and position 
	 * @param status status of the pedestrian
	 * @throws IllegalArgumentException si el poligono es nulo
	 */
	public void drawPedestrian(Kpolygon kp, int status) {
		if (kp == null) {
			throw new IllegalArgumentException("El polygono no puede ser nulo");
		}
		Placemark placeMark = KmlBase.newPlaceMark(folder, String.valueOf(kp
				.getDeep()));
		KmlBase.setTimeSpan(placeMark, begin, end);
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
		KmlBase.drawPolygon(placeMark, kp);
	}
}
