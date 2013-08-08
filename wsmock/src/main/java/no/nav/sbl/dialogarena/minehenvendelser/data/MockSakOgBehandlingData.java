package no.nav.sbl.dialogarena.minehenvendelser.data;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.BehandlingVS;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.joda.time.DateTime;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.xml.datatype.DatatypeFactory.newInstance;

public class MockSakOgBehandlingData {


    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListeResponse() {
        try {
            return new FinnSakOgBehandlingskjedeListeResponse()
                    .withSak(createSak());
        } catch (DatatypeConfigurationException e) {
            throw new ApplicationException("Failed to create SakOgBehandlingskjedeRespone ",e);
        }
    }

    public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlingerResponse() {
        return new HentBehandlingskjedensBehandlingerResponse()
                .withBehandlingskjede(createHentBehandlingskjede());
    }

    public HentBehandlingResponse hentBehandlingResponse() {
        return new HentBehandlingResponse()
                .withBehandling(createHentBehandling());
    }

    private Sak createSak() throws DatatypeConfigurationException {
        return new Sak()
                .withBehandlingskjede(createFinnBehandlingsKjede());
    }

    private Behandlingskjede createFinnBehandlingsKjede() throws DatatypeConfigurationException {
        return new Behandlingskjede()
                .withStartNAVtid(createXmlGregorianDate(1,1,2013))
                .withSluttNAVtid(createXmlGregorianDate(1,1,2014))
                .withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.TEN))
                .withBehandlingskjedetype(createBehandlingskjedetyper());
    }

    private XMLGregorianCalendar createXmlGregorianDate(int day, int month, int year) throws DatatypeConfigurationException {
        DateTime dateTime = new DateTime().withDate(year, month, day);
        XMLGregorianCalendar xmlGregorianCalendar = null;
        xmlGregorianCalendar = newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        return xmlGregorianCalendar;
    }

    private Behandlingskjedetyper createBehandlingskjedetyper() {
        return new Behandlingskjedetyper()
                .withValue("MOCK-TEMA-REF-00")
                .withKodeverksRef("MOCK-KODEVERK-REF-00");
    }

    private no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede createHentBehandlingskjede() {
        return new no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede()
                .withBehandling(createHentBehandlinger());
    }

    private Collection<Behandling> createHentBehandlinger() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(createHentBehandling());
        return behandlinger;
    }

    private Behandling createHentBehandling() {
        return new BehandlingVS()
                .withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.ONE));
    }
}
