
package webservices.gov.usgs.gisdata.xmlwebservices2;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gov.usgs.gisdata.xmlwebservices2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.usgs.gisdata.xmlwebservices2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllElevationsResponse.GetAllElevationsResult }
     * 
     */
    public GetAllElevationsResponse.GetAllElevationsResult createGetAllElevationsResponseGetAllElevationsResult() {
        return new GetAllElevationsResponse.GetAllElevationsResult();
    }

    /**
     * Create an instance of {@link GetAllElevations }
     * 
     */
    public GetAllElevations createGetAllElevations() {
        return new GetAllElevations();
    }

    /**
     * Create an instance of {@link GetAllElevationsResponse }
     * 
     */
    public GetAllElevationsResponse createGetAllElevationsResponse() {
        return new GetAllElevationsResponse();
    }

    /**
     * Create an instance of {@link GetElevationResponse }
     * 
     */
    public GetElevationResponse createGetElevationResponse() {
        return new GetElevationResponse();
    }

    /**
     * Create an instance of {@link GetElevationResponse.GetElevationResult }
     * 
     */
    public GetElevationResponse.GetElevationResult createGetElevationResponseGetElevationResult() {
        return new GetElevationResponse.GetElevationResult();
    }

    /**
     * Create an instance of {@link GetElevation }
     * 
     */
    public GetElevation createGetElevation() {
        return new GetElevation();
    }

}
