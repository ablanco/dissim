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

import java.util.List;

import javax.xml.ws.WebServiceException;

import elevation.usgs.ElevationService;
import elevation.usgs.ElevationServiceSoap;
import elevation.usgs.GetAllElevationsResponse.GetAllElevationsResult;
import elevation.usgs.GetElevationResponse.GetElevationResult;

import util.jcoord.LatLng;

//http://gisdata.usgs.gov/XMLWebServices2/Elevation_service.asmx?WSDL

/**
 * Webservice client to the USGS elevation services.
 * http://gisdata.usgs.gov/XMLWebServices/TNM_Elevation_Service.php
 * 
 * @author Manuel Gomar, Alejandro Blanco
 */
public class ElevationWS {

	private static ElevationServiceSoap service = null;

	private ElevationWS() {
		// Inaccesible - Clase no instanciable
	}

	/**
	 * Initializes the client
	 */
	private static void init() {
		ElevationService serv = new ElevationService();
		service = serv.getElevationServiceSoap();
	}

	/**
	 * Gets elevation for geographical coordinates
	 * 
	 * @param coord
	 *            Geographical coordinates
	 * @param sourceLayer
	 *            Layer of information
	 * @param elevationUnits
	 *            Units
	 * @param elevationOnly
	 *            If you want only the elevation
	 * @return Webservice's string response
	 * @throws WebServiceException
	 */
	public static String getElevation(LatLng coord, String sourceLayer,
			String elevationUnits, boolean elevationOnly)
			throws WebServiceException {
		if (service == null)
			init();

		String elevationOnlyStr;
		if (elevationOnly)
			elevationOnlyStr = "TRUE";
		else
			elevationOnlyStr = "FALSE";
		GetElevationResult res = service.getElevation(Double.toString(coord
				.getLng()), Double.toString(coord.getLat()), elevationUnits,
				sourceLayer, elevationOnlyStr);
		List<Object> results = res.getContent();

		if (results.size() != 1)
			throw new WebServiceException("Wrong results obtained -> "
					+ results.toString());

		return results.get(0).toString();
	}

	/**
	 * Gets elevation for geographical coordinates
	 * 
	 * @param coord
	 *            Geographical coordinates
	 * @return Elevation of the terrain
	 * @throws WebServiceException
	 */
	public static double getElevation(LatLng coord) throws WebServiceException {
		// -1.79769313486231E+308 means no valid values were found at that point
		double elevation = Double.MIN_VALUE;

		String result = getElevation(coord, "-1", "METERS", true);
		if (!result.contains("null")) {
			result = result.substring(8, result.length() - 9);
			elevation = Double.parseDouble(result);
		}

		return elevation;
	}

	/**
	 * Gets all elevations from the webservice for the given geographical
	 * coordinates
	 * 
	 * @param coord
	 *            Geographical coordinates
	 * @param elevationUnits
	 *            Units
	 * @return Webservice's string response
	 * @throws WebServiceException
	 */
	public static String getAllElevations(LatLng coord, String elevationUnits)
			throws WebServiceException {
		if (service == null)
			init();

		GetAllElevationsResult res = service.getAllElevations(Double
				.toString(coord.getLng()), Double.toString(coord.getLat()),
				elevationUnits);
		List<Object> results = res.getContent();

		if (results.size() != 1)
			throw new WebServiceException("Wrong results obtained -> "
					+ results.toString());

		return results.get(0).toString();
	}

	/**
	 * Gets all elevations from the webservice for the given geographical
	 * coordinates
	 * 
	 * @param coord
	 *            Geographical coordinates
	 * @return Webservice's string response
	 * @throws WebServiceException
	 */
	public static String getAllElevations(LatLng coord)
			throws WebServiceException {
		return getAllElevations(coord, "METERS");
	}
}
