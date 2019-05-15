package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.cleanOutHtml;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.fjernHardeMellomrom;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer);

    WSSendInnHenvendelseResponse stillSporsmalDirekte(Henvendelse henvendelse, String fodselsnummer);

    WSSendInnHenvendelseResponse sendSvar(Henvendelse henvendelse, String uid);

    List<Henvendelse> hentAlleHenvendelser(String fodselsnummer);

    List<Henvendelse> hentTraad(String behandlingskjedeId);

    void merkAlleSomLest(String behandlingskjedeId);

    void merkSomLest(String id);

    class Default implements HenvendelseService {

        public static final String KONTAKT_NAV_SAKSTEMA = "KNA";
        private final HenvendelsePortType henvendelsePortType;
        private final SendInnHenvendelsePortType sendInnHenvendelsePortType;
        private final InnsynHenvendelsePortType innsynHenvendelsePortType;
        private final PersonService personService;

        public Default(HenvendelsePortType henvendelsePortType,
                       SendInnHenvendelsePortType sendInnHenvendelsePortType,
                       InnsynHenvendelsePortType innsynHenvendelsePortType,
                       PersonService personService) {

            this.henvendelsePortType = henvendelsePortType;
            this.sendInnHenvendelsePortType = sendInnHenvendelsePortType;
            this.innsynHenvendelsePortType = innsynHenvendelsePortType;
            this.personService = personService;
        }

        @Override
        public WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer) {
            return stillSporsmal(henvendelse, fodselsnummer, SPORSMAL_SKRIFTLIG);
        }

        @Override
        public WSSendInnHenvendelseResponse stillSporsmalDirekte(Henvendelse henvendelse, String fodselsnummer) {
            return stillSporsmal(henvendelse, fodselsnummer, SPORSMAL_SKRIFTLIG_DIREKTE);
        }

        private WSSendInnHenvendelseResponse stillSporsmal(Henvendelse henvendelse, String fodselsnummer, XMLHenvendelseType xmlHenvendelseType) {
            String enhet = personService.hentEnhet().orElse(null);
            String sporsmaltekst = cleanOutHtml(fjernHardeMellomrom(henvendelse.fritekst));
            XMLHenvendelse info =
                    new XMLHenvendelse()
                            .withHenvendelseType(xmlHenvendelseType.name())
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withTema(KONTAKT_NAV_SAKSTEMA)
                            .withBehandlingskjedeId(null)
                            .withBrukersEnhet(enhet)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(sporsmaltekst)));
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType.name())
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public WSSendInnHenvendelseResponse sendSvar(Henvendelse henvendelse, String fodselsnummer) {
            String xmlHenvendelseType = SVAR_SBL_INNGAAENDE.name();
            String svartekst = cleanOutHtml(fjernHardeMellomrom(henvendelse.fritekst));
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
                            .withKontorsperreEnhet(henvendelse.kontorsperreEnhet)
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(henvendelse.temagruppe.name())
                                            .withFritekst(svartekst)));
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType)
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public void merkAlleSomLest(String behandlingskjedeId) {
            List<Henvendelse> traad = hentTraad(behandlingskjedeId);
            List<String> ids = traad.stream()
                    .filter(henvendelse -> !henvendelse.isLest())
                    .map(henvendelse -> henvendelse.id)
                    .collect(toList());
            innsynHenvendelsePortType.merkSomLest(ids);
        }

        @Override
        public void merkSomLest(String behandlingsId) {
            innsynHenvendelsePortType.merkSomLest(asList(behandlingsId));
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String fodselsnummer) {
            List<String> typer = asList(
                    SPORSMAL_SKRIFTLIG.name(),
                    SPORSMAL_SKRIFTLIG_DIREKTE.name(),
                    SVAR_SKRIFTLIG.name(),
                    SVAR_OPPMOTE.name(),
                    SVAR_TELEFON.name(),
                    REFERAT_OPPMOTE.name(),
                    REFERAT_TELEFON.name(),
                    SPORSMAL_MODIA_UTGAAENDE.name(),
                    SVAR_SBL_INNGAAENDE.name(),
                    DOKUMENT_VARSEL.name(),
                    OPPGAVE_VARSEL.name(),
                    DELVIS_SVAR_SKRIFTLIG.name());

            Stream<XMLHenvendelse> wsHenvendelser = henvendelsePortType.hentHenvendelseListe(
                    new WSHentHenvendelseListeRequest()
                            .withFodselsnummer(fodselsnummer)
                            .withTyper(typer))
                    .getAny()
                    .stream()
                    .map(XMLHenvendelse.class::cast);

            return wsHenvendelser
                    .map(HenvendelsesUtils::tilHenvendelse)
                    .collect(toList());
        }

        @Override
        public List<Henvendelse> hentTraad(String behandlingskjedeId) {
            List<Object> wsBehandlingskjeder = henvendelsePortType.hentBehandlingskjede(new WSHentBehandlingskjedeRequest().withBehandlingskjedeId(behandlingskjedeId)).getAny();

            return wsBehandlingskjeder.stream()
                    .map(XMLHenvendelse.class::cast)
                    .map(HenvendelsesUtils::tilHenvendelse)
                    .collect(toList());
        }
    }
}
