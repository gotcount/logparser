/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progether.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.core.exceptions.DissectionFailure;
import nl.basjes.parse.core.exceptions.InvalidDissectorException;
import nl.basjes.parse.core.exceptions.MissingDissectorsException;
import nl.basjes.parse.httpdlog.ApacheHttpdLoglineParser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;

/**
 *
 * @author Sebastian Maier (sebastian@progether.com)
 */
public class ParseNginx extends AbstractParser {

    public ParseNginx(Path geoDb) throws IOException {
        super(geoDb);
    }

    private static final String NGINX_COMMON = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"";

    public void parseCommon(Path input, Consumer<CommonNginx> consumer) {

        Parser<CommonNginx> parser = new HttpdLoglineParser<>(CommonNginx.class, NGINX_COMMON);

        CommonNginx thisLine = new CommonNginx(geo);

        try (BufferedReader reader = Files.newBufferedReader(input)) {

            reader.lines().forEach(l -> {
                try {
                    parser.parse(thisLine, l);
                    consumer.accept(thisLine);
                    thisLine.clear();
                } catch (DissectionFailure | InvalidDissectorException | MissingDissectorsException ex) {
                    Logger.getLogger(ParseNginx.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DateTimeParseException ex) {
                    System.err.println("coult not parse date " + ex.getMessage());
                }
            });

        } catch (IOException ex) {

        }

    }

    public void listPossiblePath(Path input, String format) throws MissingDissectorsException, InvalidDissectorException {
        Parser<Object> parser = new ApacheHttpdLoglineParser<>(Object.class, NGINX_COMMON);
        List<String> possiblePaths = parser.getPossiblePaths();
        possiblePaths.stream().forEach((path) -> {
            System.out.println(path);
        });
    }

    public static void main(String[] args) throws MissingDissectorsException, InvalidDissectorException, IOException {

        ParseNginx pn = new ParseNginx(Paths.get("../data/GeoIPCountryWhois.csv"));
        pn.parseCommon(Paths.get("../data/access-combined.log"), line -> {
            // nop
            //System.out.println(line);
        });

    }

}
