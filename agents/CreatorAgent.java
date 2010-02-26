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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.ListIterator;

import test.SimulationTest;
import util.Logger;
import util.Point;
import util.Scenario;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import behaviours.CreateAgentBehav;

@SuppressWarnings("serial")
public class CreatorAgent extends Agent {

	private Scenario scen = null;
	private Logger logger = new Logger();

	@Override
	protected void setup() {
		// TODO DEBUG
		// Obtener argumentos
		Object[] agtArgs = getArguments();
		int opt = 0;
		String[] strArgs = new String[] { Boolean.toString(false) };
		if (agtArgs != null) {
			opt = Integer.parseInt((String) agtArgs[0]);
			strArgs = new String[agtArgs.length - 1];
			for (int i = 1; i < agtArgs.length; i++) {
				strArgs[i - 1] = (String) agtArgs[i];
			}
		}
		SimulationTest.generateScenario(opt, strArgs);
		// FIN DEBUG

		scen = Scenario.getCurrentScenario();
		if (scen != null) {
			// logger = scen.getDefaultLogger();

			// Enviroment
			Object[] arguments = new Object[0];
			addBehaviour(new CreateAgentBehav(this, "Enviroment",
					"agents.EnviromentAgent", scen.getNumEnv(), arguments));

			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("creator");
			sd.setName(getName());
			dfd.addServices(sd);
			// Registrarse con el agente DF
			try {
				DFService.register(this, dfd);
			} catch (FIPAException e) {
				e.printStackTrace(logger.getError());
			}

			// Esperar a que el entorno esté inicializado
			addBehaviour(new WaitForReadyBehav());
		}
	}

	protected class WaitForReadyBehav extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				// Mensaje recibido, hay que procesarlo
				if (msg.getPerformative() != ACLMessage.CONFIRM)
					return;
				Object[] arguments;
				// Si es una inundación
				if (scen instanceof FloodScenario) {
					FloodScenario fscen = (FloodScenario) scen;
					ListIterator<WaterSource> it = fscen.waterSourcesIterator();
					while (it.hasNext()) {
						WaterSource ws = it.next();
						Point p = new Point(0, 0); // TODO
													// scen.coordToTile(ws.getCoord());
						arguments = new Object[] { Integer.toString(p.getX()),
								Integer.toString(p.getY()),
								Short.toString(ws.getWater()),
								Long.toString(ws.getRhythm()) };
						myAgent.addBehaviour(new CreateAgentBehav(myAgent,
								"WaterSource", "agents.flood.WaterSourceAgent",
								1, arguments));
					}

				}

				// TODO DEBUG
				arguments = new Object[] { "gui.VisorFrame" };
				myAgent.addBehaviour(new CreateAgentBehav(myAgent,
						"DefaultVisor", "agents.UpdateAgent", 1, arguments));
			} else {
				block();
			}
		}

	}

}
