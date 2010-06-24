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
 * For the simulation sometimes we need some information to be stored, but
 * without permanent saving, like OSM files, so we store them in temporal files.
 * This class is a helper for doing that.
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class TempFiles {

	/**
	 * Default Temp Directory
	 */
	public static String defaultName = "DisSim";
	private static String tmpDir = null;

	/**
	 * Gets machine Temp Directory
	 * 
	 * @return temp Path of temporal directory
	 */
	public static String getTempDirPath() {
		if (tmpDir == null) {
			try {
				File f = File.createTempFile("Auxiliar", null);

				tmpDir = f.getAbsolutePath();
				tmpDir = (String) tmpDir.subSequence(0, tmpDir.length()
						- f.getName().length());
				// Tenemos el path al directorio tmp
				f.deleteOnExit();
				f.delete();
			} catch (Exception e) {
				System.err
						.println("I wasn't able to create a file inside temp directory");
				e.printStackTrace();
				return "ERROR";
			}
		}
		return tmpDir;
	}

	/**
	 * Gets/creates Default temp directory
	 * 
	 * @return temp {@link File} (directory)
	 */
	public static File getDefaultTempDir() {
		String path = getTempDirPath();
		File f = new File(path + defaultName);
		if (!f.exists() && !f.isDirectory()) {
			f.mkdir();
		}
		return f;
	}

}
