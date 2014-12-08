package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMelding;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.fromValue;

public class HenvendelsesUtils {

    public static final List<Henvendelsetype> SVAR = asList(Henvendelsetype.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_OPPMOTE, Henvendelsetype.SVAR_TELEFON);
    public static final List<Henvendelsetype> SAMTALEREFERAT = asList(Henvendelsetype.SAMTALEREFERAT_OPPMOTE, Henvendelsetype.SAMTALEREFERAT_TELEFON);

    public static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, Henvendelsetype.SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, Henvendelsetype.SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, Henvendelsetype.SVAR_TELEFON);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, Henvendelsetype.SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, Henvendelsetype.SAMTALEREFERAT_TELEFON);
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
            henvendelse.type = HENVENDELSETYPE_MAP.get(fromValue(info.getHenvendelseType()));
            if (henvendelse.type.equals(Henvendelsetype.SPORSMAL_SKRIFTLIG)) {
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
                XMLMeldingTilBruker svarEllerReferat = (XMLMeldingTilBruker) xmlMelding;
                henvendelse.kanal = svarEllerReferat.getKanal();
            }
            return henvendelse;
        }
    };

    private static boolean innholdErKassert(XMLHenvendelse info) {
        return info.getMetadataListe() == null;
    }

}
