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

import javax.xml.ws.WebServiceException;

import util.HexagonalGrid;
import util.Point;
import util.Scenario;
import util.jcoord.LatLng;

public class Elevation {

	public static void getElevations(HexagonalGrid grid, String server,
			int port, String user, String pass) {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
					if (stmt.getUpdateCount() > 0) {
						ResultSet rs = stmt.getResultSet();
						// Existen datos en nuestra base de datos
						value = (short) getPointAltitude(rs);
						System.err.println("En local " + coord + "+ :" + value);
					} else {
						// No existen datos en nuestra base de datos
						double elev = ElevationWS.getElevation(coord);
						// Los añadimos
						System.err.println("En remoto " + coord + ": "
								+ Double.toString(value));
						insertNewElevation(con, coord, elev);
						value = Scenario.doubleToInner(grid.getPrecision(),
								elev);
					}
				} catch (WebServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			// TODO Auto-generated catch block
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

		String sql = "SELECT Elev FROM Elevations WHERE (lat BETWEEN ";
		sql += Double.toString(minLat) + " AND " + Double.toString(maxLat);
		sql += " )AND( ";
		sql += " lng BETWEEN ";
		sql += Double.toString(minLng) + " AND " + Double.toString(maxLng)+")";
		System.err.println("*******Query: " + sql);
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public static Connection getConnection(String server, int port,
			String user, String pass) {
		Connection con = null;
		try {
			// TODO peta :(
			// "jdbc:mysql://localhost:3306/contacts/"
			con = DriverManager.getConnection(server, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * Execute the query against the given connection
	 * 
	 * @param con
	 * @param query
	 * @return
	 */
	public static PreparedStatement executeSqlQuery(Connection con, String query) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(query);
			stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}

	/**
	 * Returns the mean of a collection of elevation data
	 * 
	 * @param rs
	 * @return
	 */
	public static double getPointAltitude(ResultSet rs) {
		int count = 0;
		double acum = 0;
		try {
			while (rs.next()) {
				acum += rs.getDouble(0);
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acum / count;
	}

}
