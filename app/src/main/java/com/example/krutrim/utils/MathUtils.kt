package com.example.krutrim.utils

import kotlin.math.exp

object MathUtils {

    fun calculatePerplexity(probabilities: List<Double>): Double {
        val entropy = probabilities.sumOf { it * Math.log(it) }
        return exp(-entropy)
    }
}