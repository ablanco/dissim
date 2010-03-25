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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Set;

import util.AgentHelper;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Pedestrian;
import util.Snapshot;

@SuppressWarnings("serial")
public class SendUpdateBehav extends TickerBehaviour {

	private Set<AID> to;
	private String convId;
	private HexagonalGrid grid;
	private DateAndTime dateTime;
	private Map<String, Pedestrian> people;
	private String name;
	private String description;

	public SendUpdateBehav(Agent a, long period, Set<AID> to, String convId,
			HexagonalGrid grid, DateAndTime dateTime,
			Map<String, Pedestrian> people, String name, String description) {
		super(a, period);
		this.to = to;
		this.convId = convId;
		this.grid = grid;
		this.dateTime = dateTime;
		this.people = people;
		this.name = name;
		this.description = description;
	}

	@Override
	protected void onTick() {
		AID[] receivers = new AID[to.size()];
		AgentHelper
				.send(myAgent, to.toArray(receivers), ACLMessage.INFORM,
						convId, new Snapshot(name, description, grid, dateTime,
								people));
	}

}
