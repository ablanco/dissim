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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.lang.reflect.Constructor;

import util.DateAndTime;

@SuppressWarnings("serial")
public class ReceiveClockTickBehav extends CyclicBehaviour {

	private Class<?> onTickClass;
	private Behaviour onTickBehav = null;
	private DateAndTime dateTime = null;
	private Object[] arguments;
	private MessageTemplate mt = MessageTemplate.MatchConversationId("clock");

	public ReceiveClockTickBehav(Agent agt, Class<?> onTickClass,
			Object[] arguments, DateAndTime dateTime) {
		this.onTickClass = onTickClass;
		this.arguments = arguments;
		this.dateTime = dateTime;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, procesarlo
			if (dateTime != null)
				dateTime.parseAndSetTime(msg.getContent());

			if (onTickBehav == null) {
				// Carga y crea un objeto de la clase pasada, por reflexión
				try {
					Constructor<?> ct = onTickClass
							.getConstructor(new Class[] { Object[].class });
					onTickBehav = (Behaviour) ct
							.newInstance(new Object[] { arguments });
				} catch (Exception e) {
					System.err
							.println("An error happened while creating an instance of "
									+ onTickClass.toString());
					e.printStackTrace();
					myAgent.doDelete();
				}
			}

			myAgent.addBehaviour(onTickBehav);
		} else {
			block();
		}
	}

}
