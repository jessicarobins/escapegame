package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.util.Log;
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

    //XmlResourceParser parser;
    XmlResourceParser xpp;

    public MapParser(Activity activity){
        xpp = activity.getResources().getXml(R.xml.maps_pack_1);

    }


    public List <Map> getMaps() throws XmlPullParserException, IOException {
        List <Map> maps = new ArrayList<Map>();


        String tag;
        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                tag = xpp.getName();
                if(tag.equalsIgnoreCase("Map")){
                    //eventType = xpp.nextTag();
                    maps.add(parseMap());

                }

            }

            eventType = xpp.next();
        }
        return maps;
    }

    public Map parseMap() throws XmlPullParserException, IOException {

        String tag;
        String name = null;
        Sector sectors[][] = new Sector[23][14];

        //int eventType = xpp.nextTag();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if (tag.equalsIgnoreCase("map")){
                    eventType = xpp.next();
                    tag = xpp.getName();
                    //eventType = xpp.nextTag();
                }

                if (tag.equalsIgnoreCase("name")){
                    name = readText();
                    //eventType = xpp.nextTag();
                }
                else if (tag.equalsIgnoreCase("column")) {

                    sectors = parseColumn(sectors);
                    break;
                    //eventType = xpp.nextTag();
                }
                else {
                    break;
                }
            }
            eventType = xpp.next();
        }

        return new Map (name, sectors);
    }

    public Sector [][] parseColumn (Sector [][] sectors) throws XmlPullParserException, IOException {

        String tag;
        Sector sector;
        int column = 0;

        //int eventType = xpp.nextTag();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if (tag.equalsIgnoreCase("column")){
                    column = Integer.parseInt(xpp.getAttributeValue(null, "id"));
                    eventType = xpp.next();
                    tag = xpp.getName();
                }

                if (tag.equalsIgnoreCase("sector")) {


                    sectors = parseSector(sectors, column);

                    //sectors[column][sector.yCoordinate()] = sector;
                    //eventType = xpp.nextTag();
                }



                else
                    break;
            }
            eventType = xpp.next();
        }
        return sectors;
    }

    public Sector[][] parseSector(Sector[][] sectors, int column) throws XmlPullParserException, IOException {

        String tag;
        int row = 0;
        String type = null;

        //int eventType = xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if (tag.equalsIgnoreCase("sector")){
                    eventType = xpp.next();
                    tag = xpp.getName();
                }

                if (tag.equalsIgnoreCase("row")){
                    row = Integer.parseInt(readText())-1;
                }
                else if (tag.equalsIgnoreCase("type")){
                    type = readText();
                    sectors[column][row] = new Sector(column, row, type);
                    return sectors;
                }
                else
                    break;
            }
            eventType = xpp.next();
        }
        return sectors;
    }

    public String getEventsFromAnXMLToString()
            throws XmlPullParserException, IOException
    {
        StringBuffer stringBuffer = new StringBuffer();


        xpp.next();
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_DOCUMENT)
            {
                stringBuffer.append("--- Start XML ---");
            }
            else if(eventType == XmlPullParser.START_TAG)
            {
                stringBuffer.append("\nSTART_TAG: "+xpp.getName());
            }
            else if(eventType == XmlPullParser.END_TAG)
            {
                stringBuffer.append("\nEND_TAG: "+xpp.getName());
            }
            else if(eventType == XmlPullParser.TEXT)
            {
                stringBuffer.append("\nTEXT: "+xpp.getText());
            }
            eventType = xpp.next();
        }
        stringBuffer.append("\n--- End XML ---");
        return stringBuffer.toString();
    }



    private String readText() throws IOException, XmlPullParserException {
        String result = "";
        if (xpp.next() == XmlPullParser.TEXT) {
            result = xpp.getText();
            xpp.nextTag();
        }
        return result;
    }
}
