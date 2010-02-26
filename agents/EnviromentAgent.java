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

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import util.HexagonalGrid;
import util.Logger;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import behaviours.AdjacentsGridBehav;
import behaviours.QueryGridBehav;
import behaviours.SyndicateBehav;
import behaviours.flood.AddWaterBehav;
import behaviours.flood.RegisterFloodTileBehav;
import behaviours.flood.UpdateFloodGridBehav;

@SuppressWarnings("serial")
public class EnviromentAgent extends Agent {

	private HexagonalGrid grid = null;
	private Logger logger = new Logger();

	@Override
	protected void setup() {
		Scenario scen = Scenario.getCurrentScenario();
//		logger = scen.getDefaultLogger();
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 0) {
			grid = scen.getGrid();
		} else {
			logger.errorln(getLocalName() + " wrong arguments.");
			doDelete();
		}

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd;

		// Añadir comportamientos
		addBehaviour(new AdjacentsGridBehav(grid));
		addBehaviour(new QueryGridBehav(grid));
		addBehaviour(new SyndicateBehav());

		sd = new ServiceDescription();
		sd.setType("grid-querying");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("adjacents-grid");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("syndicate");
		sd.setName(getName());
		dfd.addServices(sd);

		// Si es una inundación
		if (scen instanceof FloodScenario) {
			FloodScenario fscen = (FloodScenario) scen;

			// En el caso de que el agua se agentifique
			if (fscen.useWaterAgents()) {
				addBehaviour(new RegisterFloodTileBehav(
						(FloodHexagonalGrid) grid));

				sd = new ServiceDescription();
				sd.setType("flood-registering");
				sd.setName(getName());
				dfd.addServices(sd);
			}
			// Si no se agentifica
			else {
				addBehaviour(new AddWaterBehav((FloodHexagonalGrid) grid));

				sd = new ServiceDescription();
				sd.setType("add-water");
				sd.setName(getName());
				dfd.addServices(sd);

				addBehaviour(new UpdateFloodGridBehav(this, fscen
						.getFloodUpdateTime(), (FloodHexagonalGrid) grid));
			}
		}

		try {
			// Registrarse con el agente DF
			DFService.register(this, dfd);

			// Obtener agente creador
			dfd = new DFAgentDescription();
			sd = new ServiceDescription();
			sd.setType("creator");
			dfd.addServices(sd);
			DFAgentDescription[] result = DFService.search(this, dfd);
			if (result.length != 1) {
				logger.errorln("Error searching for the creator agent. Found "
						+ result.length + " agents.");
				doDelete();
			}
			AID creatorAID = result[0].getName();
			// Mandar mensaje al agente creador
			ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
			msg.addReceiver(creatorAID);
			send(msg);
		} catch (FIPAException e) {
			e.printStackTrace(logger.getError());
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		// Desregistrarse de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace(logger.getError());
		}
		logger.println("Enviroment-agent " + getAID().getName()
				+ " terminating.");
	}
}
