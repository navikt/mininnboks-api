package no.nav.sbl.dialogarena.mininnboks

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun <T> Result<T>.getOrThrowWith(fn: (Throwable) -> Throwable): T {
    val exception = exceptionOrNull()
    if (exception != null) {
        throw fn(exception)
    }
    return getOrThrow()
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> requireNotEmpty(value: List<T>?, lazyMessage: () -> Any): List<T> {
    contract {
        returns() implies (value != null)
    }
    val nonNonValue = requireNotNull(value, lazyMessage)
    require(nonNonValue.isNotEmpty(), lazyMessage)
    return nonNonValue
}
