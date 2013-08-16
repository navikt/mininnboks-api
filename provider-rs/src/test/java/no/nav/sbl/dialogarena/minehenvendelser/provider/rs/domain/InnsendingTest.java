package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config.MockInnholdTestContext;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static java.lang.System.setProperty;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static junit.framework.Assert.assertNotNull;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.provider.rs.domain.Innsending.InnsendingStatus.MOTTATT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MockInnholdTestContext.class})
public class InnsendingTest {

    @Inject
    private Kodeverk kodeverk;

    private CmsContentRetriever innholdsteksterMock;

    @Before
    public void setUp() {
        innholdsteksterMock = mock(CmsContentRetriever.class);
        setupResponses();
    }

    private void setupResponses() {
        when(innholdsteksterMock.hentTekst("soeknad.detaljer.link.tekst")).thenReturn("Gå til detaljoversikten");
        when(innholdsteksterMock.hentTekst("behandling.fortsett.innsending.link.tekst")).thenReturn("Fortsett dokumentinnsending");
        setProperty("soeknad.detaljer.link.url", "http://not.yet.implemented/");
        setProperty("dokumentinnsending.link.url", "http://not.yet.implemented/");
    }

    @Test
    public void transformHenvendelsesbehandling() {
        Innsending innsending = Innsending.behandlingTransformer(innholdsteksterMock, kodeverk).transform(createHenvendelsesbehandling());

        assertNotNull(innsending);
        assertThat(innsending.getStatus(), equalTo(MOTTATT));
        assertThat(innsending.getTittel(), equalTo("Mocked title 1"));
        assertThat(innsending.getDato().getDayOfMonth(), equalTo(2));
        assertThat(innsending.getInnsendingUrl().getTekst(), equalTo("Fortsett dokumentinnsending"));
        assertThat(innsending.getInnsendingUrl().getUrl(), equalTo("http://not.yet.implemented/BEHANDLING-ID-1"));
    }

    @Test
    public void transformSoeknad() {
       Innsending innsending = Innsending.soeknadTransformer(innholdsteksterMock).transform(createSoeknad());

        assertNotNull(innsending);
        assertThat(innsending.getTittel(), equalTo("BEHANDLING-KJEDETYPE-1"));
        assertThat(innsending.getStatus(), equalTo(FERDIG));
        assertThat(innsending.getDato().getMillis(), equalTo(2l));
        assertThat(innsending.getInnsendingUrl().getTekst(), equalTo("Gå til detaljoversikten"));
        assertThat(innsending.getInnsendingUrl().getUrl(), equalTo("http://not.yet.implemented/BEHANDLING-REF-ID-1"));
    }

    private static Soeknad createSoeknad() {
        return Soeknad.transformToSoeknad(createBehandlingskjede());
    }

    private static Behandlingskjede createBehandlingskjede() {
        return new Behandlingskjede()
                .withBehandlingskjedetype(new Behandlingskjedetyper()
                        .withValue("BEHANDLING-KJEDETYPE-1")
                        .withKodeverksRef("kodeverk-ref"))
                .withNormertBehandlingstid(new Behandlingstid()
                        .withTid(BigInteger.TEN)
                        .withType(new Behandlingstidtyper().withValue("days")))
                .withStart(createXmlGregorianDate(2L))
                .withStartNAVtid(createXmlGregorianDate(123))
                .withSluttNAVtid(createXmlGregorianDate(456))
                .withSisteBehandlingREF("BEHANDLING-REF-ID-1");
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
                .withBehandlingsId("BEHANDLING-ID-1")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(new DateTime(2013, 01, 01, 01, 01))
                .withSistEndret(new DateTime(2013, 01, 02, 01, 01))
                .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer().withDokumentForventningOppsummering(createWsDokumentForventningOppsummering(true)));
    }

    private static WSDokumentForventningOppsummering createWsDokumentForventningOppsummering(boolean isHovedskjema) {
        return new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withKodeverkId("MOCK-00-00-1")
                .withInnsendingsValg(WSInnsendingsValg.LASTET_OPP);
    }
}
