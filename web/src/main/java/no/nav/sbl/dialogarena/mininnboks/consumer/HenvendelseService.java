package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSSendHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.informasjon.v2.HenvendelseInformasjonV2PortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.informasjon.v2.meldinger.WSHentHenvendelseListeRequest;

import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    WSSendHenvendelseResponse stillSporsmal(String fritekst, Tema tema, String aktorId);

    List<Henvendelse> hentAlleHenvendelser(String fnr);

    void merkHenvendelseSomLest(String behandlingsId);

    class Default implements HenvendelseService {

        private final HenvendelseInformasjonV2PortType henvendelseInformasjonWS;

        private final HenvendelseAktivitetV2PortType henvendelseAktivitetWS;

        public Default(HenvendelseInformasjonV2PortType henvendelseInformasjonWS, HenvendelseAktivitetV2PortType henvendelseAktivitetWS) {
            this.henvendelseInformasjonWS = henvendelseInformasjonWS;
            this.henvendelseAktivitetWS = henvendelseAktivitetWS;
        }

        @Override
        public WSSendHenvendelseResponse stillSporsmal(String fritekst, Tema tema, String aktorId) {
            XMLBehandlingsinformasjonV2 info =
                    new XMLBehandlingsinformasjonV2()
                            .withHenvendelseType(SPORSMAL.name())
                            .withAktor(new XMLAktor().withFodselsnummer(aktorId))
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLSporsmal()
                                            .withTemagruppe(tema.name())
                                            .withFritekst(fritekst)));

            return henvendelseAktivitetWS.sendHenvendelse(
                    new WSSendHenvendelseRequest()
                            .withType(SPORSMAL.name())
                            .withFodselsnummer(aktorId)
                            .withAny(info));
        }

        @Override
        public void merkHenvendelseSomLest(String behandlingsId) {
            // Sett henvendelse til lest vha henvendelseAktivitetWS
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String fnr) {
            List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());
            return on(henvendelseInformasjonWS.hentHenvendelseListe(
                    new WSHentHenvendelseListeRequest()
                            .withFodselsnummer(fnr)
                            .withTyper(typer))
                    .getAny())
                    .map(TIL_HENVENDELSE).collect();
        }
    }
}
