/**
 * The Elevation Query Web Service returns the elevation in feet or meters for a specific latitutde/longitude (WGS 1984) point from the USGS Seamless Elevation datasets hosted at <a href="http://eros.usgs.gov/">EROS</a>.  The elevation values returned default to the best-available data source available at the specified point.  Alternately, this service may return the value from a specified data source, or from all data sources.  If unable to find data at the requested point, this service returns an extremely large, negative value (-1.79769313486231E+308).  View the detailed <a href="/XMLWebServices2/Elevation_Service_Methods.php">Elevation Service Methods</a> description for more information on the methods and parameters used in this service.  Visit <a href="http://gisdata.usgs.gov/">http://gisdata.usgs.gov/</a> to view other EROS Web Services.
 * 
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "http://gisdata.usgs.gov/XMLWebServices2/", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package elevation.usgs;
