package org.piepmeyer.gauguin.options

enum class NumeralSystem {
    Binary,
    Decimal,
    Hexadecimal,
    Quaternary,
    Octal,
    ;

    fun displayableString(number: Int): String =
        when (this) {
            Binary -> number.toString(2)
            Quaternary -> number.toString(4)
            Octal -> number.toString(8)
            Decimal -> number.toString()
            Hexadecimal -> number.toString(16).uppercase()
        }
}
