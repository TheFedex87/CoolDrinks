package it.thefedex87.cooldrinks.data.remote

val invalidIngredientDetailsResponse = """
    {
        "ingredients:
         [
            {
                "idIngredient": 1,
                "strABV": "40",
                "strAlcohol": "Yes",
                "strDescription": "Ingredient details",
                "strIngredient": "Ingredient name",
                "strType": "Ingredient type"
            }
        ]
    }
""".trimIndent()