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

package util;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.io.Serializable;

public class AgentHelper {

	private AgentHelper() {
		// No instanciable
	}

	/**
	 * Search agents in the DF Service
	 * 
	 * @param agt
	 *            Agent who search
	 * @param service
	 *            Service that the searched agents must provide
	 * @return An agent description array with the founded agents (may be empty
	 *         if zero agents were found)
	 */
	public static DFAgentDescription[] search(Agent agt, String service) {
		return search(agt, new String[] { service });
	}

	/**
	 * Search agents in the DF Service
	 * 
	 * @param agt
	 *            Agent who search
	 * @param services
	 *            Services that the searched agents must provide
	 * @return An agent description array with the founded agents (may be empty
	 *         if zero agents were found)
	 */
	public static DFAgentDescription[] search(Agent agt, String[] services) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd;
		for (String srv : services) {
			sd = new ServiceDescription();
			sd.setType(srv);
			dfd.addServices(sd);
		}
		DFAgentDescription[] result = new DFAgentDescription[0];
		try {
			result = DFService.search(agt, dfd);
			if (result.length < 1) {
				System.err.println("Error searching from " + agt.getLocalName()
						+ ". Found " + result.length + " agents.");
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Register an agent into the DF Service
	 * 
	 * @param agt
	 *            Agent to register
	 * @param service
	 *            Service that the agent provide
	 */
	public static void register(Agent agt, String service) {
		register(agt, new String[] { service });
	}

	/**
	 * Register an agent into the DF Service
	 * 
	 * @param agt
	 *            Agent to register
	 * @param services
	 *            Services that the agent provide
	 */
	public static void register(Agent agt, String[] services) {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(agt.getAID());
		ServiceDescription sd;
		for (String srv : services) {
			if (srv != null) {
				sd = new ServiceDescription();
				sd.setType(srv);
				sd.setName(agt.getName());
				dfd.addServices(sd);
			}
		}
		try {
			DFService.register(agt, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a message to another agent
	 * 
	 * @param sender
	 *            Agent who sends the message
	 * @param receiver
	 *            Agent who receives the message
	 * @param performative
	 *            Performative of the message, they are defined as static fields
	 *            in the ACLMessage class
	 * @param convId
	 *            Conversation ID (may be null)
	 * @param content
	 *            Content of the message (may be null), may be a String or may
	 *            be a Serializable object
	 * @return A template to receive the answer (if needed)
	 */
	public static MessageTemplate send(Agent sender, AID receiver,
			int performative, String convId, Object content) {
		return send(sender, new AID[] { receiver }, performative, convId,
				content);
	}

	/**
	 * Send a message to another agent
	 * 
	 * @param sender
	 *            Agent who sends the message
	 * @param receivers
	 *            Agents who receive the message
	 * @param performative
	 *            Performative of the message, they are defined as static fields
	 *            in the ACLMessage class
	 * @param convId
	 *            Conversation ID (may be null)
	 * @param content
	 *            Content of the message (may be null), may be a String or may
	 *            be a Serializable object
	 * @return A template to receive the answer (if needed)
	 */
	public static MessageTemplate send(Agent sender, AID[] receivers,
			int performative, String convId, Object content) {
		ACLMessage msg = new ACLMessage(performative);
		for (AID aid : receivers) {
			if (aid == null)
				return null;
			msg.addReceiver(aid);
		}
		if (convId != null)
			msg.setConversationId(convId);
		if (content != null) {
			if (content instanceof String) {
				msg.setContent((String) content);
			} else if (content instanceof Serializable) {
				try {
					msg.setContentObject((Serializable) content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		msg.setReplyWith(Long.toString(System.currentTimeMillis()));
		sender.send(msg);
		return MessageTemplate.MatchInReplyTo(msg.getReplyWith());
	}

}
