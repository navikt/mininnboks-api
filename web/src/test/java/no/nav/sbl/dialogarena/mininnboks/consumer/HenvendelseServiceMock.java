package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.REFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema.FAMILIE_OG_BARN;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema.INTERNASJONALT;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema.PENSJON;
import static org.joda.time.DateTime.now;

public class HenvendelseServiceMock implements HenvendelseService {

    Map<String, Henvendelse> henvendelser = new HashMap<>();

    public HenvendelseServiceMock() {
        Random random = new Random();

        Henvendelse traad1Spsm = new Henvendelse("" + random.nextInt(), SPORSMAL, "" + random.nextInt());
        traad1Spsm.opprettet = now().minusWeeks(2);
        traad1Spsm.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum. For nærmere info: www.google.com";
        traad1Spsm.tema = PENSJON;
        traad1Spsm.markerSomLest();
        traad1Spsm.lestDato = traad1Spsm.opprettet;
        henvendelser.put(traad1Spsm.id, traad1Spsm);

        Henvendelse traad1Svar1 = new Henvendelse("" + random.nextInt(), SVAR, traad1Spsm.traadId);
        traad1Svar1.opprettet = now().minusDays(6);
        traad1Svar1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
        traad1Svar1.tema = traad1Spsm.tema;
        traad1Svar1.markerSomLest();
        traad1Svar1.lestDato = now().minusDays(4);
        henvendelser.put(traad1Svar1.id, traad1Svar1);

        Henvendelse traad1Svar2 = new Henvendelse("" + random.nextInt(), SVAR, traad1Spsm.traadId);
        traad1Svar2.opprettet = now().minusDays(1);
        traad1Svar2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad1Svar2.tema = traad1Spsm.tema;
        traad1Svar2.markerSomLest();
        henvendelser.put(traad1Svar2.id, traad1Svar2);

        Henvendelse traad2Referat = new Henvendelse("" + random.nextInt(), REFERAT, "" + random.nextInt());
        traad2Referat.opprettet = now().minusHours(20);
        traad2Referat.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad2Referat.tema = ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT;
        traad2Referat.markerSomLest();
        henvendelser.put(traad2Referat.id, traad2Referat);

        Henvendelse traad3Spsm = new Henvendelse("" + random.nextInt(), SPORSMAL, "" + random.nextInt());
        traad3Spsm.opprettet = now().minusWeeks(12);
        traad3Spsm.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum";
        traad3Spsm.tema = INTERNASJONALT;
        traad3Spsm.markerSomLest();
        traad3Spsm.lestDato = traad3Spsm.opprettet;
        henvendelser.put(traad3Spsm.id, traad3Spsm);

        Henvendelse traad3Svar = new Henvendelse("" + random.nextInt(), SVAR, traad3Spsm.traadId);
        traad3Svar.opprettet = now();
        traad3Svar.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad3Svar.tema = traad3Spsm.tema;
        henvendelser.put(traad3Svar.id, traad3Svar);

        Henvendelse traad4Spsm = new Henvendelse("" + random.nextInt(), SPORSMAL, "" + random.nextInt());
        traad4Spsm.opprettet = now().minusHours(1);
        traad4Spsm.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad4Spsm.tema = FAMILIE_OG_BARN;
        traad4Spsm.markerSomLest();
        traad4Spsm.lestDato = traad4Spsm.opprettet;
        henvendelser.put(traad4Spsm.id, traad4Spsm);
    }

    @Override
    public String stillSporsmal(String fritekst, Tema tema, String aktorId) {
        Random random = new Random();
        Henvendelse spsm = new Henvendelse("" + random.nextInt(), SPORSMAL, "" + random.nextInt());
        spsm.fritekst = fritekst;
        spsm.tema = tema;
        spsm.opprettet = now();
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
