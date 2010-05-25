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

package agents;

import java.util.ArrayList;

import util.AgentHelper;
import util.DateAndTime;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * {@link Agent} that synchronizes the simulation, it sends ticks to others
 * agents on a regular period.
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
@SuppressWarnings("serial")
public class ClockAgent extends Agent {

	private DateAndTime time;
	private AID[] receivers;
	private int minutes;

	@SuppressWarnings("unchecked")
	@Override
	protected void setup() {
		// Obtener argumentos
		long period = -1;
		ArrayList<AID> rec = null;
		Object[] args = getArguments();
		if (args.length == 4) {
			time = new DateAndTime((String) args[0]);
			period = ((Long) args[1]).longValue();
			minutes = ((Integer) args[2]).intValue();
			rec = (ArrayList<AID>) args[3];
		} else {
			System.err.println(getLocalName() + " wrong arguments.");
			doDelete();
		}
		receivers = rec.toArray(new AID[rec.size()]);
		addBehaviour(new SendTimeBehav(this, period));
	}

	/**
	 * Behaviour that actually sends the clock's tick.
	 * 
	 * @author Alejandro Blanco, Manuel Gomar
	 * 
	 */
	protected class SendTimeBehav extends TickerBehaviour {

		public SendTimeBehav(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			time.updateTime(minutes);
			AgentHelper.send(myAgent, receivers, ACLMessage.INFORM, "clock",
					time.toString());
		}

	}

}
