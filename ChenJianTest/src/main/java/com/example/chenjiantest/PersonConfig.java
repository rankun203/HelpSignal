package com.example.chenjiantest;

import android.app.AlertDialog;
import android.util.Log;
import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjian on 13-7-30.
 */
public class PersonConfig extends DefaultHandler {
    private PersonInfo  personInfo;
    private StringBuffer    buffer = new StringBuffer();

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    @Override

    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
        super.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("name".equals(localName)) {
            personInfo.setName(buffer.toString());
            buffer.setLength(0);
        } else if("flag".equals(localName)) {
            personInfo.setFlag(Integer.parseInt(buffer.toString().trim()));
            buffer.setLength(0);
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startDocument() throws SAXException {
        personInfo = new PersonInfo();
        super.startDocument();
    }
    public static PersonInfo readPersonInfo(InputStream is) {
        PersonInfo personInfo = null;
        try{
            PersonConfig personConfig = new PersonConfig();
            android.util.Xml.parse(is, Xml.Encoding.UTF_8, personConfig);
            personInfo = personConfig.getPersonInfo();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return personInfo;
    }
    public static String writePersonInfo(PersonInfo p) {
        StringWriter stringWriter = new StringWriter();
        p.setFlag(10);
        p.setName("XXX");
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            xmlSerializer.setOutput(stringWriter);

            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "person");

            xmlSerializer.startTag(null, "name");
            xmlSerializer.text(p.getName());
            xmlSerializer.endTag(null, "name");

            xmlSerializer.startTag(null, "flag");
            xmlSerializer.text(p.getFlag() + "");
            xmlSerializer.endTag(null, "flag");

            xmlSerializer.endTag(null, "person");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}
