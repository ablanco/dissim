package util.jcoord;

import java.io.Serializable;

/**
 * Class to represent a latitude/longitude pair.
 * 
 * (c) 2006 Jonathan Stott
 * 
 * Created on 11-02-2006
 * 
 * Some changes by Manuel Gomar and Alejandro Blanco
 * 
 * @author Jonathan Stott
 * @version 1.0 (modified)
 * @since 1.0
 * @license GPL
 */
public class LatLng implements Comparable<LatLng>, Serializable {

	public static final int SAME = 0;
	public static final int ABOVE = 1; // Están repetidos?? en latlngbox
	public static final int ABOVE_RIGHT = 2;
	public static final int RIGHT = 3;
	public static final int BELOW_RIGHT = 4;
	public static final int BELOW = 5;
	public static final int BELOW_LEFT = 6;
	public static final int LEFT = 7;
	public static final int ABOVE_LEFT = 8;

	private static final long serialVersionUID = 1L;

	/**
	 * Latitude in degrees.
	 */
	private double lat;

	/**
	 * Longitude in degrees.
	 */
	private double lng;

	private double altitude = 0;

	/**
	 * Create a new LatLng object to represent a latitude/longitude pair.
	 * 
	 * @param lat
	 *            the latitude in degrees
	 * @param lng
	 *            the longitude in degrees
	 * @since 1.0
	 */
	public LatLng(double lat, double lng) {
		this.lat = round(lat);
		this.lng = round(lng);
	}

	/**
	 * Create a new LatLng object to represent a latitude/longitude pair. Now
	 * supporting altitude in decimeters
	 * 
	 * @param lat
	 *            the latitude in degrees
	 * @param lng
	 *            the longitude in degrees
	 * @param altitude
	 *            the altitude in decimeters
	 * @author Manuel Gomar Acosta
	 */
	public LatLng(double lat, double lng, double altitude) {
		this.lat = round(lat);
		this.lng = round(lng);
		this.altitude = altitude;
	}

	/**
	 * Get a String representation of this LatLng object.
	 * 
	 * @return a String representation of this LatLng object.
	 * @since 1.0
	 */
	public String toString() {
		return "(" + lat + ", " + lng + ")";
	}

	/**
	 * Get a String KML friendly to Google Earth representation of this LatLng
	 * object
	 * 
	 * @author Manuel Gomar Acosta
	 * @return a String KML friendly representation of this LatLng object
	 */
	public String toKmlString() {
		return lng + ", " + lat + ", " + altitude;
	}

	/**
	 * Convert this latitude and longitude to a UTM reference.
	 * 
	 * @return the converted UTM reference
	 * @since 1.0
	 */
	public UTMRef toUTMRef() {
		double UTM_F0 = 0.9996;
		double a = RefEll.WGS84.getMaj();
		double eSquared = RefEll.WGS84.getEcc();
		double longitude = this.lng;
		double latitude = this.lat;

		double latitudeRad = latitude * (Math.PI / 180.0);
		double longitudeRad = longitude * (Math.PI / 180.0);
		int longitudeZone = (int) Math.floor((longitude + 180.0) / 6.0) + 1;

		// Special zone for Norway
		if (latitude >= 56.0 && latitude < 64.0 && longitude >= 3.0
				&& longitude < 12.0) {
			longitudeZone = 32;
		}

		// Special zones for Svalbard
		if (latitude >= 72.0 && latitude < 84.0) {
			if (longitude >= 0.0 && longitude < 9.0) {
				longitudeZone = 31;
			} else if (longitude >= 9.0 && longitude < 21.0) {
				longitudeZone = 33;
			} else if (longitude >= 21.0 && longitude < 33.0) {
				longitudeZone = 35;
			} else if (longitude >= 33.0 && longitude < 42.0) {
				longitudeZone = 37;
			}
		}

		double longitudeOrigin = (longitudeZone - 1) * 6 - 180 + 3;
		double longitudeOriginRad = longitudeOrigin * (Math.PI / 180.0);

		char UTMZone = UTMRef.getUTMLatitudeZoneLetter(latitude);

		double ePrimeSquared = (eSquared) / (1 - eSquared);

		double n = a
				/ Math.sqrt(1 - eSquared * Math.sin(latitudeRad)
						* Math.sin(latitudeRad));
		double t = Math.tan(latitudeRad) * Math.tan(latitudeRad);
		double c = ePrimeSquared * Math.cos(latitudeRad)
				* Math.cos(latitudeRad);
		double A = Math.cos(latitudeRad) * (longitudeRad - longitudeOriginRad);

		double M = a
				* ((1 - eSquared / 4 - 3 * eSquared * eSquared / 64 - 5
						* eSquared * eSquared * eSquared / 256)
						* latitudeRad
						- (3 * eSquared / 8 + 3 * eSquared * eSquared / 32 + 45
								* eSquared * eSquared * eSquared / 1024)
						* Math.sin(2 * latitudeRad)
						+ (15 * eSquared * eSquared / 256 + 45 * eSquared
								* eSquared * eSquared / 1024)
						* Math.sin(4 * latitudeRad) - (35 * eSquared * eSquared
						* eSquared / 3072)
						* Math.sin(6 * latitudeRad));

		double UTMEasting = (UTM_F0
				* n
				* (A + (1 - t + c) * Math.pow(A, 3.0) / 6 + (5 - 18 * t + t * t
						+ 72 * c - 58 * ePrimeSquared)
						* Math.pow(A, 5.0) / 120) + 500000.0);

		double UTMNorthing = (UTM_F0 * (M + n
				* Math.tan(latitudeRad)
				* (A * A / 2 + (5 - t + (9 * c) + (4 * c * c))
						* Math.pow(A, 4.0) / 24 + (61 - (58 * t) + (t * t)
						+ (600 * c) - (330 * ePrimeSquared))
						* Math.pow(A, 6.0) / 720)));

		// Adjust for the southern hemisphere
		if (latitude < 0) {
			UTMNorthing += 10000000.0;
		}

		return new UTMRef(UTMEasting, UTMNorthing, UTMZone, longitudeZone);
	}

	/**
	 * Convert this LatLng from the OSGB36 datum to the WGS84 datum using an
	 * approximate Helmert transformation.
	 * 
	 * @since 1.0
	 */
	public void toWGS84() {
		double a = RefEll.AIRY_1830.getMaj();
		double eSquared = RefEll.AIRY_1830.getEcc();
		double phi = Math.toRadians(lat);
		double lambda = Math.toRadians(lng);
		double v = a / (Math.sqrt(1 - eSquared * Util.sinSquared(phi)));
		double H = 0; // height
		double x = (v + H) * Math.cos(phi) * Math.cos(lambda);
		double y = (v + H) * Math.cos(phi) * Math.sin(lambda);
		double z = ((1 - eSquared) * v + H) * Math.sin(phi);

		double tx = 446.448;
		double ty = -124.157;
		double tz = 542.060;
		double s = -0.0000204894;
		double rx = Math.toRadians(0.00004172222);
		double ry = Math.toRadians(0.00006861111);
		double rz = Math.toRadians(0.00023391666);

		double xB = tx + (x * (1 + s)) + (-rx * y) + (ry * z);
		double yB = ty + (rz * x) + (y * (1 + s)) + (-rx * z);
		double zB = tz + (-ry * x) + (rx * y) + (z * (1 + s));

		a = RefEll.WGS84.getMaj();
		eSquared = RefEll.WGS84.getEcc();

		double lambdaB = Math.toDegrees(Math.atan(yB / xB));
		double p = Math.sqrt((xB * xB) + (yB * yB));
		double phiN = Math.atan(zB / (p * (1 - eSquared)));
		for (int i = 1; i < 10; i++) {
			v = a / (Math.sqrt(1 - eSquared * Util.sinSquared(phiN)));
			double phiN1 = Math
					.atan((zB + (eSquared * v * Math.sin(phiN))) / p);
			phiN = phiN1;
		}

		double phiB = Math.toDegrees(phiN);

		lat = phiB;
		lng = lambdaB;
	}

	/**
	 * Convert this LatLng from the WGS84 datum to the OSGB36 datum using an
	 * approximate Helmert transformation.
	 * 
	 * @since 1.0
	 */
	public void toOSGB36() {
		RefEll wgs84 = new RefEll(6378137.000, 6356752.3141);
		double a = wgs84.getMaj();
		double eSquared = wgs84.getEcc();
		double phi = Math.toRadians(this.lat);
		double lambda = Math.toRadians(this.lng);
		double v = a / (Math.sqrt(1 - eSquared * Util.sinSquared(phi)));
		double H = 0; // height
		double x = (v + H) * Math.cos(phi) * Math.cos(lambda);
		double y = (v + H) * Math.cos(phi) * Math.sin(lambda);
		double z = ((1 - eSquared) * v + H) * Math.sin(phi);

		double tx = -446.448;
		double ty = 124.157;
		double tz = -542.060;
		double s = 0.0000204894;
		double rx = Math.toRadians(-0.00004172222);
		double ry = Math.toRadians(-0.00006861111);
		double rz = Math.toRadians(-0.00023391666);

		double xB = tx + (x * (1 + s)) + (-rx * y) + (ry * z);
		double yB = ty + (rz * x) + (y * (1 + s)) + (-rx * z);
		double zB = tz + (-ry * x) + (rx * y) + (z * (1 + s));

		RefEll airy1830 = new RefEll(6377563.396, 6356256.909);
		a = airy1830.getMaj();
		eSquared = airy1830.getEcc();

		double lambdaB = Math.toDegrees(Math.atan(yB / xB));
		double p = Math.sqrt((xB * xB) + (yB * yB));
		double phiN = Math.atan(zB / (p * (1 - eSquared)));
		for (int i = 1; i < 10; i++) {
			v = a / (Math.sqrt(1 - eSquared * Util.sinSquared(phiN)));
			double phiN1 = Math
					.atan((zB + (eSquared * v * Math.sin(phiN))) / p);
			phiN = phiN1;
		}

		double phiB = Math.toDegrees(phiN);

		lat = phiB;
		lng = lambdaB;
	}

	/**
	 * Calculate the surface distance in meters from the this LatLng to the
	 * given LatLng. May give negative distances
	 * 
	 * @param ll
	 * @return the surface distance in meters
	 * @since 1.0
	 */
	public double distance(LatLng ll) {
		double er = 6366.707;

		double latFrom = Math.toRadians(getLat());
		double latTo = Math.toRadians(ll.getLat());
		double lngFrom = Math.toRadians(getLng());
		double lngTo = Math.toRadians(ll.getLng());

		double d = Math.acos(Math.sin(latFrom) * Math.sin(latTo)
				+ Math.cos(latFrom) * Math.cos(latTo)
				* Math.cos(lngTo - lngFrom))
				* er * 1000;

		// return Math.abs(d);
		return d;
	}

	/**
	 * Return the latitude in degrees.
	 * 
	 * @return the latitude in degrees
	 * @since 1.0
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Return the longitude in degrees.
	 * 
	 * @return the longitude in degrees
	 * @since 1.0
	 */
	public double getLng() {
		return lng;
	}

	/**
	 * @author Manuel Gomar Acosta
	 * @param lat
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @author Manuel Gomar Acosta
	 * @param lng
	 */
	public void setLng(double lng) {
		this.lng = lng;
	}

	/**
	 * Returns the altitude
	 * 
	 * @return
	 * @author Manuel Gomar Acosta
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * Set altitude
	 * 
	 * @param altitude
	 * @author Manuel Gomar Acosta
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Comparator
	 * 
	 * @author Manuel Gomar Acosta
	 */
	@Override
	public int compareTo(LatLng o) {
		if (o.getLat() == lat && o.getLng() == lng) {
			return 0;
		} else {
			if (o.getLat() < lat) {
				return 1;
			}
			return -1;
		}
	}

	/**
	 * Equals
	 * 
	 * @author Manuel Gomar Acosta
	 */
	@Override
	public boolean equals(Object coord) {
		if (coord instanceof LatLng) {
			LatLng c = (LatLng) coord;
			return (c.getAltitude() == altitude) && (c.getLat() == lat)
					&& (c.getLng() == lng);
		} else {
			return false;
		}
	}

	/**
	 * If this coordinates are contained in the area defined by the diagonal
	 * defined by the parameters
	 * 
	 * @author Alejandro Blanco
	 * @param NW
	 * @param SE
	 * @return
	 */
	public boolean isContainedIn(LatLng NW, LatLng SE) {
		boolean blat = false;
		boolean blng = false;

		// Ha de ser un valor intermedio
		blat = ((NW.getLat() >= lat) && (lat >= SE.getLat()))
				|| ((NW.getLat() <= lat) && (lat <= SE.getLat()));
		blng = ((NW.getLng() >= lng) && (lng >= SE.getLng()))
				|| ((NW.getLng() <= lng) && (lng <= SE.getLng()));

		return blat && blng;
	}

	/**
	 * Posición absoluta de la coordenada c, respecto a mi posción, mirar
	 * variables estáticas
	 * 
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public int absolutePosition(LatLng c) {
		if (lat == c.getLat()) {
			if (c.getLng() > lng) {
				return RIGHT;
			} else if (c.getLng() < lng)
				return LEFT;
			else {
				return SAME;
			}
		}

		if (lng == c.getLng()) {
			if (c.getLat() > lat)
				return ABOVE;
			return BELOW;
		}

		if (c.getLat() > lat) {
			if (c.getLng() > lng)
				return ABOVE_RIGHT;
			return ABOVE_LEFT;
		} else {
			if (c.getLng() > lng)
				return BELOW_RIGHT;
			return BELOW_LEFT;
		}
	}

	/**
	 * Por encima o al mismo nivel
	 * 
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public boolean isAboveOf(LatLng c) {
		return lat > c.getLat();
	}

	/**
	 * Por debajo o igual
	 * 
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public boolean isBelowOf(LatLng c) {
		return lat < c.getLat();
	}

	/**
	 * A la derecha o igual
	 * 
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public boolean isRigthOf(LatLng c) {
		return lng > c.getLng();
	}

	/**
	 * A la izquierda o igual
	 * 
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public boolean isLeftOf(LatLng c) {
		return lng < c.getLng();
	}

	/**
	 * @author Manuel Gomar Acosta
	 * @param c
	 * @return
	 */
	public static double round(double c) {
		long l = (long) (c * Math.pow(10, 6));
		return l / Math.pow(10, 6);
	}
}