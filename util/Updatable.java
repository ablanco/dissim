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

import agents.UpdateAgent;
import jade.core.AID;
import jade.core.Agent;

/**
 * All the clients of {@link UpdateAgent}s must implement this.
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public interface Updatable {

	/**
	 * Sets an agent to the client, this agent will receive the updates
	 * 
	 * @param agt
	 *            Usually an {@link UpdateAgent}
	 */
	public void setAgent(Agent agt);

	/**
	 * Initializes client, called by {@link UpdateAgent}
	 */
	public void init();

	/**
	 * Asks the client to process a new update
	 * 
	 * @param obj
	 *            usually a {@link Snapshot}
	 * @param sender
	 *            who sends the update
	 * @throws IllegalArgumentException
	 *             if is not the object type we where expecting
	 */
	public void update(Object obj, AID sender) throws IllegalArgumentException;

	/**
	 * Finalizes the client, called by {@link UpdateAgent}
	 */
	public void finish();

	/**
	 * Gets the type of the client
	 * 
	 * @return type
	 */
	public String getType();

}
