package kml;

import java.io.File;
import java.io.FileNotFoundException;

import de.micromata.opengis.kml.v_2_2_0.Kml;

public class KmlUtil {

	public Kml kml;
	public String nombreFichero;
	
	public KmlUtil(String  nombreFichero, String nombreCiudad){
		new Kml();
		kml.createAndSetPlacemark().withName(nombreCiudad).withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(-0.126236, 51.500152);
		//marshals to console
		kml.marshal();
		//marshals into file
		try {
			kml.marshal(new File(nombreFichero));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
