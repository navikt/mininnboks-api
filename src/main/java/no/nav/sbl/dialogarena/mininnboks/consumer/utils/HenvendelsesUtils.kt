package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import org.apache.commons.lang3.StringEscapeUtils
import org.joda.time.DateTime
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.slf4j.LoggerFactory
import java.util.*

object HenvendelsesUtils {
    private var tekstService: TekstService? = null
    val logger = LoggerFactory.getLogger(HenvendelsesUtils::class.java)
    private val LINE_REPLACEMENT_STRING = UUID.randomUUID().toString()
    private val LINE_BREAK = "\n"
    val FRA_BRUKER = Arrays.asList(Henvendelsetype.SPORSMAL_SKRIFTLIG, Henvendelsetype.SPORSMAL_SKRIFTLIG_DIREKTE, Henvendelsetype.SVAR_SBL_INNGAAENDE)

    private val HENVENDELSETYPE_MAP: HashMap<XMLHenvendelseType, Henvendelsetype> = object : HashMap<XMLHenvendelseType, Henvendelsetype>() {
        init {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, Henvendelsetype.SPORSMAL_SKRIFTLIG)
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE, Henvendelsetype.SPORSMAL_SKRIFTLIG_DIREKTE)
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE)
            put(XMLHenvendelseType.INFOMELDING_MODIA_UTGAAENDE, Henvendelsetype.INFOMELDING_MODIA_UTGAAENDE)
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_SKRIFTLIG)
            put(XMLHenvendelseType.SVAR_OPPMOTE, Henvendelsetype.SVAR_OPPMOTE)
            put(XMLHenvendelseType.SVAR_TELEFON, Henvendelsetype.SVAR_TELEFON)
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, Henvendelsetype.SVAR_SBL_INNGAAENDE)
            put(XMLHenvendelseType.REFERAT_OPPMOTE, Henvendelsetype.SAMTALEREFERAT_OPPMOTE)
            put(XMLHenvendelseType.REFERAT_TELEFON, Henvendelsetype.SAMTALEREFERAT_TELEFON)
            put(XMLHenvendelseType.DOKUMENT_VARSEL, Henvendelsetype.DOKUMENT_VARSEL)
            put(XMLHenvendelseType.OPPGAVE_VARSEL, Henvendelsetype.OPPGAVE_VARSEL)
            put(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, Henvendelsetype.DELVIS_SVAR_SKRIFTLIG)
        }
    }
    private val domainMapper = DomainMapper<XMLHenvendelse, Henvendelse>()
    private val DEFAULT_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse -> true },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                henvendelse.opprettet = utilDate(xmlHenvendelse.opprettetDato)
                henvendelse.avsluttet = utilDate(xmlHenvendelse.avsluttetDato)
                henvendelse.ferdigstiltUtenSvar = xmlHenvendelse.isFerdigstiltUtenSvar
                henvendelse.traadId = Optional.ofNullable(xmlHenvendelse.behandlingskjedeId).orElse(xmlHenvendelse.behandlingsId)
                henvendelse.eksternAktor = xmlHenvendelse.eksternAktor
                henvendelse.tilknyttetEnhet = xmlHenvendelse.tilknyttetEnhet
                henvendelse.kontorsperreEnhet = xmlHenvendelse.kontorsperreEnhet
                henvendelse.erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt
                henvendelse.type = HENVENDELSETYPE_MAP[XMLHenvendelseType.fromValue(xmlHenvendelse.henvendelseType)]
                henvendelse.brukersEnhet = xmlHenvendelse.brukersEnhet
                henvendelse.fraBruker = FRA_BRUKER.contains(henvendelse.type)
                henvendelse.fraNav = !henvendelse.fraBruker!!
                henvendelse.temaKode = xmlHenvendelse.tema
                henvendelse.korrelasjonsId = xmlHenvendelse.korrelasjonsId
                henvendelse
            }
    )
    private val LEST_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse -> true },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                if (FRA_BRUKER.contains(henvendelse.type)) {
                    henvendelse.markerSomLest()
                } else {
                    henvendelse.markerSomLest(utilDate(xmlHenvendelse.lestDato))
                }
                henvendelse
            }
    )
    private val KASSERT_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse -> xmlHenvendelse.metadataListe == null },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                henvendelse.kassert = true
                henvendelse.fritekst = tekstService!!.hentTekst("innhold.kassert")
                henvendelse.statusTekst = tekstService!!.hentTekst("temagruppe.kassert")
                henvendelse.temagruppe = null
                henvendelse.kanal = null
                henvendelse
            },
            true
    )
    private val DOKUMENTVARSEL_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse -> Henvendelsetype.DOKUMENT_VARSEL.name == xmlHenvendelse.henvendelseType },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val varsel = xmlHenvendelse.metadataListe.metadata[0] as XMLDokumentVarsel
                henvendelse.statusTekst = varsel.dokumenttittel
                henvendelse.withTemaNavn(varsel.temanavn)
                henvendelse.temagruppe = null
                henvendelse.fritekst = Optional.ofNullable(varsel.fritekst).orElse("")
                henvendelse.opprettet = utilDate(varsel.ferdigstiltDato)
                henvendelse.dokumentIdListe.addAll(varsel.dokumentIdListe)
                henvendelse.journalpostId = varsel.journalpostId
                henvendelse
            }
    )
    private val OPPGAVEVARSEL_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse -> Henvendelsetype.OPPGAVE_VARSEL.name == xmlHenvendelse.henvendelseType },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val varsel = xmlHenvendelse.metadataListe.metadata[0] as XMLOppgaveVarsel
                henvendelse.oppgaveType = varsel.oppgaveType
                henvendelse.oppgaveUrl = varsel.oppgaveURL
                henvendelse.statusTekst = hentTekst(tekstService, String.format("oppgave.%s", varsel.oppgaveType), "oppgave.GEN")
                henvendelse.fritekst = hentTekst(tekstService, String.format("oppgave.%s.fritekst", varsel.oppgaveType), "oppgave.GEN.fritekst")
                henvendelse
            }
    )
    private val IKKEVARSEL_MAPPER = DomainMapper.Mapper(
             { xmlHenvendelse: XMLHenvendelse ->
                Henvendelsetype.OPPGAVE_VARSEL.name != xmlHenvendelse.henvendelseType &&
                        Henvendelsetype.DOKUMENT_VARSEL.name != xmlHenvendelse.henvendelseType
            },
             { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val xmlMelding = xmlHenvendelse.metadataListe.metadata[0] as XMLMelding
                henvendelse.temagruppe = Temagruppe.valueOf(xmlMelding.temagruppe)
                henvendelse.temagruppeNavn = hentTemagruppeNavn(henvendelse.temagruppe!!.name)
                henvendelse.statusTekst = statusTekst(henvendelse)
                henvendelse.fritekst = cleanOutHtml(xmlMelding.fritekst)
                if (xmlMelding is XMLMeldingTilBruker) {
                    henvendelse.kanal = xmlMelding.kanal
                }
                henvendelse
            }
    )

    fun tilHenvendelse(wsMelding: XMLHenvendelse): Henvendelse {
        return wsMelding.let { domainMapper.apply(it, Henvendelse(wsMelding.behandlingsId)) }
    }

    fun hentTekst(tekster: TekstService?, key: String?, defaultKey: String?): String? {
        return try {
            tekster!!.hentTekst(key)
        } catch (e: Exception) {
            tekster!!.hentTekst(defaultKey)
        }
    }

    private fun hentTemagruppeNavn(temagruppeNavn: String): String? {
        return try {
            tekstService!!.hentTekst(temagruppeNavn)
        } catch (exception: MissingResourceException) {
            logger.error("Finner ikke cms-oppslag for $temagruppeNavn", exception)
            temagruppeNavn
        }
    }

    fun cleanOutHtml(text: String): String {
        val clean = Jsoup.clean(text.replace(LINE_BREAK.toRegex(), LINE_REPLACEMENT_STRING), Whitelist.none())
        return StringEscapeUtils.unescapeHtml4(clean).replace(LINE_REPLACEMENT_STRING.toRegex(), LINE_BREAK)
    }

    fun fjernHardeMellomrom(tekst: String?): String {
        return tekst?.replace("[\\u00a0\\u2007\\u202f]".toRegex(), " ").toString()
    }

    private fun statusTekst(henvendelse: Henvendelse): String { //NOSONAR
        if (!henvendelse.type?.let { skalVisesTilBruker(it) }!!) {
            return ""
        }
        val type = hentTemagruppeNavn(String.format("status.%s", henvendelse.type!!.name))
        val temagruppe = hentTemagruppeNavn(henvendelse.temagruppe!!.name)
        return String.format(type!!, temagruppe)
    }

    private fun skalVisesTilBruker(type: Henvendelsetype): Boolean {
        return type != Henvendelsetype.DELVIS_SVAR_SKRIFTLIG
    }

    private fun utilDate(dateTime: DateTime?): Date? {
        return dateTime?.toDate()
    }

    init {
        tekstService = TekstServiceImpl()
        domainMapper.registerMapper(DEFAULT_MAPPER)
        domainMapper.registerMapper(LEST_MAPPER)
        domainMapper.registerMapper(KASSERT_MAPPER)
        domainMapper.registerMapper(DOKUMENTVARSEL_MAPPER)
        domainMapper.registerMapper(OPPGAVEVARSEL_MAPPER)
        domainMapper.registerMapper(IKKEVARSEL_MAPPER)
    }
}
