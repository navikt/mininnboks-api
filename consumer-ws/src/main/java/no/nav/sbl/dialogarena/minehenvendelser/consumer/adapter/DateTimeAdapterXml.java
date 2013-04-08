package no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Brukes i forbindelse med JAXB slik at JAXB-klassene bruker
 * joda-time i stedet for standard java dato-typer.
 */
public class DateTimeAdapterXml extends XmlAdapter<String, DateTime> {

    public DateTime unmarshal(String v)  {
        return new DateTime(v);
    }

    public String marshal(DateTime v) {
        return v.toString();
    }
}

