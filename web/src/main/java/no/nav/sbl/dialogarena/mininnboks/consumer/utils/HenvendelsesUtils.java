package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.apache.commons.collections15.Transformer;

import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.REFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;

public class HenvendelsesUtils {

    public static final Transformer<Object, Henvendelse> TIL_HENVENDELSE = new Transformer<Object, Henvendelse>() {

        @Override
        public Henvendelse transform(Object wsMelding) {
            XMLBehandlingsinformasjonV2 info = (XMLBehandlingsinformasjonV2) wsMelding;
            XMLMetadataListe metadataListe = info.getMetadataListe();
            XMLMetadata metadata = metadataListe.getMetadata().get(0);
            Henvendelse henvendelse = new Henvendelse(info.getBehandlingsId());
            henvendelse.opprettet = info.getOpprettetDato();
            if (metadata instanceof XMLSporsmal) {
                XMLSporsmal sporsmal = (XMLSporsmal) metadata;
                henvendelse.type = SPORSMAL;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.tema = Tema.valueOf(sporsmal.getTemagruppe());
                henvendelse.fritekst = sporsmal.getFritekst();
                return henvendelse;
            } else if (metadata instanceof XMLSvar) {
                XMLSvar svar = (XMLSvar) metadata;
                henvendelse.type = SVAR;
                henvendelse.traadId = svar.getSporsmalsId();
                henvendelse.tema = Tema.valueOf(svar.getTemagruppe());
                henvendelse.setLest(svar.getLestDato() != null);
                henvendelse.lestDato = svar.getLestDato();
                henvendelse.fritekst = svar.getFritekst();
                return henvendelse;
            } else if (metadata instanceof XMLReferat) {
                XMLReferat referat = (XMLReferat) metadata;
                henvendelse.type = REFERAT;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.tema = Tema.valueOf(referat.getTemagruppe());
                henvendelse.setLest(referat.getLestDato() != null);
                henvendelse.lestDato = referat.getLestDato();
                henvendelse.fritekst = referat.getFritekst();
                henvendelse.kanal = referat.getKanal();
                return henvendelse;
            } else {
                throw new RuntimeException("Behandlingsinformasjon sin XMLMetadata er ikke av typen Spørsmål, Svar eller Referat");
            }
        }
    };
}
