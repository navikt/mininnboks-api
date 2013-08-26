package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer;

import java.util.ArrayList;
import java.util.List;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import static no.nav.modig.lang.collections.IterUtils.on;

public interface MeldingService {

    String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId);
    List<Melding> hentAlleMeldinger(String aktorId);
    void merkMeldingSomLest(String behandlingsId);
    
    class Mock implements MeldingService {
    	
    	List<Melding> meldinger = new ArrayList<>();
    	
    	public Mock() {
    		Melding m = new Melding("123", Meldingstype.SPORSMAL, "1");
    		m.opprettet = DateTime.now();
    		m.fritekst = "Her er spørsmålet";
    		m.overskrift = "Spørsmål om Uføre";
    		m.tema = "Uføre";
			meldinger.add(m);
    	}

		@Override
		public String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId) {
			return null;
		}

		@Override
		public List<Melding> hentAlleMeldinger(String aktorId) {
			return meldinger;
		}

		@Override
		public void merkMeldingSomLest(String behandlingsId) {
		}
    	
    }

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
                    if (input instanceof WSMelding) {
                        WSMelding wsMelding = (WSMelding) input;
                        Melding melding = new Melding(
                                wsMelding.getBehandlingsId(),
                                Meldingstype.valueOf(wsMelding.getType().name()),
                                wsMelding.getTraadId());
                        melding.fritekst = wsMelding.getBeskrivelse();
                        melding.opprettet = wsMelding.getSistEndretDato();
                        melding.overskrift = wsMelding.getOverskrift();
                        melding.tema = wsMelding.getTema();
                        if (wsMelding.isLest()) {
                            melding.markerSomLest();
                        }

                        melding.lestDato = wsMelding.getLestDato();
                        return melding;
                    }
                    throw new RuntimeException("Kan ikke håndtere " + input.getClass());
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somMelding).collect();
        }

        @Override
        public void merkMeldingSomLest(String behandlingsId) {
            henvendelseWS.merkMeldingSomLest(behandlingsId);
        }

    }

}
