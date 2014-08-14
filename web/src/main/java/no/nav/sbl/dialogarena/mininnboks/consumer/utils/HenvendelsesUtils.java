package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.apache.commons.collections15.Transformer;

import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;

public class HenvendelsesUtils {

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
                henvendelse.type = SPORSMAL;
                henvendelse.temagruppe = Temagruppe.valueOf(sporsmal.getTemagruppe());
                henvendelse.fritekst = sporsmal.getFritekst();
                henvendelse.markerSomLest();
                return henvendelse;
            } else if (metadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker svarEllerReferat = (XMLMeldingTilBruker) metadata;
                XMLHenvendelseType henvendelseType = XMLHenvendelseType.fromValue(info.getHenvendelseType());
                if (henvendelseType.equals(XMLHenvendelseType.SVAR)) {
                    henvendelse.type = SVAR;
                } else if (henvendelseType.equals(XMLHenvendelseType.REFERAT)) {
                    henvendelse.type = SAMTALEREFERAT;
                }
                if(svarEllerReferat.getSporsmalsId() != null){
                    henvendelse.traadId = svarEllerReferat.getSporsmalsId();
                }
                henvendelse.temagruppe = Temagruppe.valueOf(svarEllerReferat.getTemagruppe());
                henvendelse.markerSomLest(svarEllerReferat.getLestDato());
                henvendelse.fritekst = svarEllerReferat.getFritekst();
                henvendelse.kanal = svarEllerReferat.getKanal();
                return henvendelse;
            } else {
                throw new RuntimeException("Behandlingsinformasjon sin XMLMetadata er ikke av typen XMLMeldingFraBruker eller XMLMeldingTilBruker. Ukjent type: " + metadata);
            }
        }
    };

}
