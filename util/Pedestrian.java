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
import java.util.HashSet;
import java.util.Set;

import behaviours.people.PedestrianBehav;

import agents.people.PedestrianAgent;

import util.jcoord.LatLng;

/**
 * Class for modeling pedestrians, contains methods and static values
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Pedestrian implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This pedestrian is healthy
	 */
	public final static int HEALTHY = 0;
	/**
	 * This pedestrian is dead
	 */
	public final static int DEAD = 1;
	/**
	 * This pedestrian is hurt
	 */
	public final static int HURT = 2;
	/**
	 * This pedestrian is in a safe area
	 */
	public final static int SAFE = 3;

	private LatLng pos = null;
	private int status = HEALTHY;
	private String id = null;
	private Point point = null;
	// TODO - Q se elija en el .scen
	private String behaviour = "behaviours.people.flood.KnownSafepointPedestrianBehav";
	private int vision = -1;
	private int speed = -1;
	private int clones = 1;
	private Set<LatLng> objectives = null;

	/**
	 * Creates a pedestrian into a point
	 * 
	 * @param point
	 *            tile
	 */
	public Pedestrian(Point point) {
		this.point = point;
	}

	/**
	 * Creates a pedestrian on a geographical coordinate
	 * 
	 * @param pos
	 *            coordinate
	 */
	public Pedestrian(LatLng pos) {
		this.pos = pos;
	}

	/**
	 * Creates a pedestrian on a geographical coordinate, also gives him an id
	 * and status
	 * 
	 * @param pos
	 *            coordinate
	 * @param status
	 *            of the pedestrian
	 * @param id
	 *            for the pedestrian
	 */
	public Pedestrian(LatLng pos, int status, String id) {
		this.pos = pos;
		this.status = status;
		this.id = id;
	}

	/**
	 * Creates a pedestrian into a point, also gives him an id and status
	 * 
	 * @param point
	 *            tile of a scenario
	 * @param status
	 *            of pedestrian
	 * @param id
	 *            for pedestrian
	 */
	public Pedestrian(Point point, int status, String id) {
		this.point = point;
		this.status = status;
		this.id = id;
	}

	/**
	 * Empty constructor, must be inicializated by setters
	 */
	public Pedestrian() {
	};

	/**
	 * Gets coordinate
	 * 
	 * @return coordinate
	 */
	public LatLng getPos() {
		return pos;
	}

	/**
	 * Gets status
	 * 
	 * @return status, see static values of {@link Pedestrian}
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets id
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets point
	 * 
	 * @return point
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * Sets id
	 * 
	 * @param id
	 *            of pedestrian
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets point
	 * 
	 * @param point
	 *            of pedestrian
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Sets geographical coordinate
	 * 
	 * @param pos
	 *            coordinate of pedestrian
	 */
	public void setPos(LatLng pos) {
		this.pos = pos;
	}

	/**
	 * Sets status
	 * 
	 * @param status
	 *            of pedestrian
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Sets behaviour of the {@link PedestrianAgent} associated to this
	 * {@link Pedestrian}
	 * 
	 * @param behaviour
	 *            Usually a son of {@link PedestrianBehav}
	 */
	public void setBehaviourClass(String behaviour) {
		this.behaviour = behaviour;
	}

	/**
	 * Gets behaviour of pedestrian
	 * 
	 * @return behaviour
	 */
	public String getBehaviourClass() {
		return behaviour;
	}

	/**
	 * Gets range of vision, in tiles
	 * 
	 * @return range vision
	 */
	public int getVision() {
		return vision;
	}

	/**
	 * Gets speed of pedestrian in tiles
	 * 
	 * @return speed of pedestrian in tiles
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets number of clones of this pedestrian
	 * 
	 * @return number of clones
	 */
	public int getClones() {
		return clones;
	}

	/**
	 * Initializes some pedestrian parameters with the scenario configuration
	 * values
	 * 
	 * @param vision
	 *            in tiles
	 * @param speed
	 *            in tiles
	 * @param clones
	 *            of the pedestrian
	 */
	public void setScenData(int vision, int speed, int clones) {
		this.vision = vision;
		this.speed = speed;
		this.clones = clones;
	}

	/**
	 * Adds a new objective that the pedestrian will try to reach
	 * 
	 * @param coord
	 *            Geographical coordinate of the new objective
	 * @return true if the pedestrian did not already contain the objective
	 */
	public boolean addObjective(LatLng coord) {
		if (objectives == null)
			objectives = new HashSet<LatLng>();

		return objectives.add(coord);
	}

	/**
	 * Gets a set of pedestrian objectives
	 * 
	 * @return set of objectives
	 */
	public Set<LatLng> getObjectives() {
		return objectives;
	}

	/**
	 * Gets the extra arguments for the choose method of {@link PedestrianBehav}
	 * 
	 * @return args
	 */
	public Object[] getChooseArgs() {
		if (behaviour
				.equals("behaviours.people.flood.KnownSafepointPedestrianBehav")) {
			return new Object[] { objectives };
		} else {
			return new Object[0];
		}
	}
}
