package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.fromValue;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;

public abstract class HenvendelsesUtils {

    private static TekstService tekstService;

    final static Logger logger = LoggerFactory.getLogger(HenvendelsesUtils.class);
    private static final String LINE_REPLACEMENT_STRING = UUID.randomUUID().toString();
    private static final String LINE_BREAK = "\n";

    public static final List<Henvendelsetype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final Function<XMLHenvendelse, Henvendelse> TIL_HENVENDELSE = xmlHenvendelse -> tilHenvendelse(xmlHenvendelse);

    public static void setTekstService(TekstService tekstService) {
        HenvendelsesUtils.tekstService = tekstService;
    }

    private static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
            put(XMLHenvendelseType.DOKUMENT_VARSEL, DOKUMENT_VARSEL);
        }
    };

    private static Henvendelse tilHenvendelse(XMLHenvendelse wsMelding) {
        XMLHenvendelse info = wsMelding;

        Henvendelse henvendelse = new Henvendelse(info.getBehandlingsId());
        henvendelse.opprettet = info.getOpprettetDato();
        henvendelse.avsluttet = info.getAvsluttetDato();
        henvendelse.traadId = ofNullable(info.getBehandlingskjedeId()).orElse(info.getBehandlingsId());
        henvendelse.eksternAktor = info.getEksternAktor();
        henvendelse.tilknyttetEnhet = info.getTilknyttetEnhet();
        henvendelse.kontorsperreEnhet = info.getKontorsperreEnhet();
        henvendelse.erTilknyttetAnsatt = info.isErTilknyttetAnsatt();
        henvendelse.type = HENVENDELSETYPE_MAP.get(fromValue(info.getHenvendelseType()));
        henvendelse.brukersEnhet = info.getBrukersEnhet();
        henvendelse.fraBruker = FRA_BRUKER.contains(henvendelse.type);
        henvendelse.fraNav = !henvendelse.fraBruker;
        henvendelse.korrelasjonsId = info.getKorrelasjonsId();
        if (FRA_BRUKER.contains(henvendelse.type)) {
            henvendelse.markerSomLest();
        } else {
            henvendelse.markerSomLest(info.getLestDato());
        }

        if (innholdErKassert(info)) {
            henvendelse.kassert = true;
            henvendelse.fritekst = tekstService.hentTekst("innhold.kassert");
            henvendelse.statusTekst = tekstService.hentTekst("temagruppe.kassert");
            henvendelse.temagruppe = null;
            henvendelse.kanal = null;
            return henvendelse;
        }

        if (DOKUMENT_VARSEL.name().equals(info.getHenvendelseType())) {
            XMLDokumentVarsel varsel = (XMLDokumentVarsel) info.getMetadataListe().getMetadata().get(0);
            henvendelse.statusTekst = varsel.getDokumenttittel();
            henvendelse.withTemaNavn(varsel.getTemanavn());
            henvendelse.temagruppe = null;
            henvendelse.fritekst = ofNullable(varsel.getFritekst()).orElse("");
            henvendelse.opprettet = varsel.getFerdigstiltDato();
            henvendelse.dokumentIdListe.addAll(varsel.getDokumentIdListe());
            henvendelse.journalpostId = varsel.getJournalpostId();
        } else {
            XMLMelding xmlMelding = (XMLMelding) info.getMetadataListe().getMetadata().get(0);
            henvendelse.temagruppe = Temagruppe.valueOf(xmlMelding.getTemagruppe());
            henvendelse.temagruppeNavn = hentTemagruppeNavn(henvendelse.temagruppe.name());
            henvendelse.statusTekst = statusTekst(henvendelse);
            henvendelse.fritekst = cleanOutHtml(xmlMelding.getFritekst());

            if (xmlMelding instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMelding;
                henvendelse.kanal = meldingTilBruker.getKanal();
            }
        }
        return henvendelse;
    }

    private static String hentTemagruppeNavn(String temagruppeNavn) {
        try {
            return tekstService.hentTekst(temagruppeNavn);
        } catch (MissingResourceException exception) {
            logger.error("Finner ikke cms-oppslag for " + temagruppeNavn, exception);
            return temagruppeNavn;
        }
    }

    private static boolean innholdErKassert(XMLHenvendelse info) {
        return info.getMetadataListe() == null;
    }

    public static String cleanOutHtml(String text) {
        String clean = Jsoup.clean(text.replaceAll(LINE_BREAK, LINE_REPLACEMENT_STRING), Whitelist.none());
        return StringEscapeUtils.unescapeHtml4(clean).replaceAll(LINE_REPLACEMENT_STRING, LINE_BREAK);
    }

    private static String statusTekst(Henvendelse henvendelse) {
        String type = hentTemagruppeNavn(String.format("status.%s", henvendelse.type.name()));
        String temagruppe = hentTemagruppeNavn(henvendelse.temagruppe.name());
        return String.format(type, temagruppe);

    }

}
