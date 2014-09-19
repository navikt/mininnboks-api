package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

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
            XMLMetadataListe metadataListe = info.getMetadataListe();
            XMLMetadata metadata = metadataListe.getMetadata().get(0);

            Henvendelse henvendelse = new Henvendelse(info.getBehandlingsId());
            henvendelse.opprettet = info.getOpprettetDato();
            henvendelse.avsluttet = info.getAvsluttetDato();
            henvendelse.traadId = info.getBehandlingsId();

            if (metadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker sporsmal = (XMLMeldingFraBruker) metadata;
                henvendelse.type = Henvendelsetype.SPORSMAL_SKRIFTLIG;
                henvendelse.temagruppe = Temagruppe.valueOf(sporsmal.getTemagruppe());
                henvendelse.fritekst = sporsmal.getFritekst();
                henvendelse.markerSomLest();
                return henvendelse;
            } else if (metadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker svarEllerReferat = (XMLMeldingTilBruker) metadata;
                XMLHenvendelseType henvendelseType = XMLHenvendelseType.fromValue(info.getHenvendelseType());
                henvendelse.type = HENVENDELSETYPE_MAP.get(henvendelseType);
                if(svarEllerReferat.getSporsmalsId() != null){
                    henvendelse.traadId = svarEllerReferat.getSporsmalsId();
                }
                henvendelse.temagruppe = Temagruppe.valueOf(svarEllerReferat.getTemagruppe());
                henvendelse.markerSomLest(info.getLestDato());
                henvendelse.fritekst = svarEllerReferat.getFritekst();
                henvendelse.kanal = svarEllerReferat.getKanal();
                return henvendelse;
            } else {
                throw new RuntimeException("Behandlingsinformasjon sin XMLMetadata er ikke av typen XMLMeldingFraBruker eller XMLMeldingTilBruker. Ukjent type: " + metadata);
            }
        }
    };

}
