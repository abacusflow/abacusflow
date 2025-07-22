package org.abacusflow.commons

enum class Sex {
    M,
    F,
    ;

    companion object {
        fun fromString(value: String): Sex =
            when (value) {
                "M" -> M
                "F" -> F
                else -> throw IllegalArgumentException("sex not supported $value")
            }
    }
}
