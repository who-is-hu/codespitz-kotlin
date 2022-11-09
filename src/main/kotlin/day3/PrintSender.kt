package day3

class PrintSender: Sender {
    override fun send(message: Message) {
        println(message.title)
        println(message.content)
    }
}