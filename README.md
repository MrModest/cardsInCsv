# cardsInCsv
Kotlin DSL for easy creating Anki cards

Example of using

```kotlin
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
```

Example result: file `anki_import_2022_09_18_20_27.txt`

```sv
#html:true
#separator:Tab
#columns:term	meaning	examples
increasingly	<i>more and more all the time</i>	<ul><li>Our culture seems to<b> <span style="color: rgb(222, 39, 0);">increasingly</span> </b>value efficiency over almost everything else.</li><li>One more example!</li></ul>
increasingly (2nd meaning)	<i>one more meaning!</i>	<ul><li>Example for second meaning</li></ul>
increasingly (3rd meaning)	<i>meaning without examples!</i>	<ul></ul>
toddler	<i>a child who has only recently learnt to walk</i>	<ul></ul>
```

Preview:

<img width="622" alt="image" src="https://user-images.githubusercontent.com/13031058/190923131-d8424fcc-0a6a-4b5a-8b67-4b6b8e099360.png">


---

For more info you can read Anki import documentation: https://docs.ankiweb.net/importing.html
