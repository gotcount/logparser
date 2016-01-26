/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progether.logparser;

import com.progether.logparser.geo.GeoIpDb;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.basjes.parse.core.Field;

/**
 *
 * @author Sebastian Maier (sebastian@progether.com)
 */
public class CommonNginx {

    private Map<String, String> stringValues = new HashMap<>();
    private OffsetDateTime time;
    private Integer status;
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MMM/uuuu:HH:mm:ss xx", Locale.ENGLISH);
    private final GeoIpDb geo;

    public CommonNginx(GeoIpDb geo) {
        this.geo = geo;
    }

    @Field(value = {
        "STRING:connection.client.user", 
        "HTTP.PROTOCOL:request.firstline.uri.protocol", 
        "HTTP.HOST:request.firstline.uri.host", 
        "HTTP.PORT:request.firstline.uri.port", 
        "HTTP.PATH:request.firstline.uri.path", 
        "HTTP.QUERYSTRING:request.firstline.uri.query", 
        "HTTP.USERAGENT:request.user-agent", 
        "HTTP.HOST:request.referer.host", 
        "HTTP.PORT:request.referer.port", 
        "HTTP.PATH:request.referer.path", 
        "HTTP.QUERYSTRING:request.referer.query", 
        "HTTP.REF:request.referer.ref", 
        "IP:connection.client.host"
    })
    public void setValue(final String name, final String value) {
        stringValues.put(name, value);
    }

    @Field(value = "TIME.STAMP:request.receive.time")
    public void setTime(final String timestamp) {
        time = OffsetDateTime.parse(timestamp, format);
    }

    @Field(value = "STRING:request.status.last")
    public void setStatus(final String status) {
        this.status = Integer.parseInt(status);
    }

    public OffsetDateTime getDateTime() {
        return time;
    }
    
    public LocalDate getDate() {
        return time.toLocalDate();
    }
    
    public LocalTime getTime() {
        return time.toLocalTime();
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return stringValues.get("HTTP.PATH:request.firstline.uri.path");
    }

    public String getQuery() {
        return stringValues.get("HTTP.QUERYSTRING:request.firstline.uri.query");
    }

    public String getUserAgent() {
        return stringValues.get("HTTP.USERAGENT:request.user-agent");
    }

    public String getResourceType() {
        String path = getPath();
        String type = "page";
        int index = -1;
        if (path != null && (index = path.lastIndexOf(".")) > -1) {
            type = path.substring(index + 1);
        }
        return type;
    }

    public String getIP() {
        String ip = stringValues.get("IP:connection.client.host");
        return ip;
    }

    public String getCountry() {
        try {
            return geo.getLocation(stringValues.get("IP:connection.client.host")).isoCode;
        } catch (UnknownHostException ex) {
            Logger.getLogger(ParseNginx.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "-";
    }

    public void clear() {
        stringValues = new HashMap<>();
        status = null;
        time = null;
    }

    @Override
    public String toString() {
        return getIP() + "\t" + time + "\t" + status + "\t" + getResourceType() + "\t" + getCountry() + "\t" + getUserAgent() + "\t" + getPath() + "\t" + getQuery() + stringValues.toString();
    }

}
