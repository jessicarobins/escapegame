package com.jrobins.jrobins.escape2;

import android.content.res.XmlResourceParser;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MapParser {
    // We don't use namespaces
    private static final String ns = null;

    XmlResourceParser parser;


    /*
    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }*/

    public MapParser(XmlResourceParser parser){
        this.parser = parser;
    }

    public List readFeed(XmlResourceParser parser) throws XmlPullParserException, IOException {
        List <Map> entries = new ArrayList<Map>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("map")) {
                entries.add(readMap(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of a map
    private Map readMap(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Map");
        String mapName = null;
        Sector sectors[][] = new Sector[23][14];


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                mapName = readName(parser);
            } else if (name.equals("Column")) {
                sectors = addColumn(parser, sectors);
            } else {
                skip(parser);
            }
        }
        return new Map(mapName, sectors);
    }

    // Processes the name of the map
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return name;
    }

    // Processes the columns
    private Sector[][] addColumn(XmlPullParser parser, Sector[][]sectors) throws IOException, XmlPullParserException {
        //get the column id
        Sector sector;

        parser.require(XmlPullParser.START_TAG, ns, "Column");

        int column = Integer.parseInt(parser.getAttributeValue(null, "id"));

        parser.nextTag();

        //find the sector tag
        sector = readSector(parser, column);

        parser.require(XmlPullParser.END_TAG, ns, "Column");

        //add the sector to the matrix & return it
        sectors[column][sector.yCoordinate()] = sector;
        return sectors;
    }

    // Processes the sector in the column
    private Sector readSector(XmlPullParser parser, int column) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Sector");
        int row = readSectorRow(parser);
        String sectorType = readSectorType(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Sector");
        return new Sector(column, row, sectorType);
    }

    // sector row - return the int of the row. this is going to be one too high because we
    //  started at 1. oops. so let's just subtract 1 here and call it a day
    private int readSectorRow(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "row");
        String row = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "row");
        return (Integer.parseInt(row)-1);
    }

    //sector type
    private String readSectorType(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "type");
        String sectorType = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return sectorType;
    }

    // extracts text values from between tags
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
