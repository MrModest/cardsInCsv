/*
* anki {
*     term("term1") {
*         means("translate of explaining term) {
*             example ("Sentence with an example of using the term")
*             example ("Sentence with an example of using the term")
*         }
*     }
*     term("term2") {
*         means("translate of explaining term) {
*             example ("Sentence with an example of using the term")
*             example ("Sentence with an example of using the term")
*         }
*     }
* }
*/

data class Card(
    val term: String,
    val meaning: String,
    val examples: List<String>
)

class AnkiCardsCollector(
    private val cards: MutableList<Card> = mutableListOf()
) {
    private companion object {
        val keys = listOf(
            "#html:true",
            "#separator:Tab",
            "#columns:term\tmeaning\texamples"
        )
    }

    fun addCard(card: List<Card>) {
        cards.addAll(card)
    }

    fun toAnkiImportText() = StringBuilder().apply {
        keys.forEach { append(it); append('\n') }

        cards.forEach {
            append(it.term); append('\t')
            append(it.meaning); append('\t')
            append(it.examples.toUlList())
            append('\n')
        }
    }.toString()
}

class TermMeaningsCollector(
    val term: String,
    private val meanings: MutableList<Card> = mutableListOf()
) {
    fun addMeaning(meaning: Card) {
        meanings.add(meaning)
    }

    fun toCards() = meanings as List<Card>
}

class MeaningExamplesCollector(
    val term: String,
    private val meaning: String,
    private val examples: MutableList<String> = mutableListOf()
) {
    fun addExample(example: String) {
        examples.add(example)
    }

    fun toCard() = Card(term, meaning, examples)
}

fun anki(cardsHandler: AnkiCardsCollector.() -> Unit): String {
    return AnkiCardsCollector()
        .apply(cardsHandler)
        .toAnkiImportText()
}

fun AnkiCardsCollector.term(term: String, termMeaningsCollectorHandler: TermMeaningsCollector.() -> Unit) {
    this.addCard(
        TermMeaningsCollector(term)
            .apply(termMeaningsCollectorHandler)
            .toCards()
    )
}

fun TermMeaningsCollector.means(clarification: String, explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    this.addMeaning(
        MeaningExamplesCollector("${this.term} ($clarification)", tagWrap("i", explaining))
            .apply(meaningExamplesCollectorHandler)
            .toCard()
    )
}

fun TermMeaningsCollector.means(explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    this.addMeaning(
        MeaningExamplesCollector(this.term, tagWrap("i", explaining))
            .apply(meaningExamplesCollectorHandler)
            .toCard()
    )
}

fun TermMeaningsCollector.means(clarification: String, explaining: String) {
    this.addMeaning(
        MeaningExamplesCollector("${this.term} ($clarification)", tagWrap("i", explaining))
            .toCard()
    )
}

fun TermMeaningsCollector.means(explaining: String) {
    this.addMeaning(
        MeaningExamplesCollector(this.term, tagWrap("i", explaining))
            .toCard()
    )
}

fun MeaningExamplesCollector.example(sentence: String) {
    this.addExample(sentence.boldWord(this.term).colorWord(this.term))
}

private fun String.boldWord(word: String): String {
    val start = this.indexOf(word)
    if (start == -1) {
        return this
    }

    val end = start + word.length

    return StringBuilder(this).apply {
        insert(end, tag("/b"))
        insert(start, tag("b"))
    }.toString()
}

private fun String.colorWord(word: String): String {
    val start = this.indexOf(word)
    if (start == -1) {
        return this
    }

    val end = start + word.length

    return StringBuilder(this).apply {
        insert(end, tag("/span"))
        insert(start, tag("span style=\"color: rgb(222, 39, 0);\""))
    }.toString()
}

private fun tagWrap(tagId: String, value: String) = "${tag(tagId)}$value${tag("/$tagId")}"
private fun tag(tagId: String) = "<$tagId>"
private fun List<String>.toUlList(): String {
    return StringBuilder().also {
        it.append(tag("ul"))
        for (el in this) {
            it.append(tagWrap("li", el))
        }
        it.append(tag("/ul"))
    }.toString()
}