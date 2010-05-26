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

package behaviours.flood;

import agents.EnvironmentAgent;
import agents.flood.WaterSourceAgent;
import util.AgentHelper;
import util.jcoord.LatLng;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * {@link Behaviour} that sends water to a {@link EnvironmentAgent}
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class WaterSourceBehav extends Behaviour {

	private LatLng coord;
	private short water;
	private AID envAID;
	private Agent agt;

	/**
	 * {@link WaterSourceBehav} constructor
	 * 
	 * @param args
	 *            The array must contain an {@link Agent} (owner of this
	 *            behaviour, usually a {@link WaterSourceAgent}), an Evironment
	 *            {@link AID}, a {@link LatLng} and a {@link Short} (water
	 *            quantity).
	 */
	public WaterSourceBehav(Object[] args) {
		super((Agent) args[0]);
		agt = (Agent) args[0];
		// Agent a, AID envAID, LatLng coord, short water
		envAID = (AID) args[1];
		coord = (LatLng) args[2];
		water = (Short) args[3];
	}

	@Override
	public void action() {
		if (myAgent == null)
			myAgent = agt;
		// Inundar casilla
		String content = Double.toString(coord.getLat()) + " "
				+ Double.toString(coord.getLng()) + " " + Short.toString(water);
		AgentHelper.send(myAgent, envAID, ACLMessage.PROPOSE, "add-water",
				content);

		myAgent.removeBehaviour(this);
	}

	@Override
	public boolean done() {
		return false;
	}
}