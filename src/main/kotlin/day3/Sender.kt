package day3

interface Sender {
    fun send(message: Message)
}

data class Message(
    val title: String,
    val content: String,
)