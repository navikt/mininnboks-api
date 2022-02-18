package no.nav.sbl.dialogarena.mininnboks

inline fun <T> Result<T>.getOrThrowWith(fn: (Throwable) -> Throwable): T {
    val exception = exceptionOrNull()
    if (exception != null) {
        throw fn(exception)
    }
    return getOrThrow()
}
