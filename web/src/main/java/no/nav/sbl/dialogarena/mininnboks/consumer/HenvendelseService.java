package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.SporsmalinnsendingPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.informasjon.WSSporsmal;
import org.apache.commons.collections15.Transformer;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;

public interface HenvendelseService {

    String stillSporsmal(String fritekst, Tema tema, String aktorId);
    List<Henvendelse> hentAlleHenvendelser(String fnr);
    void merkHenvendelseSomLest(String behandlingsId);

    class Default implements HenvendelseService {

        private final HenvendelseMeldingerPortType henvendelseWS;

        private final SporsmalinnsendingPortType sporsmalinnsendingPortType;
        public Default(HenvendelseMeldingerPortType henvendelseWS, SporsmalinnsendingPortType sporsmalinnsendingPortType) {
            this.henvendelseWS = henvendelseWS;
            this.sporsmalinnsendingPortType = sporsmalinnsendingPortType;
        }

        @Override
        public String stillSporsmal(String fritekst, Tema tema, String aktorId) {
            return sporsmalinnsendingPortType.opprettSporsmal(new WSSporsmal().withFritekst(fritekst).withTema(tema.toString()), aktorId);
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String fnr) {
            Transformer<WSMelding, Henvendelse> somHenvendelse = new Transformer<WSMelding, Henvendelse>() {
                @Override
                public Henvendelse transform(WSMelding melding) {
                    Henvendelsetype henvendelseType = melding.getMeldingsType() == WSMeldingstype.INNGAENDE ? SPORSMAL : SVAR;
                    Henvendelse henvendelse = new Henvendelse(melding.getBehandlingsId(), henvendelseType, melding.getTraad());
                    henvendelse.opprettet = melding.getOpprettetDato();
                    henvendelse.tema = Tema.valueOf(melding.getTemastruktur());
                    henvendelse.setLest(melding.getLestDato() != null);
                    henvendelse.lestDato = melding.getLestDato();
                    henvendelse.fritekst = melding.getTekst();
                    return henvendelse;
                }
            };
            return on(henvendelseWS.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding()).map(somHenvendelse).collect();
        }

        @Override
        public void merkHenvendelseSomLest(String behandlingsId) {
            henvendelseWS.merkMeldingSomLest(behandlingsId);
        }

    }
}
