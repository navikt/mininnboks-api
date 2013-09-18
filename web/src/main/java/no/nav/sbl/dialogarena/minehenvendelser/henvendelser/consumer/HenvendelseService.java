package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal.Tema;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.SporsmalinnsendingPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.informasjon.WSSporsmal;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.not;

public interface HenvendelseService {

    String stillSporsmal(String fritekst, String overskrift, Tema tema, String aktorId);
    List<Henvendelse> hentAlleHenvendelser(String aktorId);
    void merkHenvendelseSomLest(String behandlingsId);

    class Default implements HenvendelseService {

        private final HenvendelsePortType henvendelseWS;

        private final SporsmalinnsendingPortType sporsmalinnsendingPortType;
        public Default(HenvendelsePortType henvendelseWS, SporsmalinnsendingPortType sporsmalinnsendingPortType) {
            this.henvendelseWS = henvendelseWS;
            this.sporsmalinnsendingPortType = sporsmalinnsendingPortType;
        }

        @Override
        public String stillSporsmal(String fritekst, String overskrift, Tema tema, String aktorId) {
            return sporsmalinnsendingPortType.opprettSporsmal(new WSSporsmal().withFritekst(fritekst).withTema(tema.navn()), aktorId);
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String aktorId) {
            Transformer<WSHenvendelse, Henvendelse> somHenvendelse = new Transformer<WSHenvendelse, Henvendelse>() {
                @Override
                public Henvendelse transform(WSHenvendelse wsHenvendelse) {
                    String henvendelseType = wsHenvendelse.getHenvendelseType();
                    if (!"SPORSMAL".equals(henvendelseType) && !"SVAR".equals(henvendelseType)) {
                        return null;
                    }
                    Henvendelse henvendelse = new Henvendelse(
                            wsHenvendelse.getBehandlingsId(),
                            Henvendelsetype.valueOf(henvendelseType),
                            wsHenvendelse.getTraad());
                    henvendelse.opprettet = wsHenvendelse.getOpprettetDato();
                    henvendelse.tema = wsHenvendelse.getTema();
                    henvendelse.overskrift = ("SPORSMAL".equals(henvendelseType) ? "Spørsmål om " : "Svar på ") + wsHenvendelse.getTema();
                    henvendelse.lestDato = wsHenvendelse.getLestDato();
                    if (wsHenvendelse.getLestDato() != null) {
                        henvendelse.markerSomLest();
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> behandlingsresultat;
                    try {
                        behandlingsresultat = mapper.readValue(wsHenvendelse.getBehandlingsresultat(), Map.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Kunne ikke lese ut behandlingsresultat", e);
                    }

                    henvendelse.fritekst = behandlingsresultat.get("fritekst");
                    return henvendelse;
                }
            };
            return on(henvendelseWS.hentHenvendelseListe(aktorId)).map(somHenvendelse).filter(not(equalTo(null))).collect();
        }

        @Override
        public void merkHenvendelseSomLest(String behandlingsId) {
            henvendelseWS.merkMeldingSomLest(behandlingsId);
        }

    }

    class Mock implements HenvendelseService {

        Map<String, Henvendelse> henvendelser = new HashMap<>();

        public Mock() {
            Random random = new Random();

            Henvendelse spsm1 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
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
            henvendelser.put(spsm1.id, spsm1);

            Henvendelse svar1 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm1.traadId);
            svar1.opprettet = DateTime.now().minusDays(6);
            svar1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                    " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
            svar1.overskrift = "Re: " + spsm1.overskrift;
            svar1.tema = spsm1.tema;
            svar1.markerSomLest();
            svar1.lestDato = DateTime.now().minusDays(4);
            henvendelser.put(svar1.id, svar1);

            Henvendelse spsm2 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, spsm1.traadId);
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
            henvendelser.put(spsm2.id, spsm2);

            Henvendelse svar2 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm1.traadId);
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
            henvendelser.put(svar2.id, svar2);

            Henvendelse spsm3 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
            spsm3.opprettet = DateTime.now().minusWeeks(12);
            spsm3.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                    "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum";
            spsm3.tema = "Pensjon";
            spsm3.overskrift = "Spørsmål om " + spsm3.tema;
            spsm3.markerSomLest();
            spsm3.lestDato = spsm3.opprettet;
            henvendelser.put(spsm3.id, spsm3);

            Henvendelse svar3 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SVAR, spsm3.traadId);
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
            henvendelser.put(svar3.id, svar3);

            Henvendelse spsm4 = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
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
            henvendelser.put(spsm4.id, spsm4);
        }

        @Override
        public String stillSporsmal(String fritekst, String overskrift, Tema tema, String aktorId) {
            Random random = new Random();
            Henvendelse spsm = new Henvendelse("" + random.nextInt(), Henvendelsetype.SPORSMAL, "" + random.nextInt());
            spsm.fritekst = fritekst;
            spsm.overskrift = overskrift;
            spsm.opprettet = DateTime.now();
            spsm.markerSomLest();
            henvendelser.put(spsm.id, spsm);
            return spsm.id;
        }

        @Override
        public List<Henvendelse> hentAlleHenvendelser(String aktorId) {
            return new ArrayList<>(henvendelser.values());
        }

        @Override
        public void merkHenvendelseSomLest(String behandlingsId) {
            Henvendelse henvendelse = henvendelser.get(behandlingsId);
            henvendelse.markerSomLest();
            henvendelser.put(henvendelse.id, henvendelse);
        }

    }

}
