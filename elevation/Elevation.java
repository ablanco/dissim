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
import java.util.Hashtable;

import util.HexagonalGrid;
import util.Point;
import util.Scenario;
import util.jcoord.LatLng;

/*
 * SQLite -> http://www.zentus.com/sqlitejdbc/
 * MySQL -> http://dev.mysql.com/downloads/connector/j/
 * Postgre -> http://jdbc.postgresql.org/
 */

public class Elevation {

	private static Hashtable<String, String> classNames;

	public static void getElevations(HexagonalGrid grid, String server,
			int port, String user, String pass, String driver) {
		classNames = new Hashtable<String, String>(3);
		classNames.put("mysql", "com.mysql.jdbc.Driver");
		classNames.put("postgresql", "org.postgresql.Driver");
		classNames.put("sqlite", "org.sqlite.JDBC");

		try {
			Class.forName(classNames.get(driver));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = getConnection(server, port, user, pass);

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
//						System.err.println(acum+", "+counter);
						acum += rs.getDouble(1);
						counter++;
					}

					if (counter != 0) {
						//Quiere decir que tenemos mas de un resultado, hacemos la media
						value = Scenario.doubleToInner(grid.getPrecision(),
								(acum / counter));
					} else {
						//Quiere decir que no tenemos ningun resultado, lo preguntamos en el servicio web
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
	 * @param ilat
	 * @param ilng
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

//		System.err.println("*******Query: " + sql);
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
	 * @param coord
	 * @param elev
	 * @return
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
	 * @param server
	 * @param port
	 * @param user
	 * @param pass
	 * @return
	 */
	private static Connection getConnection(String server, int port,
			String user, String pass) {
		Connection con = null;
		try {
			// "jdbc:mysql://localhost:3306/contacts/"
			con = DriverManager.getConnection(server, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

}
