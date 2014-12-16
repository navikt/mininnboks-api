package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import org.apache.commons.collections15.Transformer;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.fromValue;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.*;

public class HenvendelsesUtils {

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

    public static final Transformer<Object, Henvendelse> TIL_HENVENDELSE = new Transformer<Object, Henvendelse>() {
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
            if (FRA_BRUKER.contains(henvendelse.type)) {
                henvendelse.markerSomLest();
            } else {
                henvendelse.markerSomLest(info.getLestDato());
            }

            if (innholdErKassert(info)) {
                henvendelse.temagruppe = null;
                henvendelse.fritekst = null;
                henvendelse.kanal = null;
                return henvendelse;
            }

            XMLMelding xmlMelding = (XMLMelding) info.getMetadataListe().getMetadata().get(0);
            henvendelse.temagruppe = Temagruppe.valueOf(xmlMelding.getTemagruppe());
            henvendelse.fritekst = xmlMelding.getFritekst();

            if (xmlMelding instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMelding;
                henvendelse.kanal = meldingTilBruker.getKanal();
            }
            return henvendelse;
        }
    };

    private static boolean innholdErKassert(XMLHenvendelse info) {
        return info.getMetadataListe() == null;
    }

}
