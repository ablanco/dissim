//    Flood and evacuation simulator using multi-agent technology
//    Copyright (C) 2010 Alejandro Blanco and Manuel Gomar
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package elevation;

import java.io.File;

import util.java.TempFiles;
import util.java.Wget;
import util.jcoord.LatLng;
import util.jcoord.UTMRef;

//http://www.idee.es/show.do?to=pideep_desarrollador_wcs.ES

public class SpainGet implements ElevationService {

	// http://www.idee.es/wcs/IDEE-WCS-UTM30N/wcsServlet?SERVICE=WCS
	// &REQUEST=GetCoverage&VERSION=1.0.0&CRS=EPSG:4326
	// &BBOX=470000,4130300,470200,4130500&COVERAGE=MDT25_peninsula_ZIP&RESX=25
	// &RESY=25&FORMAT=AsciiGrid&EXCEPTIONS=XML

	@Override
	public double getElevation(LatLng coord) {
		double result = Double.MIN_VALUE;
		String url = "http://www.idee.es/wcs/IDEE-WCS-";
		try {
			url += getHusoUTM(coord)
					+ "/wcsServlet?SERVICE=WCS&REQUEST=GetCoverage&VERSION=1.0.0&CRS=EPSG:4326";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return result;
		}
		// TODO terminar url BBOX
		url += "&COVERAGE=MDT25_peninsula_ZIP&RESX=25&RESY=25&FORMAT=AsciiGrid&EXCEPTIONS=XML";
		File f = downloadFile(url);
		double[][] data = parseFile(f);
		try {
			result = data[0][0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public double[][] getAllElevations(LatLng NW, LatLng SE, int TileSize)
			throws UnsupportedOperationException {
		// TODO implementar
		throw new UnsupportedOperationException();
	}

	private double[][] parseFile(File f) {
		double[][] result = null;
		// TODO implementar
		return result;
	}

	private File downloadFile(String url) {
		File result = null;
		try {
			File dir = TempFiles.getDefaultTempDir();
			String fileName = url.substring(url.lastIndexOf('/'));
			File file = new File(dir, fileName);
			if (!file.exists()) { // Si existe no se baja de nuevo
				if (!Wget.wget(dir.getPath(), url)) {
					System.err.println("I couldn't download the data from "
							+ url);
					return null;
				}
			}
			result = new File(dir, fileName); // TODO hace falta el new?
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 3 posibles valores
	// UTM28N Canarias
	// UTM29N Portugal
	// UTM30N Mitad Oeste
	// UTM31N Mitad Este

	private String getHusoUTM(LatLng coord) {
		UTMRef utm = coord.toUTMRef();
		// if (utm.getLatZone() != 'N')
		// throw new IllegalArgumentException("The coordinate "
		// + coord.toString() + " is outside the supported area");
		int zone = utm.getLngZone();
		if (zone != 28 && zone != 30 && zone != 31)
			throw new IllegalArgumentException("The coordinate "
					+ coord.toString() + " is outside the supported area");
		return "UTM" + zone + "N";
	}

	private String getHusoUTM(LatLng NW, LatLng SE) {
		UTMRef utmNW = NW.toUTMRef();
		UTMRef utmSE = SE.toUTMRef();
		// if (utmNW.getLatZone() != 'N' || utmSE.getLatZone() != 'N')
		// throw new IllegalArgumentException("The coordinates "
		// + NW.toString() + " and " + SE.toString()
		// + " are outside the supported area");
		if (utmNW.getLngZone() != utmSE.getLngZone())
			throw new IllegalArgumentException("The coordinates "
					+ NW.toString() + " and " + SE.toString()
					+ " are in differents zones");
		int zone = utmNW.getLngZone();
		if (zone != 28 && zone != 30 && zone != 31)
			throw new IllegalArgumentException("The coordinates "
					+ NW.toString() + " and " + SE.toString()
					+ " are outside the supported area");
		return "UTM" + zone + "N";
	}

}
