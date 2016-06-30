package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.*;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe.*;

@Configuration
public class HenvendelseMockContext {
    List<XMLHenvendelse> henvendelser = new ArrayList<XMLHenvendelse>() {{
        addAll(lagBehandlingskjede(FMLI, DateTime.now().minusDays(1), SPORSMAL_MODIA_UTGAAENDE));
        addAll(lagBehandlingskjede(ARBD, DateTime.now().minusDays(2), SPORSMAL_MODIA_UTGAAENDE));
        addAll(lagBehandlingskjede(ORT_HJE, DateTime.now().minusDays(3), SPORSMAL_MODIA_UTGAAENDE));
        addAll(lagBehandlingskjede(OVRG, DateTime.now().minusDays(4), SPORSMAL_MODIA_UTGAAENDE));
        addAll(lagBehandlingskjede(FMLI, DateTime.now().minusDays(3), SPORSMAL_SKRIFTLIG));
        addAll(lagBehandlingskjede(PENS, DateTime.now().minusDays(4), SPORSMAL_SKRIFTLIG, SVAR_SKRIFTLIG, SPORSMAL_MODIA_UTGAAENDE, SVAR_SBL_INNGAAENDE, SVAR_SKRIFTLIG));
        addAll(lagBehandlingskjede(HJLPM, DateTime.now().minusWeeks(2), REFERAT_OPPMOTE));
        addAll(lagBehandlingskjede(ARBD, DateTime.now().minusMonths(1), SPORSMAL_MODIA_UTGAAENDE, SVAR_SBL_INNGAAENDE));
        addAll(lagBehandlingskjede(BIL, DateTime.now().minusMonths(6), true, SPORSMAL_SKRIFTLIG, SVAR_SKRIFTLIG, SVAR_SKRIFTLIG));
        addAll(lagDokumentVarsel("DAG", "Vedtaksbrev om Dagpenger"));
    }};

    private Collection<? extends XMLHenvendelse> lagDokumentVarsel(String tema, String dokumentTittel) {
        Integer behandlingskjedeId = nextId();
        List<XMLHenvendelse> traad = new ArrayList<>();
        traad.add(new XMLHenvendelse()
                .withBehandlingsId(behandlingskjedeId.toString())
                .withBehandlingskjedeId(behandlingskjedeId.toString())
                .withOpprettetDato(DateTime.now().minusDays(3))
                .withTema(tema)
                .withLestDato(null)
                .withKorrelasjonsId("a1-b2")
                .withHenvendelseType(DOKUMENT_VARSEL.value())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLDokumentVarsel()
                        .withDokumenttittel(dokumentTittel)
                        .withJournalpostId("1")
                        .withDokumentIdListe("2")
                        .withTemanavn("Dagpenger")
                        .withFerdigstiltDato(DateTime.now().minusDays(3))
                )));

        return traad;    }

    @Bean
    public HenvendelseService henvendelseService(PersonService personService) {
        return new HenvendelseService.Default(henvendelsePortType(), sendInnHenvendelsePortType(), innsynHenvendelsePortType(), personService);
    }

    private HenvendelsePortType henvendelsePortType() {
        return new HenvendelsePortType() {
            @Override
            public void ping() {

            }

            @Override
            public WSHentHenvendelseResponse hentHenvendelse(final WSHentHenvendelseRequest req) {
                return new WSHentHenvendelseResponse().withAny(hent(req.getBehandlingsId()));
            }

            @Override
            public WSHentHenvendelseListeResponse hentHenvendelseListe(WSHentHenvendelseListeRequest req) {
                return new WSHentHenvendelseListeResponse().withAny(henvendelser.toArray());
            }

            @Override
            public WSHentBehandlingskjedeResponse hentBehandlingskjede(final WSHentBehandlingskjedeRequest req) {
                List<XMLHenvendelse> behandlingskjede = henvendelser.stream()
                        .filter(henvendelse -> req.getBehandlingskjedeId().equals(henvendelse.getBehandlingskjedeId()))
                        .collect(toList());
                return new WSHentBehandlingskjedeResponse().withAny(behandlingskjede.toArray());
            }
        };
    }

    private SendInnHenvendelsePortType sendInnHenvendelsePortType() {
        return new SendInnHenvendelsePortType() {
            @Override
            public void ping() {

            }

            @Override
            public WSSendInnHenvendelseResponse sendInnHenvendelse(WSSendInnHenvendelseRequest req) {
                String behandlingsId = nextId().toString();
                XMLHenvendelse henvendelse = (XMLHenvendelse) req.getAny();
                henvendelse.setBehandlingsId(behandlingsId);
                if (henvendelse.getBehandlingskjedeId() == null) {
                    henvendelse.setBehandlingskjedeId(behandlingsId);
                }
                henvendelser.add(henvendelse);
                return new WSSendInnHenvendelseResponse().withBehandlingsId(behandlingsId);
            }
        };
    }

    private InnsynHenvendelsePortType innsynHenvendelsePortType() {
        return new InnsynHenvendelsePortType() {
            @Override
            public void ping() {

            }

            @Override
            public void merkSomLest(List<String> ider) {
                for (String id : ider) {
                    hent(id).setLestDato(DateTime.now());
                }
            }
        };
    }

    private XMLHenvendelse hent(final String behandlingsId) {
        return henvendelser.stream()
                .filter(henvendelse -> behandlingsId.equals(henvendelse.getBehandlingsId()))
                .findFirst()
                .get();
    }

    private static List<XMLHenvendelse> lagBehandlingskjede(Temagruppe tema, DateTime dato, XMLHenvendelseType... typer) {
        return lagBehandlingskjede(tema, dato, false, typer);
    }

    private static List<XMLHenvendelse> lagBehandlingskjede(Temagruppe tema, DateTime dato, Boolean kassert, XMLHenvendelseType... typer) {
        Integer behandlingskjedeId = nextId();
        List<XMLHenvendelse> traad = new ArrayList<>();
        for (int i = 0; i < typer.length; i++) {
            XMLHenvendelseType type = typer[i];
            traad.add(lagHenvendelse(behandlingskjedeId + i, behandlingskjedeId, type, tema, dato.minusDays(typer.length - i), kassert));
        }
        return traad;
    }

    private static XMLHenvendelse lagHenvendelse(Integer behandlingsId, Integer behandlingskjedeId, XMLHenvendelseType type, final Temagruppe tema, DateTime opprettet, Boolean kassert) {
        final String tekst = (int) ((Math.random() * 1000)) + halvpartenEllerMer(LOREM);
        return new XMLHenvendelse()
                .withBehandlingsId(behandlingsId.toString())
                .withBehandlingskjedeId(behandlingskjedeId.toString())
                .withOpprettetDato(opprettet)
                .withLestDato(lestDato(type, opprettet))
                .withHenvendelseType(type.toString())
                .withMetadataListe(kassert ? null : new XMLMetadataListe().withMetadata(new XMLMelding() {
                    @Override
                    public String getTemagruppe() {
                        return tema.toString();
                    }

                    @Override
                    public String getFritekst() {
                        return tekst;
                    }
                }));

    }

    private static DateTime lestDato(XMLHenvendelseType type, DateTime opprettet) {
        if (asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE).contains(type)) {
            return opprettet;
        } else if (opprettet.isBefore(DateTime.now().minusWeeks(1))) {
            return opprettet.plusWeeks(1);
        } else {
            return null;
        }
    }

    private static String halvpartenEllerMer(String text) {
        int end = (text.length() / 2) + (int) ((text.length() / 2) * random.nextFloat());
        return text.substring(0, end);
    }

    private static final String LOREM = " <h1>Lorem</h1> ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore " +
            "\nhttps://navet.adeo.no/ansatt/Etatstjenester/Organisasjon+og+HR/Arbeidsmilj%C3%B8/Vil+lese+alle+svar.394779.cms\n" +
            "magna aliquam erat volutpat. Ut wisi enim ad www.nav.no minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. " +
            "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et " +
            "iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend" +
            " option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Typi non habent claritatem insitam; est usus legentis in iis qui facit" +
            " eorum claritatem. Investigationes demonstraverunt lectores legere me lius quod ii legunt saepius. Claritas est etiam processus dynamicus, qui" +
            " sequitur mutationem consuetudium lectorum. Mirum est notare quam littera gothica, quam nunc putamus parum claram, anteposuerit litterarum formas         ";

    private static Random random = new Random();

    private static Integer nextId() {
        return random.nextInt(1000000);
    }

}
