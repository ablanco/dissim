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

package kml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import util.Updateable;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;

public class KmlBase implements Updateable{
	public final static String folderName = "Kml";
	protected Kml kml;
	protected Folder folder;

	public KmlBase(String name, String description) {
		kml = new Kml();
		folder = newFolder(kml, name, description);
	}

	public KmlBase() {
		kml = new Kml();
		folder = newFolder(kml);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if(folder.getName()!= null && folder.getName().length()!=0){
			createKmzFile(kml, folder.getName());
		}else{
			createKmzFile(kml, "UnamedKml");
		}		
	}

	@Override
	public String getConversationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Object obj) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public void setName(String name) {
		folder.setName(name);
	}

	public void setDescription(String description) {
		folder.setDescription(description);
	}

	public Kml getKml() {
		return kml;
	}
	
	public Folder getFolder(){
		return folder;
	}

	public String getName() {
		if (folder.getName() != null && folder.getName().length() != 0)
			return folder.getName();
		return "DefaultName";
	}

	public String getDescription() {
		if (folder.getDescription() != null
				&& folder.getDescription().length() != 0)
			return folder.getDescription();
		return "DefaultDescriptor";
	}
	
	/**
	 * Static methods
	 */
	
	/**
	 * New kmz file of the current kml
	 * 
	 * @param fileName
	 */
	public static void createKmzFile(Kml kml, String fileName) {
		try {
			File f = new File(fileName + ".kmz");
			kml.marshalAsKmz(f.getPath());
			// For debugg
			kml.marshal(new File(fileName + ".kml"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * A folder is a container where you can put several things inside
	 */
	public static Folder newFolder(Kml kml, String name, String description) {
		return kml.createAndSetFolder().withName(name).withOpen(true)
				.withDescription(description);
	}

	public static Folder newFolder(Kml kml) {
		return kml.createAndSetFolder().withOpen(true);
	}

	/**
	 * A placemark to geolocate things
	 * 
	 * @param name
	 *            of the placemark
	 * @return placeMark to geolocate things
	 */
	public static Placemark newPlaceMark(Folder folder, String name) {
		return folder.createAndAddPlacemark().withName(name);
	}

	/**
	 * Draw a polygon from the sequence of points
	 * 
	 * @param name
	 *            of the polygon
	 * @param borderLine
	 *            borders of the polygon
	 */
	public static void drawPolygon(Placemark placeMark,
			List<LatLng> borderLine, double[] incs) {
		Polygon polygon = newPolygon(placeMark);
		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1: //Only one hexagon
			drawHexagonBorders(polygon, borderLine.get(0), incs);
			break;
		default:
//			drawPolygon(folder, name, borderLine, incs);
			break;
		}
	}
	
	public static void drawPolygon(Placemark placeMark,
			LatLng borderLine, double[] incs) {
		Polygon polygon = newPolygon(placeMark);
		if (borderLine != null){
			drawHexagonBorders(polygon, borderLine, incs);
		}else{
			throw new IllegalArgumentException("Poligon canot be empty");
		}
	}

	protected static void drawHexagonBorders(Polygon polygon, LatLng coord, double[] incs) {

		double ilat = (incs[0] * 4)/6;
		double ilng = incs[1]/2;
		
		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();

		l.addToCoordinates(coord.addIncs(ilat, 0).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat / 2, ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat / 2, ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat, 0).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat / 2, -ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat / 2, -ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat, 0).toKmlString());
	}

	/**
	 * Creates Polygon Object
	 * 
	 * @param placeMark
	 * @return
	 */
	protected static Polygon newPolygon(Placemark placeMark) {
		return placeMark.createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
	}

	protected void drawPolygonBorders(Polygon polygon, List<LatLng> borderLine) {
		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();
		LatLng z = borderLine.get(0);
		for (LatLng c : borderLine) {
			l.addToCoordinates(c.toKmlString());
		}
		l.addToCoordinates(z.toKmlString());
	}

	/**
	 * Sets when the event happends
	 * 
	 * @param placeMark
	 */
	protected static void setTimeSpan(Placemark placeMark, String beginTime,
			String endTime) {
		TimeSpan t = new TimeSpan();
		if (beginTime != null) {
			t.setBegin(beginTime);
		}
		if (endTime != null) {
			t.setEnd(endTime);
		}
		placeMark.setTimePrimitive(t);
	}

}
