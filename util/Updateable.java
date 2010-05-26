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

import jade.core.AID;
import jade.core.Agent;

/**
 * Interface for updateables objects, if need to objects that will be updated,
 * you must implement this class
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public interface Updateable {

	/**
	 * Sets an agent to the object, this agent will manage the updates
	 * 
	 * @param agt
	 *            Agent
	 */
	public void setAgent(Agent agt);

	/**
	 * Initializes parameters, if needed
	 */
	public void init();

	/**
	 * Receives and object, (usually a snapshot), read information and update
	 * the state of the simulation
	 * 
	 * @param obj
	 *            usually a snapshot
	 * @param sender
	 *            who sends the message
	 * @throws IllegalArgumentException
	 *             if is not the object type we where expecting
	 */
	public void update(Object obj, AID sender) throws IllegalArgumentException;

	/**
	 * Finalizes the object, if needed
	 */
	public void finish();

	/**
	 * Gets the type of the object
	 * 
	 * @return type
	 */
	public String getType();

}
