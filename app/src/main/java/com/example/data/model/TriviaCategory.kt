package com.example.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.CatArts
import com.example.ui.theme.CatCulture
import com.example.ui.theme.CatFilm
import com.example.ui.theme.CatFood
import com.example.ui.theme.CatGeneral
import com.example.ui.theme.CatGeography
import com.example.ui.theme.CatHistory
import com.example.ui.theme.CatMusic
import com.example.ui.theme.CatPicture
import com.example.ui.theme.CatScience
import com.example.ui.theme.CatSport
import com.example.ui.theme.CatTechnology

enum class TriviaCategory(
    val apiKey: String,
    val title: String,
    val subtitle: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val accentColor: Color,
    val illustrationType: String
) {
    MIXED(
        apiKey = "mixed",
        title = "Atlas Mix",
        subtitle = "Curated across every domain",
        titleRes = R.string.cat_mixed_title,
        subtitleRes = R.string.cat_mixed_sub,
        accentColor = CatGeneral,
        illustrationType = "compass"
    ),
    PICTURE_ATLAS(
        apiKey = "picture_atlas",
        title = "Picture Atlas",
        subtitle = "Illustrated & visual identification",
        titleRes = R.string.cat_picture_title,
        subtitleRes = R.string.cat_picture_sub,
        accentColor = CatPicture,
        illustrationType = "picture"
    ),
    GENERAL_KNOWLEDGE(
        apiKey = "general_knowledge",
        title = "General Knowledge",
        subtitle = "Everyday wonders & crisp facts",
        titleRes = R.string.cat_general_title,
        subtitleRes = R.string.cat_general_sub,
        accentColor = CatGeneral,
        illustrationType = "owl"
    ),
    SCIENCE(
        apiKey = "science",
        title = "Science",
        subtitle = "Cosmos, physics, biology & labs",
        titleRes = R.string.cat_science_title,
        subtitleRes = R.string.cat_science_sub,
        accentColor = CatScience,
        illustrationType = "atom"
    ),
    HISTORY(
        apiKey = "history",
        title = "History",
        subtitle = "Empires, milestones & archives",
        titleRes = R.string.cat_history_title,
        subtitleRes = R.string.cat_history_sub,
        accentColor = CatHistory,
        illustrationType = "column"
    ),
    GEOGRAPHY(
        apiKey = "geography",
        title = "Geography",
        subtitle = "Peaks, rivers, capitals & maps",
        titleRes = R.string.cat_geography_title,
        subtitleRes = R.string.cat_geography_sub,
        accentColor = CatGeography,
        illustrationType = "globe"
    ),
    ARTS_AND_LITERATURE(
        apiKey = "arts_and_literature",
        title = "Arts & Literature",
        subtitle = "Canvas, classics & prose",
        titleRes = R.string.cat_arts_title,
        subtitleRes = R.string.cat_arts_sub,
        accentColor = CatArts,
        illustrationType = "quill"
    ),
    FILM_AND_TV(
        apiKey = "film_and_tv",
        title = "Film & TV",
        subtitle = "Cinema reels & iconic frames",
        titleRes = R.string.cat_film_title,
        subtitleRes = R.string.cat_film_sub,
        accentColor = CatFilm,
        illustrationType = "reel"
    ),
    MUSIC(
        apiKey = "music",
        title = "Music",
        subtitle = "Symphonies, legends & rhythm",
        titleRes = R.string.cat_music_title,
        subtitleRes = R.string.cat_music_sub,
        accentColor = CatMusic,
        illustrationType = "note"
    ),
    SPORT_AND_LEISURE(
        apiKey = "sport_and_leisure",
        title = "Sport & Leisure",
        subtitle = "Tournaments, records & play",
        titleRes = R.string.cat_sport_title,
        subtitleRes = R.string.cat_sport_sub,
        accentColor = CatSport,
        illustrationType = "trophy"
    ),
    FOOD_AND_DRINK(
        apiKey = "food_and_drink",
        title = "Food & Drink",
        subtitle = "Culinary craft & world flavors",
        titleRes = R.string.cat_food_title,
        subtitleRes = R.string.cat_food_sub,
        accentColor = CatFood,
        illustrationType = "cup"
    ),
    SOCIETY_AND_CULTURE(
        apiKey = "society_and_culture",
        title = "Society & Culture",
        subtitle = "Traditions, language & thought",
        titleRes = R.string.cat_culture_title,
        subtitleRes = R.string.cat_culture_sub,
        accentColor = CatCulture,
        illustrationType = "lantern"
    ),
    TECHNOLOGY(
        apiKey = "technology",
        title = "Technology",
        subtitle = "Computing, AI, gadgets & software",
        titleRes = R.string.cat_technology_title,
        subtitleRes = R.string.cat_technology_sub,
        accentColor = CatTechnology,
        illustrationType = "chip"
    ),
    WIKIPEDIA(
        apiKey = "wikipedia",
        title = "Wikipedia Daily",
        subtitle = "Daily random article quiz",
        titleRes = R.string.cat_wikipedia_title,
        subtitleRes = R.string.cat_wikipedia_sub,
        accentColor = CatGeography,
        illustrationType = "globe"
    );

    companion object {
        val playableGridCategories: List<TriviaCategory> = listOf(
            GENERAL_KNOWLEDGE,
            SCIENCE,
            TECHNOLOGY,
            HISTORY,
            GEOGRAPHY,
            ARTS_AND_LITERATURE,
            FILM_AND_TV,
            MUSIC,
            SPORT_AND_LEISURE,
            FOOD_AND_DRINK,
            SOCIETY_AND_CULTURE
        )

        fun fromApiKey(raw: String?): TriviaCategory {
            if (raw.isNullOrBlank()) return GENERAL_KNOWLEDGE
            val normalized = raw.trim().lowercase().replace(" & ", "_and_").replace(" ", "_")
            return entries.firstOrNull { it.apiKey == normalized || it.name.lowercase() == normalized }
                ?: GENERAL_KNOWLEDGE
        }
    }
}

enum class QuizDifficulty(val apiKey: String, val label: String, @StringRes val labelRes: Int) {
    ALL("all", "Any Level", R.string.diff_all),
    EASY("easy", "Easy", R.string.diff_easy),
    MEDIUM("medium", "Medium", R.string.diff_medium),
    HARD("hard", "Hard", R.string.diff_hard);

    companion object {
        fun fromKey(raw: String?): QuizDifficulty =
            entries.firstOrNull { it.apiKey == raw?.lowercase() } ?: ALL
    }
}

enum class QuizMode(val key: String, val label: String, @StringRes val labelRes: Int) {
    STANDARD("standard", "Quiz Mode", R.string.mode_quiz),
    GUESS("guess", "Guess Mode", R.string.mode_guess),
    PICTURE("picture", "Picture Quiz", R.string.mode_picture_quiz);

    companion object {
        fun fromKey(raw: String?): QuizMode =
            entries.firstOrNull { it.key == raw?.lowercase() } ?: STANDARD
    }
}
