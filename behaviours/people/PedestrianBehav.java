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
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Set;

import util.AgentHelper;
import util.Pedestrian;
import util.Point;
import util.Scenario;
import agents.people.PedestrianAgent;
import behaviours.AdjacentsGridBehav;

/**
 * {@link Behaviour} that makes a {@link PedestrianAgent} move around and save
 * himself.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public abstract class PedestrianBehav extends Behaviour {

	private AID env;
	private double lat; // Posición en coordenadas
	private double lng;
	/**
	 * Vision distance in tiles
	 */
	protected int d;
	/**
	 * Speed in tiles
	 */
	protected int s;
	/**
	 * What kind of position are we working with
	 */
	private String type = AdjacentsGridBehav.LAT_LNG;
	private int step = 0;
	private MessageTemplate mt = MessageTemplate.MatchAll();
	protected Point position = null; // Posición en columna y fila
	private int status = Pedestrian.HEALTHY;
	protected Scenario scen;
	private Agent agt;

	/**
	 * {@link PedestrianAgent} constructor
	 * 
	 * @param args
	 *            The array must contain an {@link Agent} (owner of the
	 *            behaviour, usually a {@link PedestrianAgent}), an Environment
	 *            {@link AID} (initial environment), a {@link Scenario}, a
	 *            {@link Double} (latitude), a {@link Double} (longitude), a
	 *            {@link Integer} (distance of vison in tiles) and a
	 *            {@link Integer} (speed in tiles).
	 */
	public PedestrianBehav(Object[] args) {
		super((Agent) args[0]);
		agt = (Agent) args[0];
		// Agent a, AID env, Scenario scen, double lat, double lng, int d, int s
		if (args[1] == null)
			throw new IllegalArgumentException(
					"The enviroment AID cannot be null");
		env = (AID) args[1];
		lat = (Double) args[3];
		lng = (Double) args[4];
		d = (Integer) args[5];
		s = (Integer) args[6];
		// La velocidad no puede ser superior a la distancia de visión
		if (s > d)
			s = d;
		scen = (Scenario) args[2];
		chooseArgs((Object[]) args[7]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		if (myAgent == null)
			myAgent = agt;

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
				content += Integer.toString(position.getCol()) + " "
						+ Integer.toString(position.getRow()) + " "
						+ Integer.toString(d);
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
						pmejor = choose(adjacents);
					} catch (YouAreDeadException e) {
						AgentHelper.send(myAgent, env, ACLMessage.CANCEL,
								"register-people", myAgent.getLocalName());
						myAgent.doDelete();
						return;
					} catch (YouAreSafeException e) {
						status = Pedestrian.SAFE;
						pmejor = e.getPosition();
					}

					step = 0;

					// Si ha encontrado a donde moverse
					if (pmejor != null) {
						position = pmejor;
						type = AdjacentsGridBehav.POSITION;

						// Informamos al entorno del movimiento
						content = myAgent.getLocalName() + " "
								+ Integer.toString(position.getCol()) + " "
								+ Integer.toString(position.getRow());

						// Si el estado no es el normal informamos
						if (status != Pedestrian.HEALTHY)
							content += " " + Integer.toString(status);

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

				// Si el agente está a salvo acaba su papel en la simulación,
				// así que lo borramos
				if (status == Pedestrian.SAFE) {
					myAgent.doDelete();
					return;
				}

				step = 0;
				myAgent.removeBehaviour(this);
			} else {
				block();
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return false;
	}

	/**
	 * Method that must be extended by the actual behaviours. It chooses where
	 * to move from a {@link Set} of adjacents {@link Point}.
	 * 
	 * @param adjacents
	 *            {@link Set}<{@link Point}>
	 * @return
	 * @throws YouAreDeadException
	 *             When the agent dies
	 * @throws YouAreSafeException
	 *             When the agent reaches a safepoint
	 */
	protected abstract Point choose(Set<Point> adjacents)
			throws YouAreDeadException, YouAreSafeException;

	/**
	 * It's used for setting extra arguments for the choose method.
	 * 
	 * @param args
	 *            {@link Object}[]
	 */
	public abstract void chooseArgs(Object[] args);
}
