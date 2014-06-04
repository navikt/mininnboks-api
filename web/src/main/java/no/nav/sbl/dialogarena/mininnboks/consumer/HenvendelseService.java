package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSOppdaterHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;

import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.tilXMLBehandlingsinformasjonV2;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

    WSSendHenvendelseResponse stillSporsmal(String fritekst, Tema tema, String fodselsnummer);

    List<Henvendelse> hentAlleHenvendelser(String fodselsnummer);

    void oppdaterHenvendelse(Henvendelse henvendelse);

    class Default implements HenvendelseService {

        private final HenvendelsePortType henvendelsePortType;

        private final SendHenvendelsePortType sendHenvendelsePortType;

        public Default(HenvendelsePortType henvendelsePortType, SendHenvendelsePortType sendHenvendelsePortType) {
            this.henvendelsePortType = henvendelsePortType;
            this.sendHenvendelsePortType = sendHenvendelsePortType;
        }

        @Override
        public WSSendHenvendelseResponse stillSporsmal(String fritekst, Tema tema, String fodselsnummer) {
            XMLBehandlingsinformasjonV2 info =
                    new XMLBehandlingsinformasjonV2()
                            .withHenvendelseType(SPORSMAL.name())
                            .withAktor(new XMLAktor().withFodselsnummer(fodselsnummer))
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLSporsmal()
                                            .withTemagruppe(tema.name())
                                            .withFritekst(fritekst)));

            return sendHenvendelsePortType.sendHenvendelse(
                    new WSSendHenvendelseRequest()
                            .withType(SPORSMAL.name())
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public void oppdaterHenvendelse(Henvendelse henvendelse) {
            sendHenvendelsePortType.oppdaterHenvendelse(
                    new WSOppdaterHenvendelseRequest()
                            .withBehandlingsId(henvendelse.id)
                            .withAny(tilXMLBehandlingsinformasjonV2(henvendelse)));
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String fodselsnummer) {
            List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());
            return on(henvendelsePortType.hentHenvendelseListe(
                    new WSHentHenvendelseListeRequest()
                            .withFodselsnummer(fodselsnummer)
                            .withTyper(typer))
                    .getAny())
                    .map(TIL_HENVENDELSE).collect();
        }
    }
}
