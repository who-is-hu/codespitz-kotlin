package day5

// Rule 시스템의 결과를 밖의 Result로 바꿔주는 역할
class RuleValidator(block: RuleDSL.() -> Unit): Validator {
    private val ruleDSL: RuleDSL by lazy { RuleDSL(block) } // 메모리 늦게 올리기
    override fun <T : Any> check(v: Any): Result<T> {
        return when(val result: RuleResult = ruleDSL.check(v)){
            is RuleResult.Value<*> -> (result.value as? T)?.let {
                Result.success(it)
            } ?: Result.failure(Throwable("invalid type (cast)"))
            is RuleResult.Fail -> Result.failure(Throwable(result.msg))
        }
    }
}