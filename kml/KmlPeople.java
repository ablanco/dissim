package kml;

import java.util.List;

import util.Pedestrian;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;
import de.micromata.opengis.kml.v_2_2_0.IconStyle;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class KmlPeople {

	private final String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";

	private Folder folder;
	private double[] incs;

	public KmlPeople(Folder folder, double[] incs) {
		this.folder = folder;
		this.incs = incs;
	}

	public void update(List<Pedestrian> people, String begintime,
			String endime) {
		for (Pedestrian person : people) {
			drawPeople(person.getStatus(), person.getPos());
		}
	}

	@SuppressWarnings("unused")
	private void createStyles(Kml kml) {

		// Healthy Stile
		Style styleHealthy = folder.createAndAddStyle().withId(
				String.valueOf(Pedestrian.Healthy));
		IconStyle iconStyleHealthy = styleHealthy.createAndSetIconStyle()
				.withColorMode(ColorMode.NORMAL);
		iconStyleHealthy.createAndSetIcon().withHref(imgPath + "healthy.png");

		// Hurt Stile
		Style styleHurt = folder.createAndAddStyle().withId(
				String.valueOf(Pedestrian.Hurt));
		IconStyle iconStyleHurt = styleHurt.createAndSetIconStyle()
				.withColorMode(ColorMode.NORMAL);
		iconStyleHurt.createAndSetIcon().withHref(imgPath + "hurt.png");

		// Death Stile
		Style styledead = folder.createAndAddStyle().withId(
				String.valueOf(Pedestrian.Dead));
		IconStyle iconStyledead = styledead.createAndSetIconStyle()
				.withColorMode(ColorMode.NORMAL);
		iconStyledead.createAndSetIcon().withHref(imgPath + "dead.png");
	}

	public void newPedestrian(Pedestrian pedestrian, String begin,
			String end) {
		GroundOverlay groundoverlay = folder.createAndAddGroundOverlay()
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
		groundoverlay.createAndSetTimeSpan().withBegin(begin).withEnd(end);

		switch (pedestrian.getStatus()) {
		case Pedestrian.Healthy:
			groundoverlay.createAndSetIcon().withHref(imgPath + "healthy.png");
			break;
		case Pedestrian.Hurt:
			groundoverlay.createAndSetIcon().withHref(imgPath + "hurt.png");
			break;
		case Pedestrian.Dead:
			groundoverlay.createAndSetIcon().withHref(imgPath + "dead.png");
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

	public void drawPeople(int key, LatLng coord) {
		folder.createAndAddPlacemark().withName("Pedestrian").withStyleUrl(
				String.valueOf(key)).createAndSetPoint().addToCoordinates(
				coord.toKmlString());
	}

}
