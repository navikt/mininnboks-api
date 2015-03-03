package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.tilHenvendelse;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    String KONTAKT_NAV_SAKSTEMA = "KNA";

    WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer);

    WSSendInnHenvendelseResponse sendSvar(Henvendelse henvendelse, String uid);

    List<Henvendelse> hentAlleHenvendelser(String fodselsnummer);

    List<Henvendelse> hentTraad(String behandlingskjedeId);

    void merkHenvendelseSomLest(Henvendelse henvendelse);

    class Default implements HenvendelseService {

        private final PropertyResolver resolver;

        private final HenvendelsePortType henvendelsePortType;

        private final SendInnHenvendelsePortType sendInnHenvendelsePortType;

        private final InnsynHenvendelsePortType innsynHenvendelsePortType;

        public Default(HenvendelsePortType henvendelsePortType, SendInnHenvendelsePortType sendInnHenvendelsePortType, InnsynHenvendelsePortType innsynHenvendelsePortType, PropertyResolver resolver) {
            this.henvendelsePortType = henvendelsePortType;
            this.sendInnHenvendelsePortType = sendInnHenvendelsePortType;
            this.innsynHenvendelsePortType = innsynHenvendelsePortType;
            this.resolver = resolver;
        }

        @Override
        public WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer) {
            String xmlHenvendelseType = SPORSMAL_SKRIFTLIG.name();
            XMLHenvendelse info =
                    new XMLHenvendelse()
                            .withHenvendelseType(xmlHenvendelseType)
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withTema(KONTAKT_NAV_SAKSTEMA)
                            .withBehandlingskjedeId(null)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(henvendelse.fritekst)));
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType)
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public WSSendInnHenvendelseResponse sendSvar(Henvendelse henvendelse, String fodselsnummer) {
            String xmlHenvendelseType = SVAR_SBL_INNGAAENDE.name();
            XMLHenvendelse info =
                    new XMLHenvendelse()
                            .withHenvendelseType(xmlHenvendelseType)
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withTema(KONTAKT_NAV_SAKSTEMA)
                            .withBehandlingskjedeId(henvendelse.traadId)
                            .withEksternAktor(henvendelse.eksternAktor)
                            .withTilknyttetEnhet(henvendelse.tilknyttetEnhet)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(henvendelse.fritekst)));
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType)
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
                    REFERAT_TELEFON.name(),
                    SPORSMAL_MODIA_UTGAAENDE.name(),
                    SVAR_SBL_INNGAAENDE.name());
            return on(henvendelsePortType.hentHenvendelseListe(
                    new WSHentHenvendelseListeRequest()
                            .withFodselsnummer(fodselsnummer)
                            .withTyper(typer))
                    .getAny())
                    .map(tilHenvendelse(resolver)).collect();
        }

        @Override
        public List<Henvendelse> hentTraad(String behandlingskjedeId) {
            return on(henvendelsePortType.hentBehandlingskjede(new WSHentBehandlingskjedeRequest().withBehandlingskjedeId(behandlingskjedeId)).getAny())
                    .map(tilHenvendelse(resolver)).collect();
        }
    }
}
