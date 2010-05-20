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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

import util.HexagonalGrid;
import util.Pedestrian;
import util.Point;
import util.flood.FloodHexagonalGrid;

@SuppressWarnings("serial")
public class InterGridBehav extends CyclicBehaviour {

	public static final String WATER = "w";
	public static final String WATER_SET = "wset";
	public static final String WATER_INCREASE = "winc";
	public static final String PEOPLE = "p";
	public static final String PEOPLE_SET = "pset";

	private MessageTemplate mt = MessageTemplate
			.MatchConversationId("intergrid");
	private HexagonalGrid grid;
	private Map<String, Pedestrian> people;

	public InterGridBehav(Agent agt, HexagonalGrid grid,
			Map<String, Pedestrian> people) {
		super(agt);
		this.grid = grid;
		this.people = people;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			String[] data = msg.getContent().split(" ");
			String comm = data[0];
			if (comm.startsWith(WATER)) {
				// Movimiento de agua
				FloodHexagonalGrid fgrid = (FloodHexagonalGrid) grid;
				int x = Integer.parseInt(data[1]);
				int y = Integer.parseInt(data[2]);
				short w = Short.parseShort(data[3]);
				if (comm.equals(WATER_SET)) {
					fgrid.setWaterValue(x, y, w);
				} else if (comm.equals(WATER_INCREASE)) {
					fgrid.increaseValue(x, y, w);
				}
			} else if (comm.startsWith(PEOPLE)) {
				// Movimiento de personas
				String id = data[1];
				int x = Integer.parseInt(data[2]);
				int y = Integer.parseInt(data[3]);
				int s = Integer.parseInt(data[4]);
				if (comm.equals(PEOPLE_SET))
					people.put(id, new Pedestrian(new Point(x, y), s, id));
			}
		} else {
			block();
		}
	}

}
