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

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.ER_LEST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.ID;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.cleanOutHtml;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.tilHenvendelse;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    String KONTAKT_NAV_SAKSTEMA = "KNA";

    WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer);

    WSSendInnHenvendelseResponse sendSvar(Henvendelse henvendelse, String uid);

    List<Henvendelse> hentAlleHenvendelser(String fodselsnummer);

    List<Henvendelse> hentTraad(String behandlingskjedeId);

    void merkSomLest(String behandlingskjedeId);

    class Default implements HenvendelseService {

        private final PropertyResolver resolver;
        private final HenvendelsePortType henvendelsePortType;
        private final SendInnHenvendelsePortType sendInnHenvendelsePortType;
        private final InnsynHenvendelsePortType innsynHenvendelsePortType;
        private final PersonService personService;

        public Default(HenvendelsePortType henvendelsePortType, SendInnHenvendelsePortType sendInnHenvendelsePortType, InnsynHenvendelsePortType innsynHenvendelsePortType, PropertyResolver resolver, PersonService personService) {
            this.henvendelsePortType = henvendelsePortType;
            this.sendInnHenvendelsePortType = sendInnHenvendelsePortType;
            this.innsynHenvendelsePortType = innsynHenvendelsePortType;
            this.resolver = resolver;
            this.personService = personService;
        }

        @Override
        public WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer) {
            String xmlHenvendelseType = SPORSMAL_SKRIFTLIG.name();
            String enhet = personService.hentEnhet().getOrElse(null);
            XMLHenvendelse info =
                    new XMLHenvendelse()
                            .withHenvendelseType(xmlHenvendelseType)
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withTema(KONTAKT_NAV_SAKSTEMA)
                            .withBehandlingskjedeId(null)
                            .withBrukersEnhet(enhet)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(cleanOutHtml(henvendelse.fritekst))));
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
                            .withErTilknyttetAnsatt(henvendelse.erTilknyttetAnsatt)
                            .withBrukersEnhet(henvendelse.brukersEnhet)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(cleanOutHtml(henvendelse.fritekst))));
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType)
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public void merkSomLest(String behandlingskjedeId) {
            List<Henvendelse> traad = hentTraad(behandlingskjedeId);
            List<String> ids = on(traad).filter(where(ER_LEST, equalTo(false))).map(ID).collect();
            innsynHenvendelsePortType.merkSomLest(ids);
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
