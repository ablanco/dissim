package googleEarth;

import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;

public class GoogleEarth {
	protected Kml kml;
	protected Folder folder;
	
	public GoogleEarth(String name, String description){
		kml = new Kml();
		folder = GoogleEarthUtils.newFolder(kml, name, description);
	}
	
	public Kml getKml(){
		return kml;
	}
}
