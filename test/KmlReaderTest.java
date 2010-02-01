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

import kml.KmlReader;
import util.HexagonalGrid;
import util.Scenario;
import util.flood.FloodScenario;

public class KmlReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scenario scene = new FloodScenario();
		scene.complete();		
		KmlReader k = new KmlReader("New Orleans - Altitudes Test.kmz");
		HexagonalGrid hexGrid = k.getHexagonalGrid();
		
		
	}

}
