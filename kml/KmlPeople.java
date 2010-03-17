package kml;

import util.HexagonalGrid;
import util.Point;
import util.Snapshot;
import util.Updateable;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.IconStyle;
import de.micromata.opengis.kml.v_2_2_0.Style;

public class KmlPeople implements Updateable {

	private final String imgPath = "http://pfc.mensab.com/wp-content/uploads/2010/03/";
	public final static int Healthy = 0;
	public final static int Hurt = 1;
	public final static int Dead = 2;
	private Folder folder;

	public KmlPeople(KmlBase base) {
		folder = base.getFolder().createAndAddFolder().withName("People");
		createStyles();
	}

	@Override
	public void finish() {
		// Nada

	}

	@Override
	public String getConversationId() {
		// Nada
		return null;
	}

	@Override
	public void init() {
		// Setting icon styles
		createStyles();

	}

	@Override
	public void update(Object obj) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		Snapshot snap = (Snapshot) obj;
		HexagonalGrid grid = snap.getGrid();
		for (int i=0;i<6;i++){
			for (int j=0;j<6;j++){
				drawPeople((int) ((Math.random() *100)%3) , grid.tileToCoord(new Point(i,j)));
			}
		}
	}
	
	private void createStyles(){
		//Helty Stile
		Style styleHealthy = folder.createAndAddStyle().withId(String.valueOf(Healthy));
		IconStyle iconStyleHealthy = styleHealthy.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyleHealthy.createAndSetIcon().withHref(imgPath+"healthy.png");
		
		//Hurt Stile
		Style styleHurt = folder.createAndAddStyle().withId(String.valueOf(Hurt));
		IconStyle iconStyleHurt = styleHurt.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyleHurt.createAndSetIcon().withHref(imgPath+"hurt.png");
		
		//Death Stile
		Style styledead = folder.createAndAddStyle().withId(String.valueOf(Dead));
		IconStyle iconStyledead = styledead.createAndSetIconStyle().withColorMode(ColorMode.NORMAL);
		iconStyledead.createAndSetIcon().withHref(imgPath+"dead.png");
	}

	public void drawPeople(int key, LatLng coord) {
		folder.createAndAddPlacemark().withName("Pedestrian").withStyleUrl(String.valueOf(key))
		.createAndSetPoint().addToCoordinates(coord.toKmlString());
	}

}
