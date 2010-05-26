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

package osm;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Tags for the info contained into the xml file downloaded from OSM
 * 
 * @author Manuel Gomar, Alejando Blanco
 * 
 */
public class OsmTag {

	private String type;
	private String value;

	private OsmTag(String type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Gets name property
	 * 
	 * @return name
	 */
	public String getName() {
		return type;
	}

	/**
	 * Gets value from the property
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return type + "=" + value;
	}

	/**
	 * Given a xml parsed node from OSM check if has valuable info and build a
	 * tag.
	 * 
	 * @param node
	 *            from OSM webservice
	 * @return a {@link OsmTag} if the node has any value
	 */
	public static OsmTag getTag(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		String type = attributes.item(0).getNodeValue();
		String value = attributes.item(1).getNodeValue();
		if (type != null && value != null) {
			if (type.contains("tiger:") || type.contains("created_by")
					|| type.contains("admin_level") || type.contains("source")
					|| type.contains("attribution") || type.contains("NHD:")
					|| type.contains("gnis:") || type.contains("addr:")
					|| type.contains("network") || type.contains("operator")
					|| type.contains("is_in:")) {
				// trash
				return null;
			}
			return new OsmTag(type, value);
		} else {
			return null;
		}
	}
}
