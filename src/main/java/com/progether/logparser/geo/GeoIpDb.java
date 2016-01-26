/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progether.logparser.geo;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Uses: http://dev.maxmind.com/geoip/legacy/geolite/
 *
 * @author Sebastian Maier (sebastian@progether.com)
 */
public class GeoIpDb {
    private final NavigableMap<Long, GeoIpLocation> ips = new TreeMap<>();

    public GeoIpDb(Path db) throws FileNotFoundException, IOException {
        CSVReader reader = new CSVReader(new FileReader(db.toFile()));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            String ipFrom = nextLine[0];
            String ipTo = nextLine[1];
            String isoCode = nextLine[4];
            String name = nextLine[5];
            Long intIpFrom = Long.parseLong(nextLine[2]);
            Long intIpTo = Long.parseLong(nextLine[3]);
            ips.put(intIpFrom, new GeoIpLocation(intIpFrom, intIpTo, isoCode, name));
        }
    }

    public GeoIpLocation getLocation(String ip) throws UnknownHostException {
        Long intAddress = parseIp(ip);
        return getLocation(intAddress);
    }

    public GeoIpLocation getLocation(Long ip) {
        return ips.floorEntry(ip).getValue();
    }

    public long parseIp(String address) {
        long result = 0;
        // iterate over each octet
        for (String part : address.split(Pattern.quote("."))) {
            // shift the previously parsed bits over by 1 byte
            result = result << 8;
            // set the low order bits to the current octet
            result |= Long.parseLong(part);
        }
        return result;
    }
    
}
