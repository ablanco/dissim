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
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class CreateAgentBehav extends OneShotBehaviour {

	private static final long serialVersionUID = 1700469108759727145L;

	public CreateAgentBehav(Agent a) {
		super(a);
	}

	@Override
	public void action() {
		Object[] arguments = new Object[] { "", "" }; // TODO
		PlatformController plataforma = myAgent.getContainerController();
		AgentController agtctrl;
		try {
			agtctrl = plataforma.createNewAgent("B", "agents.Bernardo",
					arguments); // TODO
			agtctrl.start();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

}
