package pruebas;

import kml.KmlExtractor;

public class PruebaExtractKml {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KmlExtractor kmle = new KmlExtractor("Prueba.kml");
		kmle.extractCoordinates();
		kmle.extractReferences();
		System.out.println(kmle.toString());
	}

}
