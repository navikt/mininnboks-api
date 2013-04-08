package no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter;

import org.joda.time.DateMidnight;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Brukes i forbindelse med JAXB slik at JAXB-klassene bruker
 * joda-time i stedet for standard java dato-typer.
 */
public class DateAdapterXml extends XmlAdapter<String, DateMidnight> {

    public DateMidnight unmarshal(String v) {
        return new DateMidnight(v);
    }

    public String marshal(DateMidnight v) {
        return v.toString();
    }
}

