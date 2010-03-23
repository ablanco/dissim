package kml;

import java.util.List;

import util.flood.FloodPedestrian;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.IconStyle;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class KmlPeople {

	private final String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";

	private Folder folder;

	public KmlPeople(Folder folder) {
		this.folder = folder;
		createStyles();
	}

	public void update(List<FloodPedestrian> people){
			for (FloodPedestrian person: people){				
				drawPeople(person.getStatus() , person.getPos());
		}
	}
	
	private void createStyles(){
		//Helty Stile
		Style styleHealthy = folder.createAndAddStyle().withId(String.valueOf(FloodPedestrian.Healthy));
		IconStyle iconStyleHealthy = styleHealthy.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyleHealthy.createAndSetIcon().withHref(imgPath+"healthy.png");
		
		//Hurt Stile
		Style styleHurt = folder.createAndAddStyle().withId(String.valueOf(FloodPedestrian.Hurt));
		IconStyle iconStyleHurt = styleHurt.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyleHurt.createAndSetIcon().withHref(imgPath+"hurt.png");
		
		//Death Stile
		Style styledead = folder.createAndAddStyle().withId(String.valueOf(FloodPedestrian.Dead));
		IconStyle iconStyledead = styledead.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyledead.createAndSetIcon().withHref(imgPath+"dead.png");
	}

	public void drawPeople(int key, LatLng coord) {
		folder.createAndAddPlacemark().withName("Pedestrian").withStyleUrl(String.valueOf(key))
		.createAndSetPoint().addToCoordinates(coord.toKmlString());
	}

}
