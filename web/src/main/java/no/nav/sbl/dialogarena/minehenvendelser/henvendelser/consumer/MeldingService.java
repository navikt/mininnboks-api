package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer;

import java.util.List;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import org.apache.commons.collections15.Transformer;

import static no.nav.modig.lang.collections.IterUtils.on;

public interface MeldingService {

    String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId);
    List<Melding> hentAlleMeldinger(String aktorId);
    void merkMeldingSomLelst(String behandlingsId);

    class Default implements MeldingService {

        private final HenvendelsePortType henvendelseWS;
        private final SporsmalOgSvarPortType spsmogsvarWS;

        public Default(HenvendelsePortType henvendelseWS, SporsmalOgSvarPortType spsmogsvarWS) {
    		this.henvendelseWS = henvendelseWS;
    		this.spsmogsvarWS = spsmogsvarWS;
    	}

    	@Override
        public String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId) {
            return spsmogsvarWS.opprettSporsmal(new WSSporsmal().withFritekst(fritekst).withTema(tema).withOverskrift(overskrift), aktorId);
        }

        @Override
        public List<Melding> hentAlleMeldinger(String aktorId) {
        	Transformer<WSHenvendelse, Melding> somMelding = new Transformer<WSHenvendelse, Melding>() {
    			@Override
                public Melding transform(WSHenvendelse input) {
                    Melding melding = new Melding()
                            .withId(input.getBehandlingsId())
                            .withFritekst(input.getBeskrivelse())
                            .withOpprettet(input.getSistEndretDato())
                            .withOverskrift(input.getOverskrift())
                            .withTema(input.getTema())
                            .withLest(input.isLest());
                    if (input instanceof WSMelding) {
                        WSMelding wsMelding = (WSMelding) input;
                        melding.withType(Meldingstype.valueOf(wsMelding.getType().name()));
                        melding.withTraadId(wsMelding.getTraadId());
                    } 
                    return melding;
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somMelding).collect();
        }

        @Override
        public void merkMeldingSomLelst(String behandlingsId) {
            henvendelseWS.merkMeldingSomLest(behandlingsId);
        }

    }

}
