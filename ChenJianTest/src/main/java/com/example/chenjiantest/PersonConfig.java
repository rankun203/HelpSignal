package com.example.chenjiantest;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
            personInfo.setFalg(Integer.parseInt(buffer.toString().trim()));
            buffer.setLength(0);
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startDocument() throws SAXException {
        personInfo = new PersonInfo();
        super.startDocument();
    }
}
