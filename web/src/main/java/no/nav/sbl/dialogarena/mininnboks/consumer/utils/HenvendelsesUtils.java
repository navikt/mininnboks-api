package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.CmsContentRetriever;
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

    private static CmsContentRetriever cmsContentRetriever;

    final static Logger logger = LoggerFactory.getLogger(HenvendelsesUtils.class);
    private static final String LINE_REPLACEMENT_STRING = UUID.randomUUID().toString();
    private static final String LINE_BREAK = "\n";

    public static final List<Henvendelsetype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final List<Henvendelsetype> FRA_NAV = asList(SPORSMAL_MODIA_UTGAAENDE, SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON, DOKUMENT_VARSEL);


    public static void setCmsContentRetriever(CmsContentRetriever contentRetriever) {
        cmsContentRetriever = contentRetriever;
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

    public static Function<Object, Henvendelse> tilHenvendelse() {
        return wsMelding -> {
            XMLHenvendelse info = (XMLHenvendelse) wsMelding;

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
                henvendelse.fritekst = cmsContentRetriever.hentTekst("innhold.kassert");
                henvendelse.statusTekst = cmsContentRetriever.hentTekst("temagruppe.kassert");
                henvendelse.temagruppe = null;
                henvendelse.kanal = null;
                return henvendelse;
            }

            if( DOKUMENT_VARSEL.name().equals(info.getHenvendelseType())){
                XMLDokumentVarsel varsel = (XMLDokumentVarsel) info.getMetadataListe().getMetadata().get(0);
                henvendelse.statusTekst = varsel.getDokumenttittel();
                henvendelse.withTemaNavn(varsel.getTemanavn());
                henvendelse.temagruppe = null;
            } else{
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
        };
    }

    private static String hentTemagruppeNavn(String temagruppeNavn) {
        try {
            return cmsContentRetriever.hentTekst(temagruppeNavn);
        } catch(MissingResourceException exception) {
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
