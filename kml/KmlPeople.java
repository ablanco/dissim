package kml;

import java.util.List;

import util.Pedestrian;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;

public class KmlPeople {

	
	private Folder folder;
	private double[] incs;

	public KmlPeople(Folder folder, double[] incs) {
		this.folder = folder;
		this.incs = incs;
	}

	public void update(List<Pedestrian> people, String begintime,
			String endime) {
		for (Pedestrian pedestrian : people) {
			drawPedestrian(pedestrian, begintime, endime);
		}
	}


	public void drawPedestrian(Pedestrian pedestrian, String begin,
			String end) {
		GroundOverlay groundoverlay = folder.createAndAddGroundOverlay()
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
		KmlBase.setTimeSpan(groundoverlay, begin, end);
		//Depending status set propper img
		switch (pedestrian.getStatus()) {
		case Pedestrian.HEALTHY:
			groundoverlay.createAndSetIcon().withHref(Pedestrian.imgPath + "healthy.png");
			break;
		case Pedestrian.HURT:
			groundoverlay.createAndSetIcon().withHref(Pedestrian.imgPath + "hurt.png");
			break;
		case Pedestrian.DEAD:
			groundoverlay.createAndSetIcon().withHref(Pedestrian.imgPath + "dead.png");
			break;
		default:
			break;
		}
		
		// Setting LatLonBox
		LatLng pos = pedestrian.getPos();
		groundoverlay.createAndSetLatLonBox().withNorth(pos.getLat() + incs[0])
				.withSouth(pos.getLat() - incs[0]).withEast(
						pos.getLng() + incs[1])
				.withWest(pos.getLng() - incs[1]).withRotation(45d);
	}

}
