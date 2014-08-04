package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
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

            if (metadata instanceof XMLSporsmal) {
                XMLSporsmal sporsmal = (XMLSporsmal) metadata;
                henvendelse.type = SPORSMAL;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.temagruppe = Temagruppe.valueOf(sporsmal.getTemagruppe());
                henvendelse.fritekst = sporsmal.getFritekst();
                henvendelse.markerSomLest();
                return henvendelse;
            } else if (metadata instanceof XMLSvar) {
                XMLSvar svar = (XMLSvar) metadata;
                henvendelse.type = SVAR;
                henvendelse.traadId = svar.getSporsmalsId();
                henvendelse.temagruppe = Temagruppe.valueOf(svar.getTemagruppe());
                henvendelse.markerSomLest(svar.getLestDato());
                henvendelse.fritekst = svar.getFritekst();
                henvendelse.kanal = svar.getKanal();
                return henvendelse;
            } else if (metadata instanceof XMLReferat) {
                XMLReferat referat = (XMLReferat) metadata;
                henvendelse.type = SAMTALEREFERAT;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.temagruppe = Temagruppe.valueOf(referat.getTemagruppe());
                henvendelse.markerSomLest(referat.getLestDato());
                henvendelse.fritekst = referat.getFritekst();
                henvendelse.kanal = referat.getKanal();
                return henvendelse;
            } else {
                throw new RuntimeException("Behandlingsinformasjon sin XMLMetadata er ikke av typen Spørsmål, Svar eller Referat. Ukjent type: " + metadata);
            }
        }
    };

}
