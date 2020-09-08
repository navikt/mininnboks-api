package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

class DomainMapper<S, T> {
    class Mapper<S, T>(val brukMapper: (S) -> Boolean, val mapper: (S, T) -> T, val breakAfterMapping: Boolean = false)

    private val mappers: MutableList<Mapper<S, T>>

    fun registerMapper(mapper: Mapper<S, T>) {
        mappers.add(mapper)
    }

    fun apply(xmlValue: S, initailValue: T): T {
        var value: T = initailValue
        for (mapper in mappers) {
            if (mapper.brukMapper(xmlValue)) {
                value = mapper.mapper(xmlValue, value)

                if (mapper.breakAfterMapping) {
                    break
                }
            }
        }
        return value
    }

    init {
        mappers = LinkedList<Mapper<S, T>>()
    }
}
