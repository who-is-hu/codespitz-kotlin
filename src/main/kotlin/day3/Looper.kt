package day3

//abstract class Looper {
//    companion object{
//        val users = hashSetOf<User>()
//    }
//
//    var isRunning = false
//        private set
//
//    fun start() {
//        isRunning = true
//        started()
//    }
//    fun end() {
//        isRunning = false
//        ended()
//    }
//
//    protected abstract fun started()
//    protected abstract fun ended()
//}

class Looper(
    private val started: (Looper) -> Unit,
    private val ended: (Looper) -> Unit,
) {
    companion object {
        val users = hashSetOf<User>()
    }

    var isRunning = false
        private set

    fun start(){
        isRunning = true
        started(this)
    }

    fun end(){
        isRunning = false
        ended(this)
    }
}