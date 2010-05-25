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

/**
 * This is one of the largest class in dissim, it contains all the info from the
 * simulation area and parameters,
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Scenario implements Serializable {

	private static final long serialVersionUID = 1L;

	// The GUI is the one that should care that the Scenario is completed before
	// simulating it
	private boolean complete = false;
	private String description = "";
	private String name = "";
	private long simulationTick = 250;
	private int realTimeTick = 5;
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
	/**
	 * Elevation data base connection information
	 */
	private String dbServer = null;
	private int dbPort = -1;
	private String dbUser = null;
	private String dbPass = null;
	private String dbDriver = null;
	private String dbDb = null;

	/**
	 * This class shouldn't be used directly, that's why the constructor is
	 * protected
	 */

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

	/**
	 * Decode scenario array info from a string
	 * 
	 * @param s
	 *            Containing scenario info
	 * @return parameters
	 */
	protected String[] decodeScenArray(String s) {
		s = s.substring(1, s.length() - 1);
		String[] result = new String[] { s };
		if (s.length() > 0) {
			ArrayList<String> data = new ArrayList<String>();
			int i = 0;
			i = s.indexOf(',');
			while (i >= 0) {
				int j = s.indexOf('[');
				if (j == 0) {
					// El siguiente elem a procesar es un []
					j = s.indexOf(']');
					data.add(s.substring(0, j + 1));
					s = s.substring(j + 1);
				} else {
					// El siguiente elem a procesar no es un []
					data.add(s.substring(0, i));
					s = s.substring(i + 1);
				}
				i = s.indexOf(',');
			}
			if (s.length() > 0)
				data.add(s);
			result = new String[data.size()];
			result = data.toArray(result);
		}
		return result;
	}

	/**
	 * Initializes the scenario form an array of parameters
	 * 
	 * @param data
	 *            array of parameters
	 */
	protected void loadScenarioData(ArrayList<String> data) {
		LatLng NW = null;
		LatLng SE = null;
		int TS = -1;
		String date = null;
		String hour = null;
		for (String s : data) {
			if (!s.startsWith("#")) {
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
				} else if (pair[0].equals("tick")) {
					setSimulationTick(Long.parseLong(pair[1]));
				} else if (pair[0].equals("realTick")) {
					setRealTimeTick(Integer.parseInt(pair[1]));
				} else if (pair[0].equals("person")) {
					String[] person = decodeScenArray(pair[1]);
					Pedestrian p = new Pedestrian(new LatLng(Double
							.parseDouble(person[0]), Double
							.parseDouble(person[1])));
					p.setScenData(Integer.parseInt(person[2]), Integer
							.parseInt(person[3]), Integer.parseInt(person[4]));
					String[] objectives = decodeScenArray(person[5]);
					for (int i = 0; i < objectives.length
							&& objectives.length > 1; i++) {
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
				} else if (pair[0].equals("DBServer")) {
					setDbServer(pair[1]);
				} else if (pair[0].equals("DBPort")) {
					setDbPort(Integer.parseInt(pair[1]));
				} else if (pair[0].equals("DBUser")) {
					setDbUser(pair[1]);
				} else if (pair[0].equals("DBPass")) {
					setDbPass(pair[1]);
				} else if (pair[0].equals("DBDriver")) {
					setDbDriver(pair[1]);
				} else if (pair[0].equals("DBDb")) {
					setDbDb(pair[1]);
				}
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

	/**
	 * Get upper left and lower right coordinate corners of the simulation area
	 * 
	 * @return
	 */
	public LatLng[] getSimulationArea() {
		return new LatLng[] { globalNW, globalSE };
	}

	/**
	 * Set a description to describe the scenario
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets description
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * check if scenario have all the necessary (minumun) values correctly
	 * configured to run a simulation
	 * 
	 * @return true if is complete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets complete a true if globalNW or globalSe or simTime are not null
	 * 
	 * @throws IllegalStateException
	 *             if globalNW or globalSe or simTime are null
	 */
	public void complete() {
		if (globalNW == null || globalSE == null || tileSize < 0
				|| simTime == null)
			throw new IllegalStateException(
					"There are mandatory parameters that hasn't been defined yet.");

		complete = true;
	}

	/**
	 * Gets tile size of the scenario
	 * 
	 * @return tile size
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * Sets precision in the discretization of values
	 * 
	 * @param precision
	 */
	public void setPrecision(short precision) {
		this.precision = precision;
	}

	/**
	 * Gets scenario precision in data
	 * 
	 * @return
	 */
	public short getPrecision() {
		return precision;
	}

	/**
	 * Convert double units to short applying precision
	 * 
	 * @param d
	 *            unit to convert
	 * @return a conversed unit
	 */
	public short doubleToInner(double d) {
		return (short) (d * precision);
	}

	/**
	 * Convert double units to short applying precision
	 * 
	 * @param precision
	 *            of the metric
	 * @param d
	 *            unit to convert
	 * @return a converted unit
	 */
	public static short doubleToInner(short precision, double d) {
		return (short) (d * precision);
	}

	/**
	 * Convert short to double unapplying precision
	 * 
	 * @param s
	 *            value
	 * @return converted unit
	 */
	public double innerToDouble(short s) {
		return ((double) s) / precision;
	}

	/**
	 * Convert short yo double unapplying precision
	 * 
	 * @param precision
	 *            units
	 * @param s
	 *            value
	 * @return converted unit
	 */
	public static double innerToDouble(short precision, short s) {
		return ((double) s) / precision;
	}

	/**
	 * Set name for the scenario
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets scenario name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the period of time that between simulation steps for the visor
	 * 
	 * @return time between simulation steps
	 */
	public long getUpdateVisorPeriod() {
		return updateVisor;
	}

	/**
	 * Sets the period of time that between simulation steps for the visor
	 * 
	 * @param updateVisor
	 *            time between simulation steps
	 */
	public void setUpdateVisorPeriod(long updateVisor) {
		this.updateVisor = updateVisor;
	}

	/**
	 * Gets the period of time that between simulation steps for the kml
	 * 
	 * @param updateVisor
	 *            time between simulation steps
	 */
	public long getUpdateKMLPeriod() {
		return updateKML;
	}

	/**
	 * Sets the period of time that between simulation steps for the kml
	 * 
	 * @param updateKML
	 *            time between simulation steps
	 */
	public void setUpdateKMLPeriod(long updateKML) {
		this.updateKML = updateKML;
	}

	/**
	 * Get the numbers of environments for the simulation
	 * 
	 * @return number of environment
	 */
	public int getNumEnv() {
		return numEnv;
	}

	/**
	 * Sets the number of environmets for the simulation
	 * 
	 * @param numEnv
	 * @throws IllegalArgumentException
	 *             Number of enviroments must be one or even
	 */
	public void setNumEnv(int numEnv) {
		// TODO En realidad valen todos, aunque los primos son muy poco
		// adecuados
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

		/*
		 * TODO Mejorar división en entornos. Habría que quedarse con los dos
		 * divisores más adecuados según la forma del área a simular. Por
		 * ejemplo, para un área cuadrada de la que se quisieran 16 entornos
		 * habría que dividirla en 4 y 4. Pero si fuera muy alagarda sería mejor
		 * dividirla en 8 y 2. Es complicado de calcular.
		 */
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

	/**
	 * Get the envioronment that cotains the coord
	 * 
	 * @param coord
	 *            we want to place into an environment
	 * @return envioronment that cotains the coord
	 */
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

	/**
	 * Get a environment that correspond to the point
	 * 
	 * @param x
	 *            col
	 * @param y
	 *            row
	 * @return envioronment that cotains the point
	 */
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

	/**
	 * Adds one pedestrian to the scenario
	 * 
	 * @param p
	 *            pedestrian
	 * @return true if added
	 */
	public boolean addPeople(Pedestrian p) {
		boolean result = false;
		LatLng pos = p.getPos();
		if (pos != null) {
			if (pos.isContainedIn(globalNW, globalSE))
				result = people.add(p);
		}
		return result;
	}

	/**
	 * Get an iterator over pedestrians
	 * 
	 * @return pedestrian iterator
	 */
	public Iterator<Pedestrian> getPeopleIterator() {
		return people.iterator();
	}

	/**
	 * Set start date for the simulation
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 */
	public void setStartTime(int year, int month, int dayOfMonth,
			int hourOfDay, int minute) {
		simTime = new DateAndTime(year, month, dayOfMonth, hourOfDay, minute);
	}

	/**
	 * Get start date and time of the simulation
	 * 
	 * @return date and time of the simulation
	 */
	public DateAndTime getStartTime() {
		return simTime;
	}

	/**
	 * Set a random altitude for the environmets of the simulation. This is
	 * useful when you dont have real altitudes
	 * 
	 * @param randomAltitudes
	 *            true if random
	 */
	public void setRandomAltitudes(boolean randomAltitudes) {
		this.randomAltitudes = randomAltitudes;
	}

	/**
	 * Get if we wanto to use random altitudes for the simulation
	 * 
	 * @return true if random
	 */
	public boolean getRandomAltitudes() {
		return randomAltitudes;
	}

	/**
	 * Get the data base server
	 * 
	 * @return dbserver
	 */
	public String getDbServer() {
		return dbServer;
	}

	/**
	 * Set the data base server
	 * 
	 * @param dbServer
	 * @throws IllegalArgumentException
	 *             The DB server cannot be null
	 */
	public void setDbServer(String dbServer) {
		if (dbServer == null)
			throw new IllegalArgumentException("The DB server cannot be null");

		this.dbServer = dbServer;
	}

	/**
	 * Gets the port for the data base
	 * 
	 * @return port number
	 */
	public int getDbPort() {
		return dbPort;
	}

	/**
	 * Gets the port for the data base
	 * 
	 * @param dbPort
	 *            port number
	 * @throws IllegalArgumentException
	 *             DB port must be between 1024 and 65535
	 */
	public void setDbPort(int dbPort) {
		if (dbPort < 1024 || dbPort > 65535)
			throw new IllegalArgumentException(
					"DB port must be between 1024 and 65535");

		this.dbPort = dbPort;
	}

	/**
	 * Gets user for the data base
	 * 
	 * @return user
	 */
	public String getDbUser() {
		return dbUser;
	}

	/**
	 * sets user for the data base
	 * 
	 * @param dbUser
	 *            user
	 */
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	/**
	 * Gets password for the data base
	 * 
	 * @return password
	 */
	public String getDbPass() {
		return dbPass;
	}

	/**
	 * Sets password for the data base
	 * 
	 * @param dbPass
	 *            password
	 */
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	/**
	 * Gets driver for the data base
	 * 
	 * @return driver
	 */
	public String getDbDriver() {
		return dbDriver;
	}

	/**
	 * Sets driver for the data base
	 * 
	 * @param dbDriver
	 *            driver
	 */
	public void setDbDriver(String dbDriver) {
		if (dbDriver == null)
			throw new IllegalArgumentException("The DB driver cannot be null");
		this.dbDriver = dbDriver;
	}

	/**
	 * Gets data base path
	 * 
	 * @return path to db
	 */
	public String getDbDb() {
		return dbDb;
	}

	/**
	 * Sets data base path
	 * 
	 * @param dbDb
	 *            path
	 */
	public void setDbDb(String dbDb) {
		this.dbDb = dbDb;
	}

	public Object getSimulationTick() {
		return simulationTick;
	}

	/**
	 * Set time in miliseconds for the time limit computation of agents
	 * 
	 * @param simulationTick
	 *            miliseconts
	 */
	public void setSimulationTick(long simulationTick) {
		if (simulationTick <= 0)
			throw new IllegalArgumentException(
					"The simulation clock tick must be positive");

		this.simulationTick = simulationTick;
	}

	/**
	 * Returns the amount of real time in minutes that a tick represents
	 * 
	 * @return minutes
	 */
	public int getRealTimeTick() {
		return realTimeTick;
	}

	/**
	 * Sets real time tha corresponds to the simulationTick
	 * 
	 * @param realTimeTick
	 *            time
	 * @throws IllegalArgumentException
	 *             Real tick time must be positive
	 */
	public void setRealTimeTick(int realTimeTick) {
		if (realTimeTick <= 0)
			throw new IllegalArgumentException(
					"Real tick time must be positive");

		this.realTimeTick = realTimeTick;
	}

}
