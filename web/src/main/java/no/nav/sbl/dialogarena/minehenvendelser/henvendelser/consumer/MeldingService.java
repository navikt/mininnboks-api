package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    class Mock implements MeldingService {

        Map<String, Melding> meldinger = new HashMap<String, Melding>();

        public Mock() {
            Random random = new Random();

            Melding spsm1 = new Melding("" + random.nextInt(), Meldingstype.SPORSMAL, "" + random.nextInt());
            spsm1.opprettet = DateTime.now().minusWeeks(2);
            spsm1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                    " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.";
            spsm1.overskrift = "Spørsmål om Uføre";
            spsm1.tema = "Uføre";
            spsm1.markerSomLest();
            spsm1.lestDato = spsm1.opprettet;
            meldinger.put(spsm1.id, spsm1);

            Melding svar1 = new Melding("" + random.nextInt(), Meldingstype.SVAR, spsm1.traadId);
            svar1.opprettet = DateTime.now().minusDays(6);
            svar1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
            svar1.overskrift = "Re: " + spsm1.overskrift;
            svar1.tema = spsm1.tema;
            svar1.markerSomLest();
            svar1.lestDato = DateTime.now().minusDays(4);
            meldinger.put(svar1.id, svar1);

            Melding spsm2 = new Melding("" + random.nextInt(), Meldingstype.SPORSMAL, spsm1.traadId);
            spsm2.opprettet = DateTime.now().minusDays(2);
            spsm2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                    " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum.";
            spsm2.overskrift = "Spørsmål om Uføre";
            spsm2.tema = "Uføre";
            spsm2.markerSomLest();
            spsm2.lestDato = spsm2.opprettet;
            meldinger.put(spsm2.id, spsm2);

            Melding svar2 = new Melding("" + random.nextInt(), Meldingstype.SVAR, spsm1.traadId);
            svar2.opprettet = DateTime.now().minusDays(1);
            svar2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            svar2.tema = spsm2.tema;
            svar2.overskrift = "Re: " + spsm2.overskrift;
            meldinger.put(svar2.id, svar2);

            Melding spsm3 = new Melding("" + random.nextInt(), Meldingstype.SPORSMAL, "" + random.nextInt());
            spsm3.opprettet = DateTime.now().minusWeeks(12);
            spsm3.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum";
            spsm3.tema = "Pensjon";
            spsm3.overskrift = "Spørsmål om " + spsm3.tema;
            spsm3.markerSomLest();
            spsm3.lestDato = spsm3.opprettet;
            meldinger.put(spsm3.id, spsm3);

            Melding svar3 = new Melding("" + random.nextInt(), Meldingstype.SVAR, spsm3.traadId);
            svar3.opprettet = DateTime.now();
            svar3.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            svar3.tema = spsm3.tema;
            svar3.overskrift = "Re: " + spsm3.overskrift;
            meldinger.put(svar3.id, svar3);

            Melding spsm4 = new Melding("" + random.nextInt(), Meldingstype.SPORSMAL, "" + random.nextInt());
            spsm4.opprettet = DateTime.now().minusHours(1);
            spsm4.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                    " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                    " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                    " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                    " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
            spsm4.tema = "Sykepenger";
            spsm4.overskrift = "Spørsmål om " + spsm4.tema;
            spsm4.markerSomLest();
            spsm4.lestDato = spsm4.opprettet;
            meldinger.put(spsm4.id, spsm4);
        }

        @Override
        public String stillSporsmal(String fritekst, String overskrift, String tema, String aktorId) {
            Random random = new Random();
            Melding spsm = new Melding("" + random.nextInt(), Meldingstype.SPORSMAL, "" + random.nextInt());
            spsm.fritekst = fritekst;
            spsm.overskrift = overskrift;
            spsm.opprettet = DateTime.now();
            spsm.markerSomLest();
            meldinger.put(spsm.id, spsm);
            return spsm.id;
        }

        @Override
        public List<Melding> hentAlleMeldinger(String aktorId) {
            return new ArrayList<>(meldinger.values());
        }

        @Override
        public void merkMeldingSomLest(String behandlingsId) {
            Melding melding = meldinger.get(behandlingsId);
            melding.markerSomLest();
            meldinger.put(melding.id, melding);
        }

    }

}
