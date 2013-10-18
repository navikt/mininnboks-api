package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.telefonnummer.Telefonnummer;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.telefonnummer.Telefonnummertype;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform.XMLPersonidenterInToXMLPersonidenterOut;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.transform.XMLPostadresseTyperInToXMLPostadresseTyperOut;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLBruker;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLNorskIdent;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLPreferanser;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLRetningsnumre;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLSpraak;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLTelefonnummer;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.meldinger.XMLOppdaterKontaktinformasjonOgPreferanserRequest;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.transform.Transform.toXMLTelefontype;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class OppdaterBrukerprofilConsumer {

    private final BehandleBrukerprofilPortType behandleBrukerprofilService;

    public OppdaterBrukerprofilConsumer(BehandleBrukerprofilPortType behandleBrukerprofilPortType) {
        this.behandleBrukerprofilService = behandleBrukerprofilPortType;
    }

    public void oppdaterPerson(Person person) {
        XMLNorskIdent ident = new XMLNorskIdent()
                .withIdent(person.getPersonFraTPS().getIdent().getIdent())
                .withType(new XMLPersonidenterInToXMLPersonidenterOut().transform(person.getPersonFraTPS().getIdent().getType()));

        XMLBruker xmlBruker = new XMLBruker().withIdent(ident);
        no.nav.tjeneste.virksomhet.brukerprofil.v1.informasjon.XMLBruker xmlBrukerFraTPS = person.getPersonFraTPS();
        xmlBruker.withGjeldendePostadresseType(new XMLPostadresseTyperInToXMLPostadresseTyperOut().transform(xmlBrukerFraTPS.getGjeldendePostadresseType()));

        populatePreferanser(person, xmlBruker);
        // populateMidlertidigAdresse(person, xmlBruker);
        // xmlBruker.withMidlertidigPostadresse((no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.informasjon.XMLMidlertidigPostadresse) xmlBrukerFraTPS.getMidlertidigPostadresse());

        //  List<XMLElektroniskKommunikasjonskanal> kanaler = xmlBrukerFraTPS.getElektroniskKommunikasjonskanal();

        //    for (XMLElektroniskKommunikasjonskanal kanal : kanaler) {
        //        //xmlBruker.withElektroniskKommunikasjonskanal(toxml)

        //  }


//        xmlBruker.withElektroniskKommunikasjonskanal(several(
//                telefonnummerKanal(Telefonnummertype.HJEMMETELEFON, person.getHjemmetelefon()).map(toXMLElektroniskKommunkasjonskanal()),
//                telefonnummerKanal(Telefonnummertype.JOBBTELEFON, person.getJobbtelefon()).map(toXMLElektroniskKommunkasjonskanal()),
//                telefonnummerKanal(Telefonnummertype.MOBIL, person.getMobiltelefon()).map(toXMLElektroniskKommunkasjonskanal())
//        ).collect());

//        populateBankkonto(person, xmlBruker);

        try {
            behandleBrukerprofilService.oppdaterKontaktinformasjonOgPreferanser(new XMLOppdaterKontaktinformasjonOgPreferanserRequest().withPerson(xmlBruker));
        } catch (OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            throw new SystemException(e.getMessage(), e);
        } catch (OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            throw new ApplicationException(e.getMessage(), e);
        } catch (OppdaterKontaktinformasjonOgPreferanserUgyldigInput e) {
            switch (TpsValideringsfeil.fra(e)) {
                case MIDLERTIDIG_ADRESSE_LIK_FOLKEREGISTRERT:
                    throw new TpsValideringException(TpsValideringsfeil.MIDLERTIDIG_ADRESSE_LIK_FOLKEREGISTRERT, e);
                case UGYLDIG_POSTNUMMER:
                    throw new TpsValideringException(TpsValideringsfeil.UGYLDIG_POSTNUMMER, e);
                default:
                    throw new ApplicationException(
                            "Feil ved oppdatering av adresse for bruker '" + person.getPersonFraTPS().getIdent().getIdent() + "'.\n" +
                                    e.getMessage() + "\n" +
                                    "Feilmelding: " + e.getFaultInfo().getFeilmelding() + "\n" +
                                    "Årsak: " + e.getFaultInfo().getFeilaarsak() + "\n" +
                                    "Feilkilde: " + e.getFaultInfo().getFeilkilde(),
                            e);
            }
        }
    }

    private void populatePreferanser(Person person, XMLBruker xmlBruker) {
        xmlBruker.withPreferanser(new XMLPreferanser()
                .withMaalform(populateMaalform(person))
                .withElektroniskKorrespondanse(person.getPreferanser().isElektroniskSamtykke())
        );
    }

    private XMLSpraak populateMaalform(Person person) {
        return new XMLSpraak().withValue(person.getPreferanser().getMaalform().getValue());
    }

   /* private void populateMidlertidigAdresse(Person person, XMLBruker xmlBruker) {
        if (person.getPersonFraTPS().getGjeldendePostadresseType().getValue()har(GjeldendeAdressetype.MIDLERTIDIG_NORGE)) {
            DateTime utlopsdato = person.getNorskMidlertidig().getUtlopstidspunkt();
            Optional<StrukturertAdresse> midlertidigAdresse = optional(person.getNorskMidlertidig());
            Transformer<StrukturertAdresse, XMLMidlertidigPostadresseNorge> toXMLMidlertidigPostadresseNorge;
            switch (person.getValgtMidlertidigAdresse().getType()) {
                case POSTBOKSADRESSE:
                    toXMLMidlertidigPostadresseNorge = toXMLMidlertidigPostboksadresse(utlopsdato);
                    break;
                case GATEADRESSE:
                    toXMLMidlertidigPostadresseNorge = toXMLMidlertidigGateadresse(utlopsdato, midlertidigAdresse.map(EIER));
                    break;
                case OMRAADEADRESSE:
                    toXMLMidlertidigPostadresseNorge = ((StrukturertAdresse) person.getValgtMidlertidigAdresse()).getOmraadeadresse() != null ?
                            toXMLMatrikkeladresse(utlopsdato, midlertidigAdresse.map(EIER)) :
                            toXMLStedsadresseNorge(utlopsdato, midlertidigAdresse.map(EIER));
                    break;
                default:
                    throw new UnableToHandleException(person.getValgtMidlertidigAdresse().getType());
            }
            xmlBruker.withGjeldendePostadresseType(MIDLERTIDIG_POSTADRESSE_NORGE.forSkrivtjeneste)
                    .withMidlertidigPostadresse(midlertidigAdresse.map(toXMLMidlertidigPostadresseNorge).getOrElse(null));
        } else if (person.har(GjeldendeAdressetype.MIDLERTIDIG_UTLAND)) {
            xmlBruker.withGjeldendePostadresseType(MIDLERTIDIG_POSTADRESSE_UTLAND.forSkrivtjeneste)
                    .withMidlertidigPostadresse(optional(person.getUtenlandskMidlertidig())
                            .map(toXMLMidlertidigPostadresseUtland(person.getUtenlandskMidlertidig().getUtlopstidspunkt())).getOrElse(null));
        }
    }*/

    private void populateBankkonto(Person person, XMLBruker xmlBruker) {
//        Optional<? extends XMLBankkonto> valgtBankkonto;
//        if (person.har(ValgtKontotype.NORGE)) {
//            valgtBankkonto = optional(person.getKontonummer()).map(toXMLBankkontoNorge());
//        } else if (person.har(ValgtKontotype.UTLAND)) {
//            valgtBankkonto = optional(person.getBankkontoUtland()).map(toXMLBankkontonummerUtland()).map(toXMLBankkontoUtland());
//        } else {
//            valgtBankkonto = none();
//        }
//
//        for (XMLBankkonto bankkonto : valgtBankkonto) {
//            xmlBruker.withBankkonto(bankkonto);
//        }
    }

    private Optional<XMLTelefonnummer> telefonnummerKanal(Telefonnummertype type, Telefonnummer nr) {
        if (nr != null && isNotBlank(nr.getLandkode()) && isNotBlank(nr.getNummer()) && isNotBlank(type.name())) {
            return optional(new XMLTelefonnummer()
                    .withType(toXMLTelefontype().transform(type))
                    .withIdentifikator(nr.getNummer())
                    .withRetningsnummer(new XMLRetningsnumre().withValue(nr.getLandkode())));
        } else {
            return optional(null);
        }
    }

}
