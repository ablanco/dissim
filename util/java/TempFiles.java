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

package util.java;

import java.io.File;

/**
 * For the simulation sometimes we need some information to be stored but with
 * no need of saving, like OSM files, because they may be updated, thats why we
 * stored in a temporary file, so can be downloaded a new version without having
 * all versions stored
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class TempFiles {

	/**
	 * Default Temp Directory
	 */
	public static String defaultName = "PFC-Catastrofes";

	/**
	 * Gets machine Temp Directory
	 * 
	 * @return temp Path
	 */
	public static String getTempPath() {
		String path = null;
		try {
			File f = File.createTempFile("Chanicidad", null); // TODO mejorar

			path = f.getAbsolutePath();
			path = (String) path.subSequence(0, path.length()
					- f.getName().length());
			// Tenemos el path al directorio tmp
			f.deleteOnExit();
			f.delete();
		} catch (Exception e) {
			System.err
					.println("No se ha podido crear un fichero en el directorio temporal ");
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * Gets/create Default temp directory
	 * 
	 * @return temp File
	 */
	public static File getDefaultTempDir() {
		String path = getTempPath();
		File f = new File(path + defaultName);
		if (!f.exists() && !f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

}
