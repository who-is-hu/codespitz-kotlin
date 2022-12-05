package day5

class AddRules(block: AddRules.() -> Unit) : MutableSet<Rule> by mutableSetOf() {
    init {
        block()
    }

    // 좋은 방법은 아님 어휘 추가할떄마다 AddRules 고쳐야함
    inner class equal(private val base: Any, private val message: String) : Rule {
        override fun check(target: RuleResult): RuleResult {
            return if (target is RuleResult.Value<*> && target.value == base) target
            else RuleResult.fail(message)
        }

        init {
            // 바깥 this 접근
            this@AddRules += this
        }
    }
}

// 외부에서 수정없이 확장하는 법
class Length(private val length: Number, private val message: String?): Rule {
    override fun check(target: RuleResult): RuleResult {
        return if(target is RuleResult.Value<*> &&
                target.value is String &&
                target.value.length == length) target else RuleResult.fail(message)
    }
}

fun AddRules.length(value: Int, message: String?) {
    this += Length(5, message)
}