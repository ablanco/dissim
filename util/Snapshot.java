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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Contains all the changing info between two steps, agents will transfer this
 * to communicate and update the simulation, each snapshot is a view of a
 * simulation step
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Snapshot implements Serializable {

	private static final long serialVersionUID = 1L;

	private HexagonalGrid grid;
	private String dateTime;
	private List<Pedestrian> people;
	private String name;
	private String description;

	/**
	 * New snapshot of the step simulation
	 * 
	 * @param name
	 *            of simulation
	 * @param description
	 *            of simulation
	 * @param grid
	 *            for the simulation
	 * @param dateTime
	 *            and time of the simulation
	 * @param people
	 *            involved in the simulation
	 */
	public Snapshot(String name, String description, HexagonalGrid grid,
			String dateTime, Map<String, Pedestrian> people) {
		this.grid = grid;
		this.dateTime = dateTime;
		this.name = name;
		this.description = description;
		this.people = new ArrayList<Pedestrian>(people.size());
		Iterator<String> it = people.keySet().iterator();
		while (it.hasNext()) {
			String id = it.next();
			Pedestrian p = people.get(id);
			this.people.add(new Pedestrian(p.getPoint(), p.getStatus(), id));
		}
	}

	/**
	 * Get the Hexagonal grid, contains elevations, and streets
	 * 
	 * @return grid
	 */
	public HexagonalGrid getGrid() {
		return grid;
	}

	/**
	 * Get date and time of the simulation
	 * 
	 * @return date and time
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * Get pedestrians in the scenario
	 * 
	 * @return pedestrians
	 */
	public List<Pedestrian> getPeople() {
		return people;
	}

	/**
	 * Get name of the simulation
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get description of the simulation
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

}
