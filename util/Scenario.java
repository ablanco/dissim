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

package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import util.jcoord.LatLng;

public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete = false;
	private String description = "";
	private String name = "";
	/**
	 * 1 altitude unit means (1/precision) meters
	 */
	private short precision = 10;
	/**
	 * Periodo de actualización de los visores
	 */
	private long updateVisor = 1000L;
	/**
	 * Periodo de actualización de los generadores de KML
	 */
	private long updateKML = 3000L;
	/**
	 * Periodo de actualización de los agentes persona
	 */
	private long updatePeople = 300L;
	/**
	 * Coordinates of the North West point of the simulation area
	 */
	protected LatLng globalNW = null;
	/**
	 * Coordinates of the South East point of the simulation area
	 */
	protected LatLng globalSE = null;
	/**
	 * Diameter of the circunflex circle of the hexagon in meters
	 */
	private int tileSize = -1;
	/**
	 * Number of enviroment agents
	 */
	private int numEnv = 1;
	private ArrayList<LatLng[]> envAreas = null;
	private ArrayList<int[]> envSizes = null;
	/**
	 * Simulation start time and date
	 */
	private DateAndTime simTime = null;
	/**
	 * Human agents list
	 */
	private LinkedList<Pedestrian> people = new LinkedList<Pedestrian>();
	/**
	 * Random terrain
	 */
	private boolean randomAltitudes = false;

	// This class shouldn't be used directly, that's why the constructor is
	// protected
	protected Scenario() {
		// Usar clases hijas
	}

	/**
	 * Loads a Scenario form a text file and returns an instance
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static Scenario loadScenario(String path) throws IOException,
			ClassNotFoundException {
		Scenario scen = null;
		File f = new File(path);
		if (f.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			ArrayList<String> data = new ArrayList<String>();
			String line;

			try {
				// Cargamos el fichero completo
				while ((line = br.readLine()) != null)
					data.add(line);
			} finally {
				br.close();
			}

			// Buscamos el tipo de escenario
			for (String s : data) {
				if (s.startsWith("type")) {
					String[] pair = s.split("=");
					// Carga, y crea un objeto de la clase pasada, por reflexión
					try {
						Class cls = Class.forName(pair[1]);
						Constructor ct = cls.getConstructor(new Class[0]);
						scen = (Scenario) ct.newInstance(new Object[0]);
					} catch (Exception e) {
						throw new IOException(
								"There was a problem trying to instantiate "
										+ pair[1]);
					}
					break;
				}
			}

			// Rellenamos de datos el escenario
			if (scen != null) {
				scen.loadScenarioData(data);
				scen.complete();
			} else {
				throw new ClassNotFoundException(
						"Couldn't create an instance of the type defined in "
								+ path);
			}
		} else {
			throw new FileNotFoundException("File " + path + "not found.");
		}
		return scen;
	}

	protected String[] decodeScenArray(String s) {
		String[] result;
		s = s.substring(1, s.length() - 1);
		result = s.split(",");
		return result;
	}

	protected void loadScenarioData(ArrayList<String> data) {
		LatLng NW = null;
		LatLng SE = null;
		int TS = -1;
		String date = null;
		String hour = null;
		for (String s : data) {
			String[] pair = s.split("=");
			if (pair[0].equals("name")) {
				setName(pair[1]);
			} else if (pair[0].equals("description")) {
				setDescription(pair[1]);
			} else if (pair[0].equals("date")) {
				date = pair[1];
				if (hour != null)
					simTime = new DateAndTime(date, hour);
			} else if (pair[0].equals("hour")) {
				hour = pair[1];
				if (date != null)
					simTime = new DateAndTime(date, hour);
			} else if (pair[0].equals("NW")) {
				String[] nw = decodeScenArray(pair[1]);
				NW = new LatLng(Double.parseDouble(nw[0]), Double
						.parseDouble(nw[1]));
				if (SE != null && TS > 0)
					setGeoData(NW, SE, TS);
			} else if (pair[0].equals("SE")) {
				String[] se = decodeScenArray(pair[1]);
				SE = new LatLng(Double.parseDouble(se[0]), Double
						.parseDouble(se[1]));
				if (NW != null && TS > 0)
					setGeoData(NW, SE, TS);
			} else if (pair[0].equals("tileSize")) {
				TS = Integer.parseInt(pair[1]);
				if (NW != null && SE != null)
					setGeoData(NW, SE, TS);
			} else if (pair[0].equals("numEnvs")) {
				setNumEnv(Integer.parseInt(pair[1]));
			} else if (pair[0].equals("updateTimePeople")) {
				setPeopleUpdateTime(Long.parseLong(pair[1]));
			} else if (pair[0].equals("person")) {
				String[] person = decodeScenArray(pair[1]);
				Pedestrian p = new Pedestrian(new LatLng(Double
						.parseDouble(person[0]), Double.parseDouble(person[1])));
				p.setScenData(Integer.parseInt(person[2]), Integer
						.parseInt(person[3]), Integer.parseInt(person[4]));
				String[] objectives = decodeScenArray(person[5]);
				for (int i = 0; i < objectives.length; i++) {
					String lat = objectives[i];
					i++;
					String lng = objectives[i];
					LatLng obj = new LatLng(Double.parseDouble(lat), Double
							.parseDouble(lng));
					p.addObjective(obj);
				}
				addPeople(p);
			} else if (pair[0].equals("updateTimeKml")) {
				setUpdateKMLPeriod(Long.parseLong(pair[1]));
			} else if (pair[0].equals("updateTimeVisor")) {
				setUpdateVisorPeriod(Long.parseLong(pair[1]));
			} else if (pair[0].equals("precision")) {
				setPrecision(Short.parseShort(pair[1]));
			} else if (pair[0].equals("randomTerrain")) {
				setRandomAltitudes(Boolean.parseBoolean(pair[1]));
			}
		}
	}

	/**
	 * Sets the Geolocation of the simulation, and the size of the tiles
	 * 
	 * @param NW
	 *            Upper left corner
	 * @param SE
	 *            Lower right corner
	 * @param tileSize
	 *            size of the tile terrain
	 */
	public void setGeoData(LatLng NW, LatLng SE, int tileSize) {
		globalNW = NW;
		globalSE = SE;
		this.tileSize = tileSize;
	}

	public LatLng[] getSimulationArea() {
		return new LatLng[] { globalNW, globalSE };
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public boolean isComplete() {
		return complete;
	}

	public void complete() {
		if (globalNW == null || globalSE == null || tileSize < 0
				|| simTime == null)
			throw new IllegalStateException(
					"There are mandatory parameters that hasn't been defined yet.");

		complete = true;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setPrecision(short precision) {
		this.precision = precision;
	}

	public short getPrecision() {
		return precision;
	}

	public short doubleToInner(double d) {
		return (short) (d * precision);
	}

	public static short doubleToInner(short precision, double d) {
		return (short) (d * precision);
	}

	public double innerToDouble(short s) {
		return ((double) s) / precision;
	}

	public static double innerToDouble(short precision, short s) {
		return ((double) s) / precision;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public long getUpdateVisorPeriod() {
		return updateVisor;
	}

	public void setUpdateVisorPeriod(long updateVisor) {
		this.updateVisor = updateVisor;
	}

	public long getUpdateKMLPeriod() {
		return updateKML;
	}

	public void setUpdateKMLPeriod(long updateKML) {
		this.updateKML = updateKML;
	}

	public long getPeopleUpdateTime() {
		return updatePeople;
	}

	public void setPeopleUpdateTime(long updatePeople) {
		this.updatePeople = updatePeople;
	}

	public int getNumEnv() {
		return numEnv;
	}

	public void setNumEnv(int numEnv) {
		// TODO En realidad los que no valen son los primos
		if (!(numEnv == 1 || (numEnv % 2) == 0))
			throw new IllegalArgumentException(
					"Number of enviroments must be one or even");

		this.numEnv = numEnv;
	}

	public LatLng[] getEnvArea(int index) {
		if (globalNW == null || globalSE == null || tileSize < 0)
			throw new IllegalStateException(
					"Geographical data hasn't been initialized yet.");
		if (envAreas == null)
			divideAreaBetweenEnvs();
		return envAreas.get(index);
	}

	public int[] getEnvSize(int index) {
		if (globalNW == null || globalSE == null || tileSize < 0)
			throw new IllegalStateException(
					"Geographical data hasn't been initialized yet.");
		if (envSizes == null)
			divideAreaBetweenEnvs();
		return envSizes.get(index);
	}

	private void divideAreaBetweenEnvs() {
		envAreas = new ArrayList<LatLng[]>(numEnv);
		envSizes = new ArrayList<int[]>(numEnv);

		if (numEnv == 1) {
			envAreas.add(new LatLng[] { globalNW, globalSE });
			int[] size = HexagonalGrid.calculateSize(globalNW, globalSE,
					tileSize);
			envSizes.add(new int[] { size[0], size[1], 0, 0 });
			return;
		}

		double diflng = Math.abs(globalNW.getLng() - globalSE.getLng());
		double diflat = Math.abs(globalNW.getLat() - globalSE.getLat());

		System.out.println("GLOBAL: NW " + globalNW.toString() + " SE "
				+ globalSE.toString());

		// TODO Mejorar división en entornos
		int mitt = numEnv / 2;
		diflat = diflat / 2.0;
		diflng = diflng / ((double) mitt);
		int offX = 0;
		int offY = 0;
		double lat = globalNW.getLat();
		for (int i = 0; i < numEnv; i++) {
			if (i == mitt) {
				lat -= diflat;
				offX = 0;
				offY = envSizes.get(i - 1)[1];
			}

			LatLng NW = new LatLng(lat, globalNW.getLng()
					+ (diflng * Math.abs(i % mitt)));
			LatLng SE = new LatLng(lat - diflat, globalNW.getLng() + diflng
					+ (diflng * Math.abs(i % mitt)));
			int[] size = HexagonalGrid.calculateSize(NW, SE, tileSize);

			System.out.println("ENV" + i + ": NW " + NW.toString() + " SE "
					+ SE.toString());

			envAreas.add(i, new LatLng[] { NW, SE });
			envSizes.add(i, new int[] { size[0], size[1], offX, offY });

			offX += size[0];
		}
	}

	public int getEnviromentByCoord(LatLng coord) {
		if (envAreas == null)
			throw new IllegalStateException(
					"Enviroments haven't been initialized");

		int idx = 0;
		for (LatLng[] envCoords : envAreas) {
			if (coord.isContainedIn(envCoords[0], envCoords[1]))
				return idx;
			idx++;
		}
		return -1;
	}

	public int getEnviromentByPosition(int x, int y) {
		if (envSizes == null)
			throw new IllegalStateException(
					"Enviroments haven't been initialized");

		int idx = 0;
		for (int[] envSize : envSizes) {
			if (x < (envSize[0] + envSize[2]) && x >= envSize[2]
					&& y < (envSize[1] + envSize[3]) && y >= envSize[3])
				return idx;
			idx++;
		}
		return -1;
	}

	public boolean addPeople(Pedestrian p) {
		boolean result = false;
		LatLng pos = p.getPos();
		if (pos != null) {
			if (pos.isContainedIn(globalNW, globalSE))
				result = people.add(p);
		}
		return result;
	}

	public Iterator<Pedestrian> getPeopleIterator() {
		return people.iterator();
	}

	public void setStartTime(int year, int month, int dayOfMonth,
			int hourOfDay, int minute) {
		simTime = new DateAndTime(year, month, dayOfMonth, hourOfDay, minute);
	}

	public DateAndTime getStartTime() {
		return simTime;
	}

	public void setRandomAltitudes(boolean randomAltitudes) {
		this.randomAltitudes = randomAltitudes;
	}

	public boolean getRandomAltitudes() {
		return randomAltitudes;
	}

}
