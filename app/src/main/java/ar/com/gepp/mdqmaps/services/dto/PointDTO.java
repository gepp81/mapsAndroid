package ar.com.gepp.mdqmaps.services.dto;

public class PointDTO extends AbstractPointDTO {

    private String name;

    private double[] location;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
