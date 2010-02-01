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

package test;

import kml.KmlWriter;
import util.Scenario;
import util.jcoord.LatLng;

public class KmlWriterTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Nueva Orleans NW: 29.953260, -90.088238 SE: 29.918075, -90.053707 UTM
		 */

		Scenario newOrleans = Scenario.getCurrentScenario();

		newOrleans.setArea(new LatLng(29.953260, -90.088238), new LatLng(
				29.918075, -90.053707));
		newOrleans.setTileSize(150);

		System.out.println(newOrleans.toString());
		KmlWriter kmle = new KmlWriter();
		kmle.buildKmlAltitudesMap("New Orleans - Altitudes", newOrleans);
	}

}
