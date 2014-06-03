package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.apache.commons.collections15.Transformer;

import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SAMTALEREFERAT;
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
            henvendelse.fodselsnummer = info.getAktor().getFodselsnummer();
            henvendelse.opprettet = info.getOpprettetDato();
            henvendelse.avsluttet = info.getAvsluttetDato();

            if (metadata instanceof XMLSporsmal) {
                XMLSporsmal sporsmal = (XMLSporsmal) metadata;
                henvendelse.type = SPORSMAL;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.tema = Tema.valueOf(sporsmal.getTemagruppe());
                henvendelse.fritekst = sporsmal.getFritekst();
                henvendelse.markerSomLest();
                return henvendelse;
            } else if (metadata instanceof XMLSvar) {
                XMLSvar svar = (XMLSvar) metadata;
                henvendelse.type = SVAR;
                henvendelse.traadId = svar.getSporsmalsId();
                henvendelse.tema = Tema.valueOf(svar.getTemagruppe());
                henvendelse.markerSomLest(svar.getLestDato());
                henvendelse.fritekst = svar.getFritekst();
                return henvendelse;
            } else if (metadata instanceof XMLReferat) {
                XMLReferat referat = (XMLReferat) metadata;
                henvendelse.type = SAMTALEREFERAT;
                henvendelse.traadId = info.getBehandlingsId();
                henvendelse.tema = Tema.valueOf(referat.getTemagruppe());
                henvendelse.markerSomLest(referat.getLestDato());
                henvendelse.fritekst = referat.getFritekst();
                henvendelse.kanal = referat.getKanal();
                return henvendelse;
            } else {
                throw new RuntimeException("Behandlingsinformasjon sin XMLMetadata er ikke av typen Spørsmål, Svar eller Referat. Ukjent type: " + metadata);
            }
        }
    };

    public static XMLBehandlingsinformasjonV2 tilXMLBehandlingsinformasjonV2(Henvendelse henvendelse){
        XMLBehandlingsinformasjonV2 info = new XMLBehandlingsinformasjonV2()
                .withBehandlingsId(henvendelse.id)
                .withAktor(new XMLAktor().withFodselsnummer(henvendelse.fodselsnummer))
                .withOpprettetDato(henvendelse.opprettet)
                .withAvsluttetDato(henvendelse.avsluttet);

        if (henvendelse.type.equals(Henvendelsetype.SVAR)) {
            return info
                    .withHenvendelseType(XMLHenvendelseType.SVAR.name())
                    .withMetadataListe(new XMLMetadataListe().withMetadata(
                            new XMLSvar()
                                    .withSporsmalsId(henvendelse.traadId)
                                    .withTemagruppe(henvendelse.tema.name())
                                    .withFritekst(henvendelse.fritekst)
                                    .withLestDato(henvendelse.getLestDato())));
        } else if (henvendelse.type.equals(Henvendelsetype.SAMTALEREFERAT)){
            return info
                    .withHenvendelseType(XMLHenvendelseType.REFERAT.name())
                    .withMetadataListe(new XMLMetadataListe().withMetadata(
                            new XMLReferat()
                                    .withTemagruppe(henvendelse.tema.name())
                                    .withKanal(henvendelse.kanal)
                                    .withFritekst(henvendelse.fritekst)
                                    .withLestDato(henvendelse.getLestDato())));
        } else {
            throw new RuntimeException("Henvendelse som skal settes som lest er ikke av typen Svar eller Referat. Ukjent type: " + henvendelse.type.name());
        }
    }

}
