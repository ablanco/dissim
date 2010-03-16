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

import java.io.File;

public class Util {

	/**
	 * Default Temp Directory
	 */
	public static String defaultName = "PFC-Catastrofes";
	
	
	/**
	 * Gets machine Temp Directory
	 * @return
	 */
	public static String getTempPath(){
		 String path = null;
		try{
			File f = File.createTempFile("Chanicidad", null);

		path = f.getAbsolutePath();
		path = (String) path.subSequence(0, path.length()
				- f.getName().length());
		//Tenemos el path al directorio tmp
		f.deleteOnExit();
		f.delete();
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println("No se ha podido crear un fichero en el directorio temporal ");
		}
		return path;
	}

	/**
	 * Gets/create Default temp Directorory
	 * @return
	 */
	public static File getDefaultTempDir(){
		String path = getTempPath();
		File f = new File(path+defaultName);
		if (!f.exists() && !f.isDirectory()){
			f.mkdir();
		}
		return f;
	}


}
