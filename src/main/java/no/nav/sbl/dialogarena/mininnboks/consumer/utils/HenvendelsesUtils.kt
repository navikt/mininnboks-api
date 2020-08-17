package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.fromValue;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;

public abstract class HenvendelsesUtils {

    private static TekstService tekstService;

    final static Logger logger = LoggerFactory.getLogger(HenvendelsesUtils.class);
    private static final String LINE_REPLACEMENT_STRING = UUID.randomUUID().toString();
    private static final String LINE_BREAK = "\n";

    public static final List<Henvendelsetype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG_DIREKTE, SVAR_SBL_INNGAAENDE);

    public static void setTekstService(TekstService tekstService) {
        HenvendelsesUtils.tekstService = tekstService;
    }

    private static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE, SPORSMAL_SKRIFTLIG_DIREKTE);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.INFOMELDING_MODIA_UTGAAENDE, INFOMELDING_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
            put(XMLHenvendelseType.DOKUMENT_VARSEL, DOKUMENT_VARSEL);
            put(XMLHenvendelseType.OPPGAVE_VARSEL, OPPGAVE_VARSEL);
            put(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, DELVIS_SVAR_SKRIFTLIG);
        }
    };

    private static final DomainMapper<XMLHenvendelse, Henvendelse> domainMapper = new DomainMapper<>();

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> DEFAULT_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse) -> true,
            (xmlHenvendelse, henvendelse) -> {
                henvendelse = new Henvendelse(xmlHenvendelse.getBehandlingsId());
                henvendelse.opprettet = utilDate(xmlHenvendelse.getOpprettetDato());
                henvendelse.avsluttet = utilDate(xmlHenvendelse.getAvsluttetDato());
                henvendelse.ferdigstiltUtenSvar = xmlHenvendelse.isFerdigstiltUtenSvar();
                henvendelse.traadId = ofNullable(xmlHenvendelse.getBehandlingskjedeId()).orElse(xmlHenvendelse.getBehandlingsId());
                henvendelse.eksternAktor = xmlHenvendelse.getEksternAktor();
                henvendelse.tilknyttetEnhet = xmlHenvendelse.getTilknyttetEnhet();
                henvendelse.kontorsperreEnhet = xmlHenvendelse.getKontorsperreEnhet();
                henvendelse.erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt();
                henvendelse.type = HENVENDELSETYPE_MAP.get(fromValue(xmlHenvendelse.getHenvendelseType()));
                henvendelse.brukersEnhet = xmlHenvendelse.getBrukersEnhet();
                henvendelse.fraBruker = FRA_BRUKER.contains(henvendelse.type);
                henvendelse.fraNav = !henvendelse.fraBruker;
                henvendelse.temaKode = xmlHenvendelse.getTema();
                henvendelse.korrelasjonsId = xmlHenvendelse.getKorrelasjonsId();
                return henvendelse;
            }
    );

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> LEST_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse) -> true,
            (xmlHenvendelse, henvendelse) -> {
                if (FRA_BRUKER.contains(henvendelse.type)) {
                    henvendelse.markerSomLest();
                } else {
                    henvendelse.markerSomLest(utilDate(xmlHenvendelse.getLestDato()));
                }

                return henvendelse;
            }
    );

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> KASSERT_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse) -> xmlHenvendelse.getMetadataListe() == null,
            (xmlHenvendelse, henvendelse) -> {
                henvendelse.kassert = true;
                henvendelse.fritekst = tekstService.hentTekst("innhold.kassert");
                henvendelse.statusTekst = tekstService.hentTekst("temagruppe.kassert");
                henvendelse.temagruppe = null;
                henvendelse.kanal = null;

                return henvendelse;
            },
            true
    );

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> DOKUMENTVARSEL_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse -> DOKUMENT_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())),
            (xmlHenvendelse, henvendelse) -> {
                XMLDokumentVarsel varsel = (XMLDokumentVarsel) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                henvendelse.statusTekst = varsel.getDokumenttittel();
                henvendelse.withTemaNavn(varsel.getTemanavn());
                henvendelse.temagruppe = null;
                henvendelse.fritekst = ofNullable(varsel.getFritekst()).orElse("");
                henvendelse.opprettet = utilDate(varsel.getFerdigstiltDato());
                henvendelse.dokumentIdListe.addAll(varsel.getDokumentIdListe());
                henvendelse.journalpostId = varsel.getJournalpostId();

                return henvendelse;
            }
    );

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> OPPGAVEVARSEL_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse -> OPPGAVE_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())),
            (xmlHenvendelse, henvendelse) -> {
                XMLOppgaveVarsel varsel = (XMLOppgaveVarsel) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                henvendelse.oppgaveType = varsel.getOppgaveType();
                henvendelse.oppgaveUrl = varsel.getOppgaveURL();
                henvendelse.statusTekst = hentTekst(tekstService, format("oppgave.%s", varsel.getOppgaveType()), "oppgave.GEN");
                henvendelse.fritekst = hentTekst(tekstService, format("oppgave.%s.fritekst", varsel.getOppgaveType()), "oppgave.GEN.fritekst");
                return henvendelse;
            }
    );

    private static final DomainMapper.Mapper<XMLHenvendelse, Henvendelse> IKKEVARSEL_MAPPER = new DomainMapper.Mapper<>(
            (xmlHenvendelse -> (
                    !OPPGAVE_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType()) &&
                            !DOKUMENT_VARSEL.name().equals(xmlHenvendelse.getHenvendelseType())
            )),
            (xmlHenvendelse, henvendelse) -> {
                XMLMelding xmlMelding = (XMLMelding) xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                henvendelse.temagruppe = Temagruppe.valueOf(xmlMelding.getTemagruppe());
                henvendelse.temagruppeNavn = hentTemagruppeNavn(henvendelse.temagruppe.name());
                henvendelse.statusTekst = HenvendelsesUtils.statusTekst(henvendelse);
                henvendelse.fritekst = cleanOutHtml(xmlMelding.getFritekst());

                if (xmlMelding instanceof XMLMeldingTilBruker) {
                    XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMelding;
                    henvendelse.kanal = meldingTilBruker.getKanal();
                }

                return henvendelse;
            }
    );

    static {
        domainMapper.registerMapper(DEFAULT_MAPPER);
        domainMapper.registerMapper(LEST_MAPPER);
        domainMapper.registerMapper(KASSERT_MAPPER);
        domainMapper.registerMapper(DOKUMENTVARSEL_MAPPER);
        domainMapper.registerMapper(OPPGAVEVARSEL_MAPPER);
        domainMapper.registerMapper(IKKEVARSEL_MAPPER);
    }

    public static Henvendelse tilHenvendelse(XMLHenvendelse wsMelding) {
        return domainMapper.apply(wsMelding);
    }

    public static String hentTekst(TekstService tekster, String key, String defaultKey) {
        try {
            return tekster.hentTekst(key);
        } catch (Exception e) {
            return tekster.hentTekst(defaultKey);
        }
    }

    private static String hentTemagruppeNavn(String temagruppeNavn) {
        try {
            return tekstService.hentTekst(temagruppeNavn);
        } catch (MissingResourceException exception) {
            logger.error("Finner ikke cms-oppslag for " + temagruppeNavn, exception);
            return temagruppeNavn;
        }
    }

    public static String cleanOutHtml(String text) {
        String clean = Jsoup.clean(text.replaceAll(LINE_BREAK, LINE_REPLACEMENT_STRING), Whitelist.none());
        return StringEscapeUtils.unescapeHtml4(clean).replaceAll(LINE_REPLACEMENT_STRING, LINE_BREAK);
    }

    public static String fjernHardeMellomrom(String tekst) {
        return tekst.replaceAll("[\\u00a0\\u2007\\u202f]", " ");
    }

    private static String statusTekst(Henvendelse henvendelse) { //NOSONAR
        if (!skalVisesTilBruker(henvendelse.type)) {
            return "";
        }
        String type = hentTemagruppeNavn(format("status.%s", henvendelse.type.name()));
        String temagruppe = hentTemagruppeNavn(henvendelse.temagruppe.name());
        return format(type, temagruppe);

    }

    private static boolean skalVisesTilBruker(Henvendelsetype type) {
        return type != DELVIS_SVAR_SKRIFTLIG;
    }

    private static Date utilDate(DateTime dateTime) {
        return dateTime != null ? dateTime.toDate() : null;
    }

}
