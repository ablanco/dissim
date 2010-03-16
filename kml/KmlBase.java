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

package kml;

import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;

public class KmlBase {
	protected Kml kml;
	protected Folder folder;

	public KmlBase(String name, String description) {
		kml = new Kml();
		folder = KmlUtils.newFolder(kml, name, description);
	}

	public KmlBase() {
		kml = new Kml();
		folder = KmlUtils.newFolder(kml);
	}

	public void setName(String name) {
		folder.setName(name);
	}

	public void setDescription(String description) {
		folder.setDescription(description);
	}

	public Kml getKml() {
		return kml;
	}

	public String getName() {
		if (folder.getName() != null && folder.getName().length() != 0)
			return folder.getName();
		return "DefaultName";
	}

	public String getDescription() {
		if (folder.getDescription() != null
				&& folder.getDescription().length() != 0)
			return folder.getDescription();
		return "DefaultDescriptor";
	}
}
