package ar.com.gepp.mdqmaps.services.dto;

import java.io.Serializable;

public class AbstractPointDTO implements Serializable {

    private Double lat;

    private Double lon;

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
