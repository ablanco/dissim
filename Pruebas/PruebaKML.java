package Pruebas;

import kml.KmlUtil;

public class PruebaKML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KmlUtil k = new KmlUtil("l","New Orleans, USA");
		System.out.println(k.nombreFichero);
	}

}
