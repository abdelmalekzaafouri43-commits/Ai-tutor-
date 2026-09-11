package com.example.util

import com.example.R

object IllustrationUtils {

    fun getTopicIllustrationRes(topic: String): Int {
        val lower = topic.lowercase()
        return when {
            lower.contains("tense") || lower.contains("verb") -> R.drawable.img_verb_tenses_illustration_1789127792885
            lower.contains("preposition") || lower.contains("place") -> R.drawable.img_prepositions_illustration_1789127811955
            else -> R.drawable.img_hero_banner_1789125014572
        }
    }
}
