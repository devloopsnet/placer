package net.devloops.placer.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Odey M. Khalaf <odey@devloops.net> on 07/23/19.
 */
public class LocationEntity {
    @SerializedName("results")
    private List<Results> results;
    @SerializedName("status")
    private String status;

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Address_components {
        @SerializedName("long_name")
        private String long_name;
        @SerializedName("short_name")
        private String short_name;


        public String getLong_name() {
            return long_name;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }


    }

    public static class Northeast {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class Southwest {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class Bounds {
        @SerializedName("northeast")
        private Northeast northeast;
        @SerializedName("southwest")
        private Southwest southwest;

        public Northeast getNortheast() {
            return northeast;
        }

        public void setNortheast(Northeast northeast) {
            this.northeast = northeast;
        }

        public Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(Southwest southwest) {
            this.southwest = southwest;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class Viewport {
        @SerializedName("northeast")
        private Northeast northeast;
        @SerializedName("southwest")
        private Southwest southwest;

        public Northeast getNortheast() {
            return northeast;
        }

        public void setNortheast(Northeast northeast) {
            this.northeast = northeast;
        }

        public Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(Southwest southwest) {
            this.southwest = southwest;
        }
    }

    public static class Geometry {
        @SerializedName("bounds")
        private Bounds bounds;
        @SerializedName("location")
        private Location location;
        @SerializedName("location_type")
        private String location_type;
        @SerializedName("viewport")
        private Viewport viewport;

        public Bounds getBounds() {
            return bounds;
        }

        public void setBounds(Bounds bounds) {
            this.bounds = bounds;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getLocation_type() {
            return location_type;
        }

        public void setLocation_type(String location_type) {
            this.location_type = location_type;
        }

        public Viewport getViewport() {
            return viewport;
        }

        public void setViewport(Viewport viewport) {
            this.viewport = viewport;
        }
    }

    public static class Results {
        @SerializedName("address_components")
        private List<Address_components> address_components;
        @SerializedName("formatted_address")
        private String formatted_address;
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("place_id")
        private String place_id;

        public List<Address_components> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(List<Address_components> address_components) {
            this.address_components = address_components;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }
    }
}