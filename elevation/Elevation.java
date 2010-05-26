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

package elevation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.HexagonalGrid;
import util.Point;
import util.Scenario;
import util.jcoord.LatLng;

/*
 * SQLite -> http://www.zentus.com/sqlitejdbc/
 * MySQL -> http://dev.mysql.com/downloads/connector/j/
 * Postgre -> http://jdbc.postgresql.org/
 */

/**
 * Utilities to obtain the elevation data for a {@link HexagonalGrid}, in the
 * form of static methods. It queries a DataBase for the data, and if it doesn't
 * find it there the uses a webservice to obatin it, and updates the DataBase.
 * 
 * @see ElevationWS
 * 
 * @author Manuel Gomar, Alejandro Blanco
 */
public class Elevation {

	private Elevation() {
		// No instanciable
	}

	/**
	 * Method that obtains the elevation data for all the {@link Point} in the
	 * {@link HexagonalGrid}. It uses JDBC for the DataBase connection.
	 * 
	 * @param grid
	 *            {@link HexagonalGrid} to obtain the elevation data for
	 * @param server
	 *            Url of the DataBase server
	 * @param port
	 *            DataBase port
	 * @param db
	 *            Name of the DataBase
	 * @param user
	 *            DataBase user
	 * @param pass
	 *            DataBase password of the user
	 * @param driver
	 *            JDBC driver to use
	 */
	public static void getElevations(HexagonalGrid grid, String server,
			int port, String db, String user, String pass, String driver) {
		String clase = null;
		if (driver.equals("mysql")) {
			clase = "com.mysql.jdbc.Driver";
		} else if (driver.equals("postgresql")) {
			clase = "org.postgresql.Driver";
		} else if (driver.equals("sqlite")) {
			clase = "org.sqlite.JDBC";
		}

		try {
			Class.forName(clase);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Connection con = getConnection(driver, server, port, db, user, pass);

		// Ahora recorremos toda la matriz y buscamos/insertamos los valores de
		// las alturas
		double ilat = grid.getIncs()[0];
		double ilng = grid.getIncs()[1];

		int endCol = grid.getOffCol() + grid.getColumns();
		int endRow = grid.getOffRow() + grid.getRows();
		for (int col = grid.getOffCol(); col < endCol; col++) {
			for (int row = grid.getOffRow(); row < endRow; row++) {
				LatLng coord = grid.tileToCoord(new Point(col, row));
				PreparedStatement stmt = getNearPoints(con, coord, ilat, ilng);

				short value = Short.MIN_VALUE;

				try {
					double acum = 0;
					int counter = 0;
					ResultSet rs = stmt.getResultSet();
					while (rs.next()) {
						acum += rs.getDouble(1);
						counter++;
					}

					if (counter != 0) {
						// Quiere decir que tenemos mas de un resultado, hacemos
						// la media
						value = Scenario.doubleToInner(grid.getPrecision(),
								(acum / counter));
					} else {
						// Quiere decir que no tenemos ningun resultado, lo
						// preguntamos en el servicio web
						double elev = ElevationWS.getElevation(coord);
						insertNewElevation(con, coord, elev);
						value = Scenario.doubleToInner(grid.getPrecision(),
								elev);
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				grid.setTerrainValue(col, row, value);
			}
		}
		closeConnection(con);
	}

	/**
	 * Close the given connection
	 * 
	 * @param con
	 */
	private static void closeConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a SQL query to obtain information of the elevations near the
	 * coordinates
	 * 
	 * @param coord
	 *            Position
	 * @param ilat
	 *            Range on latitude
	 * @param ilng
	 *            Range on longitude
	 * @return
	 */
	private static PreparedStatement getNearPoints(Connection con,
			LatLng coord, double ilat, double ilng) {
		double maxLat = Math.max(LatLng.round(coord.getLat() - ilat), LatLng
				.round(coord.getLat() + ilat));
		double minLat = Math.min(LatLng.round(coord.getLat() - ilat), LatLng
				.round(coord.getLat() + ilat));

		double maxLng = Math.max(LatLng.round(coord.getLng() - ilng), LatLng
				.round(coord.getLng() + ilng));
		double minLng = Math.min(LatLng.round(coord.getLng() - ilng), LatLng
				.round(coord.getLng() + ilng));

		String sql = "SELECT Elev FROM Elevations WHERE Lat BETWEEN ";
		sql += Double.toString(minLat) + " AND " + Double.toString(maxLat);
		sql += " AND ";
		sql += " Lng BETWEEN ";
		sql += Double.toString(minLng) + " AND " + Double.toString(maxLng);

		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}

	/**
	 * Returns a SQL query to insert the elevation of the coordinates into the
	 * database
	 * 
	 * @param con
	 *            Connection to the DataBase
	 * @param coord
	 *            Coordinates of the point to insert
	 * @param elev
	 *            Elevation of the point
	 */
	private static void insertNewElevation(Connection con, LatLng coord,
			double elev) {
		String query = "INSERT INTO Elevations VALUES(" + coord.getLat() + ","
				+ coord.getLng() + "," + LatLng.round(elev) + ")";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(query);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a connection with the server
	 * 
	 * @param driver
	 *            Driver of the DataBase
	 * @param server
	 *            Url of the DataBase server
	 * @param port
	 *            Port of the DataBase server
	 * @param user
	 *            User of the DataBase
	 * @param pass
	 *            Password of the user
	 * @return
	 */
	private static Connection getConnection(String driver, String server,
			int port, String db, String user, String pass) {
		Connection con = null;
		// prototipo de url "jdbc:mysql://localhost:3306/JunkDB"
		String url = "jdbc:" + driver + ":" + server;
		if (port >= 0)
			url += ":" + Integer.toString(port);
		if (db != null)
			url += "/" + db;
		try {
			if (user == null)
				con = DriverManager.getConnection(url);
			else
				con = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

}
