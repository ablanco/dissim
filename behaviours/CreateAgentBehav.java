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
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class CreateAgentBehav extends OneShotBehaviour {

	private static final long serialVersionUID = -1618019490277716693L;

	protected Object[] arguments;
	protected String name;
	protected String agtClass;

	/**
	 * 
	 * @param agt
	 *            Agente que llama a este constructor
	 * @param name
	 *            Nombre del agente a crear
	 * @param agtClass
	 *            Clase del agente a crear
	 * @param arguments
	 *            Argumentos a pasar al agente a crear
	 */
	public CreateAgentBehav(Agent agt, String name, String agtClass,
			Object[] arguments) {
		super(agt);
		this.name = name;
		this.agtClass = agtClass;
		this.arguments = arguments;
	}

	@Override
	public void action() {
		AgentContainer container = myAgent.getContainerController();
		AgentController agtctrl;
		try {
			agtctrl = container.createNewAgent(name, agtClass, arguments);
			agtctrl.start();
		} catch (ControllerException e) {
			System.err.println(myAgent.getLocalName()
					+ " -> Error creating an agent of type " + agtClass
					+ " with name " + name);
			e.printStackTrace();
		}
	}
}
