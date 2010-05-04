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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.DateAndTime;
import util.HexagonalGrid;
import util.Pedestrian;
import util.Scenario;

@SuppressWarnings("serial")
public class SyndicateBehav extends CyclicBehaviour {

	private long defaultPeriod = 500;
	private Scenario scen;
	private Map<String, Object[]> subscribers = new Hashtable<String, Object[]>();
	private HexagonalGrid grid;
	private DateAndTime dateTime;
	private Map<String, Pedestrian> people;

	public SyndicateBehav(Agent a, HexagonalGrid grid, DateAndTime dateTime,
			Scenario scen, Map<String, Pedestrian> people) {
		super(a);
		this.grid = grid;
		this.dateTime = dateTime;
		this.scen = scen;
		this.people = people;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchConversationId("syndicate");
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			String type = null;
			AID aid = null;
			try {
				Object[] data = (Object[]) msg.getContentObject();
				type = (String) data[0];
				aid = (AID) data[1];
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			// Sindicarse
			if (msg.getPerformative() == ACLMessage.SUBSCRIBE && aid != null) {
				Object[] data = subscribers.get(type);
				Set<AID> receivers = null;
				if (data != null) {
					myAgent.removeBehaviour((Behaviour) data[0]);
					receivers = (Set<AID>) data[1];
				} else {
					data = new Object[2];
					receivers = new TreeSet<AID>();
				}
				receivers.add(aid);
				Behaviour behav = createBehav(type, receivers);
				myAgent.addBehaviour(behav);
				data[0] = behav;
				data[1] = receivers;
				subscribers.put(type, data);
			}
			// Desregistrarse
			else if (msg.getPerformative() == ACLMessage.CANCEL && aid != null) {
				Object[] data = subscribers.remove(type);
				if (data != null) {
					myAgent.removeBehaviour((Behaviour) data[0]);
					Set<AID> receivers = (Set<AID>) data[1];
					receivers.remove(aid);
					if (receivers.size() > 0) {
						Behaviour behav = createBehav(type, receivers);
						myAgent.addBehaviour(behav);
						data[0] = behav;
						data[1] = receivers;
						subscribers.put(type, data);
					}
				}
			}
		} else {
			block();
		}
	}

	private Behaviour createBehav(String type, Set<AID> receivers) {
		Behaviour behav = null;
		if (type.equals("visor")) {
			behav = new SendUpdateBehav(myAgent, scen.getUpdateVisorPeriod(),
					receivers, "update", grid, dateTime, people,
					scen.getName(), scen.getDescription());
		} else if (type.equals("kml")) {
			behav = new SendUpdateBehav(myAgent, scen.getUpdateKMLPeriod(),
					receivers, "update", grid, dateTime, people,
					scen.getName(), scen.getDescription());
		} else {
			// Default case
			behav = new SendUpdateBehav(myAgent, defaultPeriod, receivers,
					"update", grid, dateTime, people, scen.getName(), scen
							.getDescription());
		}
		return behav;
	}

}
