/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progether.logparser;

import com.progether.logparser.geo.GeoIpDb;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author Sebastian Maier (sebastian@progether.com)
 */
public abstract class AbstractParser {

    protected final GeoIpDb geo;

    public AbstractParser(Path geoDb) throws IOException {
        geo = new GeoIpDb(geoDb);
    }

}
