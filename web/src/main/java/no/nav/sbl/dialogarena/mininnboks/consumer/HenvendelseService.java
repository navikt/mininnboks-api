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
import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.TIL_HENVENDELSE;
import static org.joda.time.DateTime.now;

public interface HenvendelseService {

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
                            .withHenvendelseType(SPORSMAL.name())
                            .withOpprettetDato(now())
                            .withAvsluttetDato(now())
                            .withMetadataListe(new XMLMetadataListe().withMetadata(
                                    new XMLMeldingFraBruker()
                                            .withTemagruppe(temagruppe.name())
                                            .withFritekst(fritekst)));

            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    new WSSendInnHenvendelseRequest()
                            .withType(SPORSMAL.name())
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info));
        }

        @Override
        public void merkHenvendelseSomLest(Henvendelse henvendelse) {
            innsynHenvendelsePortType.merkSomLest(new ArrayList<>(Arrays.asList(henvendelse.id)));
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
