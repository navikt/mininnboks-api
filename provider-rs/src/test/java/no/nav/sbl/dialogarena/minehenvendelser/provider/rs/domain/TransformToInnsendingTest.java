package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static junit.framework.Assert.assertNotNull;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.MOTTATT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TransformToInnsendingTest {

    @Test
    public void transformHenvendelsesbehandling() {
        Innsending innsending = Innsending.behandlingTransformer().transform(createHenvendelsesbehandling());

        assertNotNull(innsending);
        assertThat(MOTTATT, equalTo(innsending.getStatus()));
        assertThat("forventing-kodeverk-id", equalTo(innsending.getTittel()));
        assertThat(2, equalTo(innsending.getDato().getDayOfMonth()));
        assertThat("forventing-kodeverk-id", equalTo(innsending.getInnsendingUrl().getTekst()));
        assertThat("http://not-yet-implemented", equalTo(innsending.getInnsendingUrl().getUrl()));
    }

    @Test
    public void transformSoeknad() {
       Innsending innsending = Innsending.soeknadTransformer().transform(createSoeknad());

        assertNotNull(innsending);
        assertThat("kjedetype-val", equalTo(innsending.getTittel()));
        assertThat(FERDIG, equalTo(innsending.getStatus()));
        assertThat(2L, equalTo(innsending.getDato().getMillis()));
        assertThat("Not implemented", equalTo(innsending.getInnsendingUrl().getTekst()));
        assertThat("http://not-yet-implemented", equalTo(innsending.getInnsendingUrl().getUrl()));
    }

    private static Soeknad createSoeknad() {
        return Soeknad.transformToSoeknad(createBehandlingskjede());
    }

    private static Behandlingskjede createBehandlingskjede() {
        return new Behandlingskjede()
                .withBehandlingskjedetype(new Behandlingskjedetyper()
                        .withValue("kjedetype-val")
                        .withKodeverksRef("kodeverk-ref"))
                .withNormertBehandlingstid(new Behandlingstid()
                        .withTid(BigInteger.TEN)
                        .withType(new Behandlingstidtyper().withValue("days")))
                .withStart(createXmlGregorianDate(2L))
                .withStartNAVtid(createXmlGregorianDate(123))
                .withSluttNAVtid(createXmlGregorianDate(456));
    }

    private static XMLGregorianCalendar createXmlGregorianDate(long milliseconds) {
        DateTime dateTime = new DateTime().withMillis(milliseconds);
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new ApplicationException("Failed to convert date to XMLGregorianCalendar ",e);
        }
        return xmlGregorianCalendar;
    }

    private static Henvendelsesbehandling createHenvendelsesbehandling() {
        return Henvendelsesbehandling.transformToBehandling(createWSBrukerBehandlingOppsummering());
    }

    private static WSBrukerBehandlingOppsummering createWSBrukerBehandlingOppsummering() {
        return new WSBrukerBehandlingOppsummering()
                .withStatus(WSBehandlingsstatus.FERDIG)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(new DateTime(2013, 01, 01, 01, 01))
                .withSistEndret(new DateTime(2013, 01, 02, 01, 01))
                .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer().withDokumentForventningOppsummering(createWsDokumentForventningOppsummering(true)));
    }

    private static WSDokumentForventningOppsummering createWsDokumentForventningOppsummering(boolean isHovedskjema) {
        return new WSDokumentForventningOppsummering().withHovedskjema(isHovedskjema).withKodeverkId("forventing-kodeverk-id").withInnsendingsValg(WSInnsendingsValg.LASTET_OPP);
    }
}
