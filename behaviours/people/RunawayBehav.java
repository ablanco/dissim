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

package behaviours.people;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Set;

import util.AgentHelper;
import util.Point;
import agents.people.ranking.Ranking;
import agents.people.ranking.YouAreDeadException;
import behaviours.AdjacentsGridBehav;

@SuppressWarnings("serial")
public class RunawayBehav extends CyclicBehaviour {

	private AID env;
	private Ranking rank;
	private double lat; // Posición en coordenadas
	private double lng;
	private int x; // Posición en columna y fila
	private int y;
	private int d; // Distancia de visión
	private int s; // Velocidad
	private String type = AdjacentsGridBehav.LAT_LNG;
	private long period;
	private long previous;
	private int step = 0;
	private MessageTemplate mt = MessageTemplate.MatchAll();
	private Point position = null;

	public RunawayBehav(Agent a, long period, AID env, double lat, double lng,
			int d, int s, Ranking rank) {
		super(a);
		if (env == null)
			throw new IllegalArgumentException(
					"The enviroment AID cannot be null");
		this.env = env;
		this.period = period;
		this.lat = lat;
		this.lng = lng;
		this.d = d;
		if (s > d) // La velocidad no puede ser superior a la distancia de
			// visión
			this.s = d;
		else
			this.s = s;
		this.rank = rank;
		previous = System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		if ((System.currentTimeMillis() - previous) < period)
			return; // TODO Ugly

		previous = System.currentTimeMillis();
		ACLMessage msg;
		String content;
		switch (step) {
		case 0:
			content = type + " ";
			// Pedir adyacentes
			if (type.equals(AdjacentsGridBehav.LAT_LNG)) {
				content += Double.toString(lat) + " " + Double.toString(lng)
						+ " " + Integer.toString(d);
			} else {
				content += Integer.toString(x) + " " + Integer.toString(y)
						+ " " + Integer.toString(d);
			}
			mt = AgentHelper.send(myAgent, env, ACLMessage.REQUEST,
					"adjacents-grid", content);
			step = 1;
		case 1:
			msg = myAgent.receive(mt);
			if (msg != null) {
				try {
					Set<Point> adjacents = (Set<Point>) msg.getContentObject();

					Point pmejor = null;
					try {
						pmejor = rank.choose(adjacents, position, d, s);
					} catch (YouAreDeadException e) {
						AgentHelper.send(myAgent, env, ACLMessage.CANCEL,
								"register-people", myAgent.getLocalName());
						myAgent.doDelete();
						return;
					}

					step = 0;

					// Si ha encontrado a donde moverse
					if (pmejor != null) {
						x = pmejor.getCol();
						y = pmejor.getRow();
						position = pmejor;
						type = AdjacentsGridBehav.POSITION;

						// Informamos al entorno del movimiento
						content = myAgent.getLocalName() + " "
								+ Integer.toString(x) + " "
								+ Integer.toString(y);
						mt = AgentHelper.send(myAgent, env, ACLMessage.INFORM,
								"register-people", content);
						step = 2;
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
					step = 0;
				}
			} else {
				block();
			}
			break;
		case 2:
			msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.INFORM) {
					try {
						env = (AID) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				step = 0;
			} else {
				block();
			}
			break;
		}
	}

}
