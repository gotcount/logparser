/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progether.logparser.geo;

/**
 * A geo location for a given ip range
 * 
 * @author Sebastian Maier (sebastian@progether.com)
 */
public class GeoIpLocation implements Comparable<GeoIpLocation> {

    public final long ipFrom;
    public final long ipTo;
    public final String isoCode;
    public final String name;

    GeoIpLocation(long ipFrom, long ipTo, String isoCode, String name) {
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
        this.isoCode = isoCode;
        this.name = name;
    }

    @Override
    public int compareTo(GeoIpLocation o) {
        long diff = ipFrom - o.ipFrom;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
