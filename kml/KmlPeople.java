package kml;

import java.util.List;

import util.Pedestrian;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.RefreshMode;
import de.micromata.opengis.kml.v_2_2_0.ViewRefreshMode;

public class KmlPeople {

	public final static String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";
	private Folder container;
	private Folder folder;

	public KmlPeople(Folder folder) {
		this.container = folder;
	}

	public void update(List<Pedestrian> people, String name,String beginTime,
			String endTime, double[] incs) {
		folder = container.createAndAddFolder().withName(name).withDescription(
				"From: " + beginTime + " To :" + endTime);
		for (Pedestrian pedestrian : people) {
			drawPedestrian(pedestrian, beginTime, endTime, incs);
		}
	}

	public void drawPedestrian(Pedestrian pedestrian, String begin, String end,
			double[] incs) {
		GroundOverlay groundoverlay = folder.createAndAddGroundOverlay()
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
		KmlBase.setTimeSpan(groundoverlay, begin, end);
		// Depending status set propper img
		Icon icon = groundoverlay.createAndSetIcon().withRefreshInterval(10.0).withViewBoundScale(1.0).withViewRefreshTime(10.0).withRefreshMode(RefreshMode.ON_INTERVAL).withViewRefreshMode(ViewRefreshMode.ON_STOP);
		switch (pedestrian.getStatus()) {
		case Pedestrian.HEALTHY:
			icon.withHref(imgPath + "healthy.png");
			break;
		case Pedestrian.HURT:
			icon.withHref(imgPath + "hurt.png");
			break;
		case Pedestrian.DEAD:
			icon.withHref(imgPath + "dead.png");
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
