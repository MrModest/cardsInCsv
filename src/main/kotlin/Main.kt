import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(args: Array<String>) {
    val result = anki {
        term("increasingly") {
            means("more and more all the time") {
                example("Our culture seems to increasingly value efficiency over almost everything else.")
                example("One more example!")
            }
            means("2nd meaning", "one more meaning!") {
                example("Example for second meaning")
            }
            means("3rd meaning", "meaning without examples!")
        }
        term("toddler") {
            means("a child who has only recently learnt to walk")
        }
    }

    val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm"))

    if (args.isEmpty()) {
        println("Can't write the result to a file because a directory is not set!")
        println("You can find the result as a plain text below\n")
        println(result)
        return
    }

    File(args[0], "anki_import_$date.txt").also { it.createNewFile() }.printWriter().use { out ->
        out.println(result)
    }
}