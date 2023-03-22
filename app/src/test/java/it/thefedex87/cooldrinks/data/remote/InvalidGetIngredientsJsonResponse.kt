package it.thefedex87.cooldrinks.data.remote

val invalidGetIngredientsResponse = """
    {
        "drinks: [
            { "strIngredient1": "Ingredient 1" },
            { "strIngredient1": "Ingredient 2" },
            { "strIngredient1": "Ingredient 3" },
            { "strIngredient1": "Ingredient 4" }
        ]
    }
""".trimIndent()