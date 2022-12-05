package day5

interface Rule {
    fun check(target: RuleResult): RuleResult
}

sealed interface RuleResult {
    companion object {
        private val defaultMessage = "invalid"
        fun <T:Any> value(v: T) = Value(v)
        fun fail(msg: String?) = Fail(msg ?: defaultMessage)
    }
    data class Value<T: Any>(val value: T): RuleResult
    data class Fail(val msg: String): RuleResult
}