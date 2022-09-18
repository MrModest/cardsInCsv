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
) {
    fun toReverse() = Card(meaning, term, examples)

    override fun toString() = StringBuilder().apply {
        append(term); append('\t')
        append(tagWrap("i", meaning)); append('\t')
        append(examples.map { e -> e.boldWord(term).colorWord(term) }.toUlList())
    }.toString()
}

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
        cards.forEach { append(it.toString()); append('\n') }
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

fun AnkiCardsCollector.term(term: String, meaning: String) {
    this.addCard(
        TermMeaningsCollector(term)
            .apply{
                means(meaning)
            }
            .toCards()
    )
}

fun AnkiCardsCollector.termWithReverse(term: String, meaning: String) {
    term(term, meaning)
    term(meaning, term)
}

private fun TermMeaningsCollector.meansInternal(
    clarification: String?,
    explaining: String,
    meaningExamplesCollectorHandler: (MeaningExamplesCollector.() -> Unit)?,
    withReverse: Boolean = false
) {
    val postfix = if (clarification != null) { " ($clarification)" } else { "" }
    val collector = MeaningExamplesCollector("${this.term}$postfix", explaining)
    if (meaningExamplesCollectorHandler != null) {
        collector.meaningExamplesCollectorHandler()
    }

    val card = collector.toCard()

    this.addMeaning(card)
    if (withReverse) {
        this.addMeaning(card.toReverse())
    }
}

fun TermMeaningsCollector.means(clarification: String, explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    meansInternal(clarification, explaining, meaningExamplesCollectorHandler, false)
}

fun TermMeaningsCollector.means(explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    meansInternal(null, explaining, meaningExamplesCollectorHandler, false)
}

fun TermMeaningsCollector.means(clarification: String, explaining: String) {
    meansInternal(clarification, explaining, null, false)
}

fun TermMeaningsCollector.means(explaining: String) {
    meansInternal(null, explaining, null, false)
}

fun TermMeaningsCollector.meansWithReverse(clarification: String, explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    meansInternal(clarification, explaining, meaningExamplesCollectorHandler, true)
}

fun TermMeaningsCollector.meansWithReverse(explaining: String, meaningExamplesCollectorHandler: MeaningExamplesCollector.() -> Unit) {
    meansInternal(null, explaining, meaningExamplesCollectorHandler, true)
}

fun TermMeaningsCollector.meansWithReverse(clarification: String, explaining: String) {
    meansInternal(clarification, explaining, null, true)
}

fun TermMeaningsCollector.meansWithReverse(explaining: String) {
    meansInternal(null, explaining, null, true)
}

fun MeaningExamplesCollector.example(sentence: String) {
    this.addExample(sentence)
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