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

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import behaviours.AdjacentsGridBehav;

import util.AgentHelper;
import util.HexagonalGrid;
import util.Point;

@SuppressWarnings("serial")
public class RunawayBehav extends CyclicBehaviour {

	private AID env;
	private double lat; // Posición en coordenadas
	private double lng;
	private int x; // Posición en columna y fila
	private int y;
	private int d; // Distancia de visión
	private String type = AdjacentsGridBehav.LAT_LNG;
	private long period;
	private long previous;
	private int step = 0;
	private MessageTemplate mt = MessageTemplate.MatchAll();

	public RunawayBehav(Agent a, long period, AID env, double lat, double lng,
			int d) {
		super(a);
		if (env == null)
			throw new IllegalArgumentException(
					"The enviroment AID cannot be null");
		this.env = env;
		this.period = period;
		this.lat = lat;
		this.lng = lng;
		this.d = d;
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

					// Separar casillas inundadas de las secas
					Set<Point> water = new TreeSet<Point>();
					Set<Point> dry = new TreeSet<Point>();
					for (Point pt : adjacents) {
						if (pt.getW() > 0)
							water.add(pt);
						else
							dry.add(pt);
					}

					// Por si no ve agua, que no se mueva
					step = 0;

					// Si no tiene casillas secas a su alrededor está rodeado y
					// se ahoga
					if (dry.size() == 0) {
						// TODO Mejorar sistema para detectar ahogamiento
						System.out.println(myAgent.getLocalName()
								+ ": Me ahogo!!");
						AgentHelper.send(myAgent, env, ACLMessage.CANCEL,
								"register-people", myAgent.getLocalName());
						myAgent.doDelete();
						return;
					}

					if (water.size() > 0) {
						// Buscar la casilla seca más alejada de las inundadas
						int dmejor = Integer.MIN_VALUE;
						Point pmejor = null;
						for (Point pt : dry) {
							int dist = 0;
							for (Point w : water) {
								dist += HexagonalGrid.distance(pt, w);
							}
							dist /= water.size();
							if (dist > dmejor)
								pmejor = pt;
						}

						// Si no se ha encontrado una mejor se mueve a una seca
						// al azar
						if (pmejor != null) {
							Point[] arrdry = new Point[dry.size()];
							arrdry = dry.toArray(arrdry);
							Random rnd = new Random(System.currentTimeMillis());
							pmejor = arrdry[rnd.nextInt(arrdry.length)];
						}

						// El agente avanza una casilla aunque tenga una
						// distancia de visión mayor
						if (type.equals(AdjacentsGridBehav.POSITION))
							pmejor = HexagonalGrid.nearestHexagon(new Point(x,
									y), pmejor);

						x = pmejor.getCol();
						y = pmejor.getRow();
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
