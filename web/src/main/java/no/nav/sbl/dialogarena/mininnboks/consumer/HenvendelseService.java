package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_TELEFON;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_TELEFON;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    WSSendInnHenvendelseResponse stillSporsmal(String fritekst, Temagruppe temagruppe, String fodselsnummer);

    List<Henvendelse> hentAlleHenvendelser(String fodselsnummer);

    void merkHenvendelseSomLest(Henvendelse henvendelse);

    class Default implements HenvendelseService {

        private final HenvendelsePortType henvendelsePortType;

        private final SendInnHenvendelsePortType sendInnHenvendelsePortType;

        private final InnsynHenvendelsePortType innsynHenvendelsePortType;

        public Default(HenvendelsePortType henvendelsePortType, SendInnHenvendelsePortType sendInnHenvendelsePortType, InnsynHenvendelsePortType innsynHenvendelsePortType) {
            this.henvendelsePortType = henvendelsePortType;
            this.sendInnHenvendelsePortType = sendInnHenvendelsePortType;
            this.innsynHenvendelsePortType = innsynHenvendelsePortType;
        }

        @Override
        public WSSendInnHenvendelseResponse stillSporsmal(String fritekst, Temagruppe temagruppe, String fodselsnummer) {
            XMLHenvendelse info =
                    new XMLHenvendelse()
                            .withHenvendelseType(SPORSMAL_SKRIFTLIG.name())
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withTema(KONTAKT_NAV_SAKSTEMA)
                            .withBehandlingskjedeId(null)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(temagruppe.name())
                                            .withFritekst(fritekst)));

            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(SPORSMAL_SKRIFTLIG.name())
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public void merkHenvendelseSomLest(Henvendelse henvendelse) {
            innsynHenvendelsePortType.merkSomLest(new ArrayList<>(asList(henvendelse.id)));
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String fodselsnummer) {
            List<String> typer = asList(
                    SPORSMAL_SKRIFTLIG.name(),
                    SVAR_SKRIFTLIG.name(),
                    SVAR_OPPMOTE.name(),
                    SVAR_TELEFON.name(),
                    REFERAT_OPPMOTE.name(),
                    REFERAT_TELEFON.name());
            return on(henvendelsePortType.hentHenvendelseListe(
                    new WSHentHenvendelseListeRequest()
                            .withFodselsnummer(fodselsnummer)
                            .withTyper(typer))
                    .getAny())
                    .map(TIL_HENVENDELSE).collect();
        }
    }
}
