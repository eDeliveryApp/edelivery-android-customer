package io.hackathon.edelivery.DeliveryService;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by study on 0004 7/4/2015.
 */
public class Order {
    String code;
    LatLng location;
    int status;
    final String deliver_phone_number ;

    public Order() {
        code = "";
        location = null;
        status = 0;
        this.deliver_phone_number = "";
    }

    public Order(String code, LatLng location, int status) {
        this.code = code;
        this.location = location;
        this.status = status;
        this.deliver_phone_number = "";
    }

    public Order(String code, LatLng location, int status, String deliver_phone_number ) {
        this.code = code;
        this.location = location;
        this.status = status;
        this.deliver_phone_number = deliver_phone_number;
    }

    public String getDeliver_phone_number() {
        return this.deliver_phone_number;
    }

    public String getCode() {
        return this.code;
    }

    public LatLng getLocation() {
        return this.location;
    }

    public int getStatus() {
        return this.status;
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new LatLng(latitude, longitude);
    }

    public void setLocation(LatLng newLoc) {
        this.location = newLoc;
    }
}
