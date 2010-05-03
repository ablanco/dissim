package util.elevation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sun.jdbc.odbc.JdbcOdbcDriver;
import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;
import webservices.AltitudeWS;

public class Elevation {

	public static void getElevations(HexagonalGrid grid, String server,
			int port, String user, String pass) {

		try {
			DriverManager.registerDriver(new JdbcOdbcDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//TODO esto peta :(
		Connection con = getConnectio(server, port, user, pass);
		// Ahora recorremos toda la matriz y buscamos/insertamos los valores de
		// las alturas
		double ilat = grid.getBox().getIlat();
		double ilng = grid.getBox().getIlng();
		for (int col = 0; col < grid.getColumns(); col++) {
			for (int row = 0; row < grid.getRows(); row++) {
				LatLng centre = grid.tileToCoord(new Point(col, row));
				ResultSet rs = executeSqlQuerry(con, getNearPoints(centre,
						ilat, ilng));
				short value;
				if (rs != null) {
					// Existen datos en nuestra base de datos
					value = (short) getPointAltitude(rs);

				} else {
					// No existen datos en nuestra base de datos
					double elev = AltitudeWS.getElevation(centre);
					// Los añadimos
					executeSqlQuerry(con, insertNewElevation(centre, elev));
					value = (short) elev;
				}
				grid.setTerrainValue(col, row, value);
			}
		}

	}

	/**
	 * gives an string containin the query from near points
	 * 
	 * @param centre
	 * @param ilat
	 * @param ilng
	 * @return
	 */
	private static String getNearPoints(LatLng centre, double ilat, double ilng) {
		String sql = "SELECT elev FROM dissim_elevations WHERE lat BETWEEN ";
		sql += centre.getLat() - ilat + " AND " + centre.getLat() + ilat;
		sql += " AND ";
		sql += " lng BETWEEN ";
		sql += centre.getLng() - ilng + " AND " + centre.getLng() + ilng;
		return sql;
	}

	/**
	 * sql query to insert this centre an elev
	 * @param centre
	 * @param elev
	 * @return
	 */
	private static String insertNewElevation(LatLng centre, double elev) {
		return "INSERT INTO dissim_elevations (" + centre.getLat() + ","
				+ centre.getLng() + "," + elev + ")";
	}

	/**
	 * Nos da una conexion con el servidor
	 * 
	 * @param server
	 * @param port
	 * @param user
	 * @param pass
	 * @return
	 */
	public static Connection getConnectio(String server, int port, String user,
			String pass) {
		Connection con = null;
		try {
			//TODO peta :(
			con = DriverManager.getConnection(server, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * Dada una conexion y una consulta sql devuelve los resultados
	 * 
	 * @param con
	 * @param query
	 * @return
	 */
	public static ResultSet executeSqlQuerry(Connection con, String query) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Dada una lista de puntos de altitud, me quedo con la media
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
