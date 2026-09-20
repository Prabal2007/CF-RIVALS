package com.example.cfrivals

import com.example.cfrivals.Models.ComparisonWinner
import com.example.cfrivals.Models.RivalComparisonCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class RivalComparisonCalculatorTest {

    @Test
    fun `higher rating gives positive difference and user wins`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1600,
            rivalRating = 1400,
            mySolvedProblems = 100,
            rivalSolvedProblems = 90
        )

        assertEquals(200, result.ratingDifference)
        assertEquals(ComparisonWinner.YOU, result.ratingWinner)
    }

    @Test
    fun `lower rating gives negative difference and rival wins`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1400,
            rivalRating = 1600,
            mySolvedProblems = 100,
            rivalSolvedProblems = 90
        )

        assertEquals(-200, result.ratingDifference)
        assertEquals(ComparisonWinner.RIVAL, result.ratingWinner)
    }

    @Test
    fun `equal rating gives zero difference and equal result`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1500,
            rivalRating = 1500,
            mySolvedProblems = 100,
            rivalSolvedProblems = 100
        )

        assertEquals(0, result.ratingDifference)
        assertEquals(ComparisonWinner.EQUAL, result.ratingWinner)
    }

    @Test
    fun `higher solved problems gives positive difference and user wins`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1500,
            rivalRating = 1500,
            mySolvedProblems = 120,
            rivalSolvedProblems = 100
        )

        assertEquals(20, result.solvedDifference)
        assertEquals(ComparisonWinner.YOU, result.solvedWinner)
    }

    @Test
    fun `lower solved problems gives negative difference and rival wins`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1500,
            rivalRating = 1500,
            mySolvedProblems = 80,
            rivalSolvedProblems = 100
        )

        assertEquals(-20, result.solvedDifference)
        assertEquals(ComparisonWinner.RIVAL, result.solvedWinner)
    }

    @Test
    fun `equal solved problems gives zero difference`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1500,
            rivalRating = 1500,
            mySolvedProblems = 100,
            rivalSolvedProblems = 100
        )

        assertEquals(0, result.solvedDifference)
        assertEquals(ComparisonWinner.EQUAL, result.solvedWinner)
    }

    @Test
    fun `equal metrics give equal overall result`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1500,
            rivalRating = 1500,
            mySolvedProblems = 100,
            rivalSolvedProblems = 100
        )

        assertEquals(ComparisonWinner.EQUAL, result.overallWinner)
        assertEquals(50, result.myDominancePercentage)
    }

    @Test
    fun `higher combined score gives user overall win`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1600,
            rivalRating = 1400,
            mySolvedProblems = 120,
            rivalSolvedProblems = 100
        )

        assertEquals(ComparisonWinner.YOU, result.overallWinner)
    }

    @Test
    fun `higher combined score for rival gives rival overall win`() {
        val result = RivalComparisonCalculator.compare(
            myRating = 1400,
            rivalRating = 1600,
            mySolvedProblems = 100,
            rivalSolvedProblems = 120
        )

        assertEquals(ComparisonWinner.RIVAL, result.overallWinner)
    }
}