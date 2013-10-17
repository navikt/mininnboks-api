package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.Adresse;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.StrukturertAdresse;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.adresse.UstrukturertAdresse;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.common.TekstUtils;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.kontaktdetaljer.Preferanser;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.konto.UtenlandskKonto;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.telefonnummer.Telefonnummer;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.meldinger.XMLHentKontaktinformasjonOgPreferanserResponse;

import java.io.Serializable;
import java.util.Objects;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.GjeldendeAdressetype.FOLKEREGISTRERT;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.GjeldendeAdressetype.MIDLERTIDIG_NORGE;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.GjeldendeAdressetype.UKJENT;

public class Person implements Serializable {

    public String ident;
    public String navn;
    public Adresse folkeregistrertAdresse;

    private String kontonummer;
    private Telefonnummer hjemmetelefon;
    private Telefonnummer mobiltelefon;
    private Telefonnummer jobbtelefon;
    private String epost;
    private StrukturertAdresse norskMidlertidig;
    private UstrukturertAdresse utenlandskMidlertidig;
    private GjeldendeAdressetype gjeldendeAdressetype;
    private UtenlandskKonto bankkontoUtland;

    private Preferanser preferanser = new Preferanser();
    private XMLHentKontaktinformasjonOgPreferanserResponse response;
    private XMLHentKontaktinformasjonOgPreferanserResponse responseFraTPS;

    public Person() {
    }

    public Person(String navn, String ident, Optional<? extends Adresse> folkeregistrertAdresse) {
        this.navn = navn;
        this.ident = ident;
        this.gjeldendeAdressetype = folkeregistrertAdresse.isSome() ? FOLKEREGISTRERT : UKJENT;
        this.folkeregistrertAdresse = folkeregistrertAdresse.getOrElse(null);
    }

    public Person lagNy() {
        return new Person(navn, ident, optional(folkeregistrertAdresse));
    }

    public boolean harIdent(String forventetIdent) {
        return Objects.equals(this.ident, forventetIdent);
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public Telefonnummer getHjemmetelefon() {
        return hjemmetelefon;
    }

    public Telefonnummer getMobiltelefon() {
        return mobiltelefon;
    }

    public Telefonnummer getJobbtelefon() {
        return jobbtelefon;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public Preferanser getPreferanser() {
        return preferanser;
    }

    public void setPreferanser(Preferanser preferanser) {
        this.preferanser = preferanser;
    }

    public void setKontonummer(String kontonummer) {
        this.kontonummer = TekstUtils.fjernSpesialtegn(kontonummer);
    }

    public Adresse getValgtMidlertidigAdresse() {
        return gjeldendeAdressetype == MIDLERTIDIG_NORGE ? norskMidlertidig : utenlandskMidlertidig;
    }

    public StrukturertAdresse getNorskMidlertidig() {
        return norskMidlertidig;
    }

    public UstrukturertAdresse getUtenlandskMidlertidig() {
        return utenlandskMidlertidig;
    }

    public void setNorskMidlertidig(StrukturertAdresse midlertidig) {
        norskMidlertidig = midlertidig;
    }

    public void setUtenlandskMidlertidig(UstrukturertAdresse midlertidig) {
        utenlandskMidlertidig = midlertidig;
    }

    public GjeldendeAdressetype getGjeldendeAdressetype() {
        return gjeldendeAdressetype;
    }

    public void velg(GjeldendeAdressetype type) {
        if (type == FOLKEREGISTRERT && folkeregistrertAdresse == null) {
            throw new KanIkkeVelgeAdresse(ident, type);
        }
        this.gjeldendeAdressetype = type;
    }

    public UtenlandskKonto getBankkontoUtland() {
        return bankkontoUtland;
    }

    public void setBankkontoUtland(UtenlandskKonto bankkontoUtland) {
        this.bankkontoUtland = bankkontoUtland;
    }

    public ValgtKontotype getValgtKontotype() {
        return this.bankkontoUtland != null ? ValgtKontotype.UTLAND : ValgtKontotype.NORGE;
    }

    public boolean har(GjeldendeAdressetype type) {
        return type == gjeldendeAdressetype;
    }

    public boolean har(ValgtKontotype kontotype) {
        return getValgtKontotype() == kontotype;
    }

    public void setHjemmetelefonnummer(Telefonnummer telefonnummer) {
        this.hjemmetelefon = telefonnummer;
    }

    public void setMobilnummer(Telefonnummer mobilnummer) {
        mobiltelefon = mobilnummer;
    }

    public void setJobbtelefonnummer(Telefonnummer ekstraTelefonnummer) {
        this.jobbtelefon = ekstraTelefonnummer;
    }

    public void setResponseFraTPS(XMLHentKontaktinformasjonOgPreferanserResponse responseFraTPS) {
        this.responseFraTPS = responseFraTPS;
    }

    public XMLHentKontaktinformasjonOgPreferanserResponse getResponseFraTPS() {
        return responseFraTPS;
    }

    public static class KanIkkeVelgeAdresse extends ApplicationException {
        public KanIkkeVelgeAdresse(String ident, GjeldendeAdressetype type) {
            super("Type: " + type + ", ident: " + ident);
        }
    }

}
