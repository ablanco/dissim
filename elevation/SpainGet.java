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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import util.HexagonalGrid;
import util.java.TempFiles;
import util.java.Wget;
import util.jcoord.LatLng;
import util.jcoord.UTMRef;

//http://www.idee.es/show.do?to=pideep_desarrollador_wcs.ES

public class SpainGet implements ElevationService {

	// http://www.idee.es/wcs/IDEE-WCS-UTM30N/wcsServlet?SERVICE=WCS
	// &REQUEST=GetCoverage&VERSION=1.0.0&CRS=EPSG:4326
	// &BBOX=-5.99678,37.38264,-5.99135,37.385972&COVERAGE=MDT25_peninsula_ZIP&RESX=25
	// &RESY=25&FORMAT=AsciiGrid&EXCEPTIONS=XML

	@Override
	public double getElevation(LatLng coord) throws ElevationException {
		double result = Double.MIN_VALUE;
		String url = "http://www.idee.es/wcs/IDEE-WCS-";
		try {
			url += getHusoUTM(coord)
					+ "/wcsServlet?SERVICE=WCS&REQUEST=GetCoverage&VERSION=1.0.0&CRS=EPSG:4326";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return result;
		}
		double lat = coord.getLat();
		double lng = coord.getLng();
		// -5.927247,37.403450,-5.92724,37.40346 Este BB da una única celda
		url += "&BBOX=" + lng + "," + lat + "," + (lng + 0.00001) + ","
				+ (lat + 0.00001);
		url += "&COVERAGE=MDT25_peninsula_ZIP&RESX=25&RESY=25&FORMAT=AsciiGrid&EXCEPTIONS=XML";
		File f = downloadFile(url);
		try {
			double[][] data = parseFile(f);
			result = data[0][0];
		} catch (Exception e) {
			if (e instanceof ElevationException)
				throw (ElevationException) e;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public double[][] getAllElevations(LatLng NW, LatLng SE, int tileSize)
			throws UnsupportedOperationException {
		double[][] result = new double[0][0];
		String url = "http://www.idee.es/wcs/IDEE-WCS-";
		try {
			url += getHusoUTM(NW, SE)
					+ "/wcsServlet?SERVICE=WCS&REQUEST=GetCoverage&VERSION=1.0.0&CRS=EPSG:4326";
		} catch (IllegalArgumentException e) {
			throw new UnsupportedOperationException();
		}
		// BBOX = SWlng,SWlat,NElng,NElat
		url += "&BBOX=" + NW.getLng() + "," + SE.getLat() + "," + SE.getLng()
				+ "," + NW.getLat();
		url += "&COVERAGE=MDT25_peninsula_ZIP&RESX=25&RESY=25&FORMAT=AsciiGrid&EXCEPTIONS=XML";
		System.out.println(url);
		File f = downloadFile(url);
		try {
			double[][] data = parseFile(f);
			int dcols = data.length;
			int drows = data[0].length;
			// result está en tiles cuadradas? de 25m
			// hay que adaptarlo a la maya hexagonal
			int[] size = HexagonalGrid.calculateSize(NW, SE, tileSize);
			result = new double[size[0] + 2][size[1] + 2];
			// el +2 es de la corona

			for (int col = 0; col < result.length; col++) {
				for (int row = 0; row < result[0].length; row++) {
					int dcol = ((col - 1) * tileSize) / 25;
					int drow = ((row - 1) * tileSize) / 25;
					if (dcol < 0)
						dcol = 0;
					if (drow < 0)
						drow = 0;
					if (dcol >= dcols)
						dcol = dcols - 1;
					if (drow >= drows)
						drow = drows - 1;

					result[col][row] = data[dcol][drow];
				}
			}
		} catch (Exception e) {
			throw new UnsupportedOperationException();
		}
		return result;
	}

	private double[][] parseFile(File f) throws ParseException,
			ElevationException {
		double[][] result = null;

		ArrayList<String> data = new ArrayList<String>();
		try {
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;

				try {
					// Cargamos el fichero completo
					while ((line = br.readLine()) != null)
						data.add(line);
				} finally {
					br.close();
				}
			} else {
				System.err.println("The file " + f.getName()
						+ " doesn't exists");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}

		// Buscamos el código de excepción por si acaso
		for (String s : data) {
			if (s.contains("ServiceExceptionReport")) {
				String aux = "";
				for (String s2 : data)
					aux += s2 + "\n";
				throw new ElevationException(null, aux);
			}
		}

		int cont = 0;
		double no_data = -999.0;
		int cols = -1;
		int rows = -1;
		String[] aux;
		for (String s : data) {
			cont++;
			if (cont > 6) {
				// Datos
				if (cols == -1 || rows == -1)
					throw new ParseException("File " + f.getAbsolutePath()
							+ " has wrong data. The number of columns "
							+ "and rows hasen't been defined.");
				if (result == null)
					result = new double[cols][rows];

				aux = s.split(" ");
				for (int i = 0; i < cols; i++) {
					double value = Double.parseDouble(aux[i]);
					if (value == no_data)
						value = Double.MIN_VALUE;
					result[i][cont - 7] = value;
				}
			} else {
				// Metadatos
				if (s.startsWith("ncols")) {
					aux = s.split(" ");
					cols = Integer.parseInt(aux[aux.length - 1]);
				} else if (s.startsWith("nrows")) {
					aux = s.split(" ");
					rows = Integer.parseInt(aux[aux.length - 1]);
				} else if (s.startsWith("NODATA")) {
					aux = s.split(" ");
					no_data = Double.parseDouble(aux[aux.length - 1]);
				}
			}
		}

		return result;
	}

	private File downloadFile(String url) {
		File result = null;
		try {
			File dir = TempFiles.getDefaultTempDir();
			result = new File(dir, url.substring(url.lastIndexOf('/')));
			if (!result.exists()) { // Si existe no se baja de nuevo
				if (!Wget.wget(dir.getPath(), url)) {
					System.err.println("I couldn't download the data from "
							+ url);
					return null;
				}
			}
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
		if (zone != 28 && zone != 29 && zone != 30 && zone != 31)
			throw new IllegalArgumentException("The coordinate "
					+ coord.toString() + " is outside the supported area");
		return "UTM" + 30 + "N"; // TODO zone parece estar mal calculado
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
					+ NW.toString() + "[" + utmNW.getLngZone() + "]" + " and "
					+ SE.toString() + "[" + utmSE.getLngZone() + "]"
					+ " are in differents zones");
		int zone = utmNW.getLngZone();
		if (zone != 28 && zone != 29 && zone != 30 && zone != 31)
			throw new IllegalArgumentException("The coordinates "
					+ NW.toString() + " and " + SE.toString()
					+ " are outside the supported area");
		return "UTM" + 30 + "N"; // TODO zone parece estar mal calculado
	}

	public class ParseException extends Exception {

		private static final long serialVersionUID = 1L;

		public ParseException(String msg) {
			super(msg);
		}

	}

}
