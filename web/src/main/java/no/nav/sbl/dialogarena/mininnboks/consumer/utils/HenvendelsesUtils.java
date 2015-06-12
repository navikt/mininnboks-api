package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMelding;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.modig.content.PropertyResolver;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.fromValue;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;

public abstract class HenvendelsesUtils {

    private static final String LINE_REPLACEMENT_STRING = UUID.randomUUID().toString();
    private static final String LINE_BREAK = "\n";

    public static final List<Henvendelsetype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final List<Henvendelsetype> FRA_NAV = asList(SPORSMAL_MODIA_UTGAAENDE, SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON);

    public static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
        }
    };

    public static Transformer<Object, Henvendelse> tilHenvendelse(final PropertyResolver propertyResolver) {
        return new Transformer<Object, Henvendelse>() {
            @Override
            public Henvendelse transform(Object wsMelding) {
                XMLHenvendelse info = (XMLHenvendelse) wsMelding;

                Henvendelse henvendelse = new Henvendelse(info.getBehandlingsId());
                henvendelse.opprettet = info.getOpprettetDato();
                henvendelse.avsluttet = info.getAvsluttetDato();
                henvendelse.traadId = info.getBehandlingskjedeId();
                henvendelse.eksternAktor = info.getEksternAktor();
                henvendelse.tilknyttetEnhet = info.getTilknyttetEnhet();
                henvendelse.type = HENVENDELSETYPE_MAP.get(fromValue(info.getHenvendelseType()));
                henvendelse.fraBruker = FRA_BRUKER.contains(henvendelse.type);
                henvendelse.fraNav = !henvendelse.fraBruker;
                if (FRA_BRUKER.contains(henvendelse.type)) {
                    henvendelse.markerSomLest();
                } else {
                    henvendelse.markerSomLest(info.getLestDato());
                }

                if (innholdErKassert(info)) {
                    henvendelse.kassert = true;
                    henvendelse.fritekst = propertyResolver.getProperty("innhold.kassert");
                    henvendelse.statusTekst = propertyResolver.getProperty("temagruppe.kassert");
                    henvendelse.temagruppe = null;
                    henvendelse.kanal = null;
                    return henvendelse;
                }

                XMLMelding xmlMelding = (XMLMelding) info.getMetadataListe().getMetadata().get(0);
                henvendelse.temagruppe = Temagruppe.valueOf(xmlMelding.getTemagruppe());
                henvendelse.temagruppeNavn = propertyResolver.getProperty(henvendelse.temagruppe.name());
                henvendelse.statusTekst = statusTekst(henvendelse, propertyResolver);
                henvendelse.fritekst = cleanOutHtml(xmlMelding.getFritekst());

                if (xmlMelding instanceof XMLMeldingTilBruker) {
                    XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMelding;
                    henvendelse.kanal = meldingTilBruker.getKanal();
                }
                return henvendelse;
            }
        };
    }

    private static boolean innholdErKassert(XMLHenvendelse info) {
        return info.getMetadataListe() == null;
    }

    public static String cleanOutHtml(String text) {
        String clean = Jsoup.clean(text.replaceAll(LINE_BREAK, LINE_REPLACEMENT_STRING), Whitelist.none());
        return StringEscapeUtils.unescapeHtml4(clean).replaceAll(LINE_REPLACEMENT_STRING, LINE_BREAK);
    }

    private static String statusTekst(Henvendelse henvendelse, PropertyResolver resolver) {
        String type = resolver.getProperty(String.format("status.%s", henvendelse.type.name()));
        String temagruppe = resolver.getProperty(henvendelse.temagruppe.name());
        return String.format(type, temagruppe);

    }

}
