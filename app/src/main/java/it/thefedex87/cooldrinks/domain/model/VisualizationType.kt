package it.thefedex87.cooldrinks.domain.model

sealed class VisualizationType {
    object Card : VisualizationType()
    object List : VisualizationType()

    companion object {
        fun fromString(name: String) : VisualizationType {
            return when(name) {
                "Card" -> Card
                "List" -> List
                else -> Card
            }
        }

        fun toSimpleString(type: VisualizationType): String {
            return when(type) {
                Card -> "Card"
                List -> "List"
            }
        }
    }
}