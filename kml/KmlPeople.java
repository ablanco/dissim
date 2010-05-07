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

public class KmlPeople {

	public final static String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";
	private final String peopleId = "pedestrian"; 
	private Folder container;
	private Folder folder;
	private String begin;
	private String end;

	public KmlPeople(Folder folder) {
		this.container = folder;
		createPeopleStyle();
	}

	protected void createPeopleStyle() {		
		String abgr = "ff" + "00" + "00" + "ff";		
		container.createAndAddStyle().withId(peopleId)
				.createAndSetPolyStyle().withColor(abgr);
		// polyStyle.setColorMode(ColorMode.NORMAL);
	}
	
	public void update(List<Pedestrian> people, String name,String beginTime,
			String endTime, double[] incs) {
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		this.begin=beginTime;
		this.end=endTime;
		double ilat = incs[0] * 4 / 6;
		double ilng = incs[1] / 2;
		for (Pedestrian pedestrian : people) {
		ArrayList<LatLng> rawPolygon = new ArrayList<LatLng>();
		rawPolygon.add(pedestrian.getPos());
			drawPedestrian(new Kpolygon(Kpolygon.Pedestrian, rawPolygon, ilat, ilng));
		}
	}

//	public void drawPedestrian(Pedestrian pedestrian, String begin, String end,
//			double[] incs) {
//		GroundOverlay groundoverlay = folder.createAndAddGroundOverlay()
//				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
//		KmlBase.setTimeSpan(groundoverlay, begin, end);
//		// Depending status set propper img
//		Icon icon = groundoverlay.createAndSetIcon().withRefreshInterval(10.0).withViewBoundScale(1.0).withViewRefreshTime(10.0).withRefreshMode(RefreshMode.ON_INTERVAL).withViewRefreshMode(ViewRefreshMode.ON_STOP);
//		switch (pedestrian.getStatus()) {
//		case Pedestrian.HEALTHY:
//			icon.withHref(imgPath + "healthy.png");
//			break;
//		case Pedestrian.HURT:
//			icon.withHref(imgPath + "hurt.png");
//			break;
//		case Pedestrian.DEAD:
//			icon.withHref(imgPath + "dead.png");
//			break;
//		default:
//			break;
//		}
//		// Setting LatLonBox
//		LatLng pos = pedestrian.getPos();
//		groundoverlay.createAndSetLatLonBox().withNorth(pos.getLat() + incs[0])
//				.withSouth(pos.getLat() - incs[0]).withEast(
//						pos.getLng() + incs[1])
//				.withWest(pos.getLng() - incs[1]).withRotation(45d);
//		
//	}

	/**
	 * Dibuja un poligno que representa a una persona
	 */
	public void drawPedestrian(Kpolygon kp) {
		if (kp == null) {
			throw new IllegalArgumentException("El polygono no puede ser nulo");
		}
		Placemark placeMark = KmlBase.newPlaceMark(folder, String.valueOf(kp
				.getDeep()));
		KmlBase.setTimeSpan(placeMark, begin, end);
		placeMark.setStyleUrl(peopleId);
		KmlBase.drawPolygon(placeMark, kp);
		
	}
}
