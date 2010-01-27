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

package webservices;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.crimson.tree.ElementNode2;

import util.jcoord.LatLng;
import webservices.gov.usgs.gisdata.xmlwebservices2.ElevationService;
import webservices.gov.usgs.gisdata.xmlwebservices2.ElevationServiceSoap;
import webservices.gov.usgs.gisdata.xmlwebservices2.GetElevationResponse.GetElevationResult;

//http://gisdata.usgs.gov/XMLWebServices2/Elevation_service.asmx?WSDL

public class Altitude {

	private static ElevationServiceSoap service = null;

	private Altitude() {
		// Inaccesible
	}

	private static void init() {
		ElevationService serv = new ElevationService();
		service = serv.getElevationServiceSoap();
	}

	public static ElementNode2 getElevation(LatLng coord, String sourceLayer,
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
				.getLat()), Double.toString(coord.getLng()), elevationUnits,
				sourceLayer, elevationOnlyStr);
		List<Object> results = res.getContent();

		if (results.size() != 1)
			throw new WebServiceException("Wrong results obtained -> "
					+ results.toArray().toString());

		return (ElementNode2) results.get(0);
	}

	public static double getElevation(LatLng coord) throws WebServiceException {
		// -1.79769313486231E+308 means no valid values were found at that point
		double altitude = Double.MIN_VALUE;

		ElementNode2 result = getElevation(coord, "-1", "METERS", true);
		altitude = Double.parseDouble(result.getFirstChild().toString());

		return altitude;
	}
}
