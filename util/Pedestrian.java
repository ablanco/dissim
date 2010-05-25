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
	 * Creates a pedestrian on a coordinate
	 * 
	 * @param pos
	 *            coordinate
	 */
	public Pedestrian(LatLng pos) {
		this.pos = pos;
	}

	/**
	 * Creates a pedestrian on a coordinate, also gives an id and status
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
	 * Creates a pedestrian into a point, also gives an id and status
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
	 * Empty constructor, must be inicializated by sets
	 */
	public Pedestrian() {
	};

	/**
	 * Get coordinate
	 * 
	 * @return coordinate
	 */
	public LatLng getPos() {
		return pos;
	}

	/**
	 * Get status
	 * 
	 * @return status, see static values of <Pedestrian>
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get point
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
	 * Set coordinate
	 * 
	 * @param pos
	 *            coordinate of pedestrian
	 */
	public void setPos(LatLng pos) {
		this.pos = pos;
	}

	/**
	 * Set status
	 * 
	 * @param status
	 *            of pedestrian
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Set behaviors so we can choose between them
	 * 
	 * @param behaviour
	 *            we want to manage pedestrian
	 */
	public void setBehaviourClass(String behaviour) {
		this.behaviour = behaviour;
	}

	/**
	 * Gets behaviors of pedestrian
	 * 
	 * @return behaviors
	 */
	public String getBehaviourClass() {
		return behaviour;
	}

	/**
	 * Get range of vision, in tiles
	 * 
	 * @return range vision
	 */
	public int getVision() {
		return vision;
	}

	/**
	 * Get speed of pedestrian in tiles
	 * 
	 * @return
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Get number of clones of this pedestrian
	 * 
	 * @return number of clones
	 */
	public int getClones() {
		return clones;
	}

	/**
	 * Initializes some pedestrian parameters to the scenario configuration
	 * values
	 * 
	 * @param vision
	 *            in tiles
	 * @param speed
	 *            in tiles
	 * @param clones
	 *            of the same pedestrian
	 */
	public void setScenData(int vision, int speed, int clones) {
		this.vision = vision;
		this.speed = speed;
		this.clones = clones;
	}

	/**
	 * Adds a new objective where the pedestrian would try to get there
	 * 
	 * @param coord
	 *            of the new objective
	 * @return true if added
	 */
	public boolean addObjective(LatLng coord) {
		if (objectives == null)
			objectives = new HashSet<LatLng>();

		return objectives.add(coord);
	}

	/**
	 * Get a list of pedestrian objectives
	 * 
	 * @return list of objectives
	 */
	public Set<LatLng> getObjectives() {
		return objectives;
	}

	/**
	 * Get args
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
