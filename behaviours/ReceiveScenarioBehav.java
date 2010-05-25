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

package behaviours;

import agents.CreatorAgent;
import jade.core.behaviours.Behaviour;
import util.Scenario;

/**
 * Abstract {@link Behaviour} to be extended and used by the agents that ask
 * {@link CreatorAgent} for the {@link Scenario}
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public abstract class ReceiveScenarioBehav extends Behaviour {

	protected Scenario scen = null;

	/**
	 * Variable used to control when this {@link Behaviour} has finished
	 */
	protected boolean done = false;

	/**
	 * {@link CreatorAgent} calls this method to set the {@link Scenario}
	 * instance.
	 * 
	 * @param scen
	 *            {@link Scenario}
	 */
	public void setScenario(Scenario scen) {
		this.scen = scen;
	}

	@Override
	public boolean done() {
		return done;
	}

}
