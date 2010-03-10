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

@SuppressWarnings("serial")
public class PacmanBehav extends CyclicBehaviour {

	private AID env;
	private int x; // Initial position
	private int y;
	private int d; // Distancia de visión
	private long period;
	private int step = 0;
	private MessageTemplate mt = MessageTemplate.MatchAll();

	public PacmanBehav(Agent a, long period, AID env, int x, int y, int d) {
		super(a);
		if (env == null)
			throw new IllegalArgumentException(
					"The enviroment AID cannot be null");
		this.env = env;
		this.period = period;
		this.x = x;
		this.y = y;
		this.d = d;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		ACLMessage msg;
		switch (step) {
		case 0:
			// Pedir adyacentes
			String content = Integer.toString(x) + " " + Integer.toString(y)
					+ " " + Integer.toString(d);
			mt = AgentHelper.send(myAgent, env, ACLMessage.REQUEST,
					"adjacents-grid", content);
			step = 1;
		case 1:
			msg = myAgent.receive(mt);
			if (msg != null) {
				try {
					Set<Point> adjacents = (Set<Point>) msg.getContentObject();
					for (Point pt : adjacents) {
						// TODO Buscar hacia dnd moverse
					}
					// TODO Informar al entorno del movimiento
					step = 0;
				} catch (UnreadableException e) {
					e.printStackTrace();
					step = 0;
				}
			} else {
				block();
			}
			break;
		}
	}

	public void setEnv(AID env) {
		this.env = env;
	}

}
