package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl.hentTekst
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import org.apache.commons.lang.StringEscapeUtils
import org.joda.time.DateTime
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object HenvendelsesUtils {
    private val logger: Logger = LoggerFactory.getLogger(HenvendelsesUtils::class.java)
    private val LINE_REPLACEMENT_STRING = UUID.randomUUID().toString()
    private const val LINE_BREAK = "\n"
    private val FRA_BRUKER: List<Henvendelsetype> = listOf(Henvendelsetype.SPORSMAL_SKRIFTLIG, Henvendelsetype.SPORSMAL_SKRIFTLIG_DIREKTE, Henvendelsetype.SVAR_SBL_INNGAAENDE)

    private val HENVENDELSETYPE_MAP = HashMap<XMLHenvendelseType, Henvendelsetype>().apply {
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
    private val domainMapper = DomainMapper<XMLHenvendelse, Henvendelse>()
    private val henvendelseFactory =
            { xmlHenvendelse: XMLHenvendelse ->
                val henvendelseType = HENVENDELSETYPE_MAP[XMLHenvendelseType.fromValue(xmlHenvendelse.henvendelseType)]!!
                Henvendelse(
                        id = xmlHenvendelse.behandlingsId,
                        opprettet = utilDate(xmlHenvendelse.opprettetDato),
                        avsluttet = utilDate(xmlHenvendelse.avsluttetDato),
                        ferdigstiltUtenSvar = xmlHenvendelse.isFerdigstiltUtenSvar,
                        traadId = Optional.ofNullable(xmlHenvendelse.behandlingskjedeId).orElse(xmlHenvendelse.behandlingsId),
                        eksternAktor = xmlHenvendelse.eksternAktor,
                        tilknyttetEnhet = xmlHenvendelse.tilknyttetEnhet,
                        kontorsperreEnhet = xmlHenvendelse.kontorsperreEnhet,
                        erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt,
                        type = henvendelseType,
                        brukersEnhet = xmlHenvendelse.brukersEnhet,
                        fraBruker = FRA_BRUKER.contains(henvendelseType),
                        fraNav = !FRA_BRUKER.contains(henvendelseType),
                        temaKode = xmlHenvendelse.tema,
                        korrelasjonsId = xmlHenvendelse.korrelasjonsId
                )
            }

    private val LEST_MAPPER = DomainMapper.Mapper(
            { true },
            { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                henvendelse.copy(
                        lestDato = if (FRA_BRUKER.contains(henvendelse.type)) Date() else utilDate(xmlHenvendelse.lestDato)
                )
            }
    )

    private val KASSERT_MAPPER = DomainMapper.Mapper(
            { xmlHenvendelse: XMLHenvendelse -> xmlHenvendelse.metadataListe == null },
            { _: XMLHenvendelse, henvendelse: Henvendelse ->
                henvendelse.copy(
                        kassert = true,
                        fritekst = hentTekst("innhold.kassert"),
                        statusTekst = hentTekst("temagruppe.kassert"),
                        temagruppe = null,
                        kanal = null
                )
            },
            true
    )

    private val DOKUMENTVARSEL_MAPPER = DomainMapper.Mapper(
            { xmlHenvendelse: XMLHenvendelse -> Henvendelsetype.DOKUMENT_VARSEL.name == xmlHenvendelse.henvendelseType },
            { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val varsel = xmlHenvendelse.metadataListe.metadata[0] as XMLDokumentVarsel
                henvendelse.copy(
                        statusTekst = varsel.dokumenttittel,
                        temaNavn = varsel.temanavn,
                        temagruppe = null,
                        fritekst = Optional.ofNullable(varsel.fritekst).orElse(""),
                        opprettet = utilDate(varsel.ferdigstiltDato),
                        journalpostId = varsel.journalpostId,
                        dokumentIdListe = varsel.dokumentIdListe as ArrayList<String>
                )
            }
    )
    private val OPPGAVEVARSEL_MAPPER = DomainMapper.Mapper(
            { xmlHenvendelse: XMLHenvendelse -> Henvendelsetype.OPPGAVE_VARSEL.name == xmlHenvendelse.henvendelseType },
            { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val varsel = xmlHenvendelse.metadataListe.metadata[0] as XMLOppgaveVarsel
                henvendelse.copy(
                        oppgaveType = varsel.oppgaveType,
                        oppgaveUrl = varsel.oppgaveURL,
                        statusTekst = hentTekst(String.format("oppgave.%s", varsel.oppgaveType), "oppgave.GEN"),
                        fritekst = hentTekst(String.format("oppgave.%s.fritekst", varsel.oppgaveType), "oppgave.GEN.fritekst")
                )
            }
    )
    private val IKKEVARSEL_MAPPER = DomainMapper.Mapper(
            { xmlHenvendelse: XMLHenvendelse ->
                Henvendelsetype.OPPGAVE_VARSEL.name != xmlHenvendelse.henvendelseType &&
                        Henvendelsetype.DOKUMENT_VARSEL.name != xmlHenvendelse.henvendelseType
            },
            { xmlHenvendelse: XMLHenvendelse, henvendelse: Henvendelse ->
                val xmlMelding = xmlHenvendelse.metadataListe.metadata[0] as XMLMelding
                henvendelse.copy(
                        temagruppe = Temagruppe.valueOf(xmlMelding.temagruppe),
                        temagruppeNavn = hentTemagruppeNavn(henvendelse.temagruppe?.name),
                        statusTekst = statusTekst(henvendelse),
                        fritekst = cleanOutHtml(xmlMelding.fritekst),
                        kanal = if (xmlMelding is XMLMeldingTilBruker) xmlMelding.kanal else null
                )
            }
    )

    fun tilHenvendelse(wsMelding: XMLHenvendelse): Henvendelse {
        return domainMapper.apply(wsMelding, henvendelseFactory(wsMelding))
    }


    private fun hentTemagruppeNavn(temagruppeNavn: String?): String? {
        return try {
            temagruppeNavn?.let { hentTekst(it) }
        } catch (exception: MissingResourceException) {
            logger.error("Finner ikke cms-oppslag for $temagruppeNavn", exception)
            temagruppeNavn
        }
    }

    fun cleanOutHtml(text: String): String {
        val clean = Jsoup.clean(text.replace(LINE_BREAK.toRegex(), LINE_REPLACEMENT_STRING), Whitelist.none())
        return StringEscapeUtils.unescapeHtml(clean).replace(LINE_REPLACEMENT_STRING.toRegex(), LINE_BREAK)
    }

    fun fjernHardeMellomrom(tekst: String?): String {
        return tekst?.replace("[\\u00a0\\u2007\\u202f]".toRegex(), " ").toString()
    }

    private fun statusTekst(henvendelse: Henvendelse): String { //NOSONAR
        if (!skalVisesTilBruker(henvendelse.type)) {
            return ""
        }
        val type = hentTemagruppeNavn(String.format("status.%s", henvendelse.type.name))
        val temagruppe = hentTemagruppeNavn(henvendelse.temagruppe?.name)
        return String.format(type!!, temagruppe)
    }

    private fun skalVisesTilBruker(type: Henvendelsetype): Boolean {
        return type != Henvendelsetype.DELVIS_SVAR_SKRIFTLIG
    }

    private fun utilDate(dateTime: DateTime?): Date? {
        return dateTime?.toDate()
    }

    init {
        domainMapper.registerMapper(LEST_MAPPER)
        domainMapper.registerMapper(KASSERT_MAPPER)
        domainMapper.registerMapper(DOKUMENTVARSEL_MAPPER)
        domainMapper.registerMapper(OPPGAVEVARSEL_MAPPER)
        domainMapper.registerMapper(IKKEVARSEL_MAPPER)
    }
}
