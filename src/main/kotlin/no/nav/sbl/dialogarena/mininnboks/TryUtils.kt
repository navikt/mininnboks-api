package no.nav.sbl.dialogarena.mininnboks

private typealias Supplier<S> = () -> S
private typealias Function<S, T> = (s: S) -> T
sealed class Try<S> {
    companion object {
        @JvmStatic
        fun <S> of(fn: Supplier<S>): Try<S> = create(fn)

        private fun <S> create(fn: Supplier<S>): Try<S> {
            return try {
                val result = fn()
                Success(result)
            } catch (throwable: Throwable) {
                Failure(throwable)
            }
        }
    }

    abstract fun <T> map(fn: Function<S, T>): Try<T>
    abstract fun isSuccess(): Boolean
    abstract fun isFailure(): Boolean
    abstract fun get(): S
    abstract fun getFailure(): Throwable

    class Success<S>(val result: S) : Try<S>() {
        override fun get(): S = result
        override fun getFailure(): Throwable = throw IllegalStateException("'getFailure' called on 'Try.Success'")
        override fun isSuccess(): Boolean = true
        override fun isFailure(): Boolean = false
        override fun <T> map(fn: Function<S, T>): Try<T> {
            return create { fn(result) }
        }


    }

    class Failure<S>(val throwable: Throwable) : Try<S>() {
        override fun get(): S = throw IllegalStateException("'get' called on 'Try.Failure'")
        override fun getFailure(): Throwable = throwable
        override fun isSuccess(): Boolean = false
        override fun isFailure(): Boolean = true
        override fun <T> map(fn: Function<S, T>): Try<T> = this as Failure<T>
    }
}
