package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SAMTALEREFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SVAR_SKRIFTLIG;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe.FMLI;
import static org.joda.time.DateTime.now;

public class HenvendelseServiceMock implements HenvendelseService {

    Map<String, Henvendelse> henvendelser = new HashMap<>();

    public HenvendelseServiceMock() {
        Random random = new Random();

        Henvendelse traad1Spsm = new Henvendelse("" + random.nextInt());
        traad1Spsm.type = SPORSMAL_SKRIFTLIG;
        traad1Spsm.traadId = traad1Spsm.id;
        traad1Spsm.opprettet = now().minusWeeks(2);
        traad1Spsm.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas" +
                " humanitatis per seacula quarta decima et quinta decima. Eodem modo typi, qui nunc nobis videntur parum clari, fiant sollemnes in futurum. For nærmere info: www.google.com";
        traad1Spsm.temagruppe = FMLI;
        traad1Spsm.markerSomLest(traad1Spsm.opprettet);
        henvendelser.put(traad1Spsm.id, traad1Spsm);

        Henvendelse traad1Svar1 = new Henvendelse("" + random.nextInt());
        traad1Svar1.type = SVAR_SKRIFTLIG;
        traad1Svar1.traadId = traad1Spsm.traadId;
        traad1Svar1.opprettet = now().minusDays(6);
        traad1Svar1.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
        traad1Svar1.temagruppe = traad1Spsm.temagruppe;
        traad1Svar1.markerSomLest(now().minusDays(4));
        henvendelser.put(traad1Svar1.id, traad1Svar1);

        Henvendelse traad1Svar2 = new Henvendelse("" + random.nextInt());
        traad1Svar2.type = SVAR_SKRIFTLIG;
        traad1Svar2.traadId = traad1Spsm.traadId;
        traad1Svar2.opprettet = now().minusDays(1);
        traad1Svar2.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad1Svar2.temagruppe = traad1Spsm.temagruppe;
        traad1Svar2.markerSomLest();
        henvendelser.put(traad1Svar2.id, traad1Svar2);

        Henvendelse traad2Referat = new Henvendelse("" + random.nextInt());
        traad2Referat.type = SAMTALEREFERAT_OPPMOTE;
        traad2Referat.traadId =  traad2Referat.id;
        traad2Referat.opprettet = now().minusHours(20);
        traad2Referat.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas";
        traad2Referat.temagruppe = ARBD;
        traad2Referat.markerSomLest();
        traad2Referat.kanal = "Kanal";
        henvendelser.put(traad2Referat.id, traad2Referat);

        Henvendelse traad3Spsm = new Henvendelse("" + random.nextInt());
        traad3Spsm.type = SPORSMAL_SKRIFTLIG;
        traad3Spsm.traadId = traad3Spsm.id;
        traad3Spsm.opprettet = now().minusWeeks(12);
        traad3Spsm.fritekst = null;
        traad3Spsm.temagruppe = null;
        traad3Spsm.markerSomLest(traad3Spsm.opprettet);
        henvendelser.put(traad3Spsm.id, traad3Spsm);

        Henvendelse traad3Svar = new Henvendelse("" + random.nextInt());
        traad3Svar.type = SVAR_SKRIFTLIG;
        traad3Svar.traadId = traad3Spsm.traadId;
        traad3Svar.opprettet = now();
        traad3Svar.fritekst = null;
        traad3Svar.temagruppe = null;
        henvendelser.put(traad3Svar.id, traad3Svar);

        Henvendelse traad4Spsm = new Henvendelse("" + random.nextInt());
        traad4Spsm.type = SPORSMAL_SKRIFTLIG;
        traad4Spsm.traadId = traad4Spsm.id;
        traad4Spsm.opprettet = now().minusHours(1);
        traad4Spsm.fritekst = " https://navet.adeo.no/ansatt/Etatstjenester/Organisasjon+og+HR/Arbeidsmilj%C3%B8/Vil+lese+alle+svar.394779.cms Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. " +
                "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
                " iriure dolor in hendrerit in vulputate velit esse molestie consequat, https://tjenester.nav.no/stillinger/stilling?sort=akt&s1=817395&rpp=50&p=0&ID=8641930&rv=al vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto" +
                " odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
                " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
                " eorum claritatem. Investigationes demonstraverunt https://www.nav.no/no/Person/Hjelpemidler/Tjenester+og+produkter/Hjelpemidler/Om+hjelpemidler.358874.cms lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
                " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam http://www.test.no/?id=1&id=2 nunc putamus parum claram, anteposuerit litterarum formas";
        traad4Spsm.temagruppe = FMLI;
        traad4Spsm.markerSomLest(traad4Spsm.opprettet);
        henvendelser.put(traad4Spsm.id, traad4Spsm);
    }

    @Override
    public WSSendInnHenvendelseResponse stillSporsmal(String fritekst, Temagruppe temagruppe, String fodselsnummer) {
        Random random = new Random();
        Henvendelse spsm = new Henvendelse("" + random.nextInt());
        spsm.type = SPORSMAL_SKRIFTLIG;
        spsm.traadId = "" + random.nextInt();
        spsm.fritekst = fritekst;
        spsm.temagruppe = temagruppe;
        spsm.opprettet = now();
        spsm.markerSomLest();
        henvendelser.put(spsm.id, spsm);
        return new WSSendInnHenvendelseResponse().withBehandlingsId(spsm.id);
    }

    @Override
    public List<Henvendelse> hentAlleHenvendelser(String fnr) {
        return new ArrayList<>(henvendelser.values());
    }

    @Override
    public void merkHenvendelseSomLest(Henvendelse henvendelse) {
    }
}
