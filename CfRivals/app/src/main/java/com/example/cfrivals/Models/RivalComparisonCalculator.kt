package com.example.cfrivals.Models

enum class ComparisonWinner {
    YOU,
    RIVAL,
    EQUAL
}

data class RivalComparisonResult(
    val ratingDifference: Int,
    val solvedDifference: Int,
    val ratingWinner: ComparisonWinner,
    val solvedWinner: ComparisonWinner,
    val overallWinner: ComparisonWinner,
    val myDominancePercentage: Int
)

object RivalComparisonCalculator {
    private const val RATING_WEIGHT = 0.7f
    private const val SOLVED_WEIGHT = 10f

    fun compare(
        myRating: Int,
        rivalRating: Int,
        mySolvedProblems: Int,
        rivalSolvedProblems: Int
    ): RivalComparisonResult {
        val ratingDifference = myRating - rivalRating
        val solvedDifference = mySolvedProblems - rivalSolvedProblems

        val ratingWinner = determineWinner(ratingDifference)
        val solvedWinner = determineWinner(solvedDifference)

        val myScore =
            (myRating*RATING_WEIGHT) + (mySolvedProblems*SOLVED_WEIGHT)
        val rivalScore =
            (rivalRating*RATING_WEIGHT) + (rivalSolvedProblems*SOLVED_WEIGHT)
        val overallWinner = when {
            myScore > rivalScore -> ComparisonWinner.YOU
            myScore < rivalScore -> ComparisonWinner.RIVAL
            else -> ComparisonWinner.EQUAL
        }

        val totalScore = myScore + rivalScore

        val dominancePercentage =
            if(totalScore > 0f) {
                ((myScore/totalScore)*100).toInt()
            } else {
                50
            }

        return RivalComparisonResult(
            ratingDifference = ratingDifference,
            solvedDifference = solvedDifference,
            ratingWinner = ratingWinner,
            solvedWinner = solvedWinner,
            overallWinner = overallWinner,
            myDominancePercentage = dominancePercentage
        )
    }

    private fun determineWinner(difference: Int): ComparisonWinner {
        return when {
            difference > 0 -> ComparisonWinner.YOU
            difference < 0 -> ComparisonWinner.RIVAL
            else -> ComparisonWinner.EQUAL
        }
    }
}