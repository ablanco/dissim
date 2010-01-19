package pruebas;

import kml.KmlUtil;

public class PruebaKML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KmlUtil kml = new KmlUtil();
		// 29.964722 , -90.070556
		/*
		 * <longitude>-90,0750720001457</longitude>
		 * <latitude>29,95464802061058</latitude> 29,95464802061058 ,
		 * -90,0750720001457
		 */
		double latitud = 29.95464802061058;
		double longitud = -90.0750720001457;
		kml.setPlacemark("Nueva Orleans", latitud, longitud);
		kml.writeFile("Prueba");
		kml.showCoordinates();

	}

}
