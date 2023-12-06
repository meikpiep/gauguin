package org.piepmeyer.gauguin.options

enum class NumeralSystem {
    Binary,
    Decimal,
    Hexadecimal,
    Quaternary,
    Octal,
    ;

    fun displayableString(number: Int): String {
        return when (this) {
            Binary -> Integer.toBinaryString(number)
            Quaternary -> number.toString(4)
            Octal -> number.toString(8)
            Decimal -> number.toString()
            Hexadecimal -> number.toString(16).uppercase()
        }
    }
}
