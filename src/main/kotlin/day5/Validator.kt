package day5

interface Validator {
    fun <T:Any> check(v: Any): Result<T>
}