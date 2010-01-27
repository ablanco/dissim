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

package webservices;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import util.jcoord.LatLng;

public class GetAltitude implements SOAPHandler<SOAPMessageContext> {

	public static final String BEST_AVAIBLE = "-1";
	public static final String METERS = "METERS";
	public static final String FEET = "FEET";

	private String X_Value, Y_Value, Source_Layer, Elevation_Only,
			Elevation_Units;
	private String WSDL = "http://gisdata.usgs.gov/XMLWebServices2/Elevation_service.asmx?WSDL";
	private String IP_ORIGEN = "192.168.2.1";

	// private String DIR_WEB = "http://gisdata.usgs.gov/XMLWebServices2/";

	public GetAltitude() {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public void close(MessageContext arg0) {

	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext messageContext) {
		SOAPMessage msg = messageContext.getMessage();
		boolean bolMsgSalida = (Boolean) messageContext
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		boolean noProblem = true;

		// Obtenemos el contenedor del mensaje SOAP
		SOAPPart sp = msg.getSOAPPart();

		// A partir del contendor, obtenemos el nodo "Envelope"
		SOAPEnvelope env = null;
		try {
			env = sp.getEnvelope();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			noProblem = false;
		}

		// Instanciamos un objeto SOAPFactory para crear cualquier elemento
		// perteneciente a un mensaje SOAP, en nuestro caso, los nodos que
		// formarán la cabecera
		SOAPFactory soapFactory = null;
		try {
			soapFactory = SOAPFactory.newInstance();
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}

		// Definimos los elementos a incluir en el mensaje
		SOAPElement soapElementoCabecera = null;
		try {
			soapElementoCabecera = soapFactory.createElement("cabeceraSOAP",
					"", WSDL);
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}

		SOAPElement soapIpOrigen = null;
		try {
			soapIpOrigen = soapFactory.createElement(IP_ORIGEN, "",
					"http://gisdata.usgs.gov/XMLWebServices2/");
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}

		// Rellenamos la información del nodo ipOrigen
		try {
			soapIpOrigen.addTextNode(IP_ORIGEN);
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}

		// Incluimos los elementos dentro de los objetos correspondientes
		try {
			soapElementoCabecera.addChildElement(soapIpOrigen);
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}

		SOAPHeader soapHeader = null;
		try {
			soapHeader = env.addHeader();
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		} // Crea un elemento cabecera
		// SOAP

		try {
			soapHeader.addChildElement(soapElementoCabecera);
		} catch (SOAPException e) {
			e.printStackTrace();
			noProblem = false;
		}
		return noProblem;
	}

	/*
	 * <X_Value>string</X_Value> <Y_Value>string</Y_Value>
	 * <Elevation_Units>string</Elevation_Units>
	 * <Source_Layer>string</Source_Layer>
	 * <Elevation_Only>string</Elevation_Only>
	 */
	public double getAltitude(LatLng coord, String sourceLayer,
			String elevationUnits, boolean elevationOnly) {
		double altitude = 0;

		if (elevationOnly)
			Elevation_Only = "TRUE";
		else
			Elevation_Only = "FALSE";
		Elevation_Units = elevationUnits;
		Source_Layer = sourceLayer;
		X_Value = Double.toString(coord.getLat());
		Y_Value = Double.toString(coord.getLng());

		return altitude;
	}

}
