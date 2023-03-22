package it.thefedex87.cooldrinks.data.remote

val invalidSearchDrinkResponse = """
    {
        drinks: [
            {
                "idDrink": 1,
                "strDrink": "Name1",
                "strDrinkThumb": "Image1"
            },,
            {
                "idDrink": 2,
                "strDrink": "Name2",
                "strDrinkThumb": "Image2"
            },
            {
                "idDrink": 3,
                "strDrink": "Name3",
                "strDrinkThumb": "Image3"
            },
            {
                "idDrink": 4,
                "strDrink": "Name4",
                "strDrinkThumb": "Image4"
            }
        ]
    }
""".trimIndent()