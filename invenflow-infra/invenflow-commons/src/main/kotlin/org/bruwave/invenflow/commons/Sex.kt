package org.bruwave.invenflow.commons

enum class Sex {
    MALE,
    FEMALE,
    UNKNOWN,
    ;

    companion object {
        fun fromString(value: String): Sex =
            when (value) {
                "MALE" -> MALE
                "FEMALE" -> FEMALE
                else -> UNKNOWN
            }
    }
}
