package com.example.cfrivals

import com.example.cfrivals.Models.BattleLogCalculator
import com.example.cfrivals.Models.Problem
import com.example.cfrivals.Models.Submission
import org.junit.Assert.assertEquals
import org.junit.Test

class BattleLogCalculatorTest {

    @Test
    fun `rival problems not solved by user are returned`() {

        val mySubmissions = listOf(
            Submission(
                id = 1,
                verdict = "OK",
                problem = Problem(1000, "A", "Problem A", 800)
            ),
            Submission(
                id = 2,
                verdict = "OK",
                problem = Problem(1000, "B", "Problem B", 900)
            ),
            Submission(
                id = 3,
                verdict = "OK",
                problem = Problem(1000, "C", "Problem C", 1000)
            )
        )

        val rivalSubmissions = listOf(
            Submission(
                id = 4,
                verdict = "OK",
                problem = Problem(1000, "B", "Problem B", 900)
            ),
            Submission(
                id = 5,
                verdict = "OK",
                problem = Problem(1000, "C", "Problem C", 1000)
            ),
            Submission(
                id = 6,
                verdict = "OK",
                problem = Problem(1000, "D", "Problem D", 1100)
            ),
            Submission(
                id = 7,
                verdict = "OK",
                problem = Problem(1000, "E", "Problem E", 1200)
            )
        )

        val result = BattleLogCalculator.problemsToCatchUp(
            mySubmissions = mySubmissions,
            rivalSubmissions = rivalSubmissions
        )

        val resultKeys = result.map {
            "${it.contestId}:${it.index}"
        }

        assertEquals(
            listOf("1000:D", "1000:E"),
            resultKeys
        )
    }

    @Test
    fun `duplicate rival submissions are returned only once`() {

        val mySubmissions = emptyList<Submission>()

        val rivalSubmissions = listOf(
            Submission(
                id = 1,
                verdict = "OK",
                problem = Problem(1000, "A", "Problem A", 800)
            ),
            Submission(
                id = 2,
                verdict = "OK",
                problem = Problem(1000, "A", "Problem A", 800)
            ),
            Submission(
                id = 3,
                verdict = "OK",
                problem = Problem(1000, "B", "Problem B", 900)
            )
        )

        val result = BattleLogCalculator.problemsToCatchUp(
            mySubmissions = mySubmissions,
            rivalSubmissions = rivalSubmissions
        )

        assertEquals(2, result.size)
    }

    @Test
    fun `wrong submissions are not included`() {

        val mySubmissions = emptyList<Submission>()

        val rivalSubmissions = listOf(
            Submission(
                id = 1,
                verdict = "WRONG_ANSWER",
                problem = Problem(1000, "A", "Problem A", 800)
            ),
            Submission(
                id = 2,
                verdict = "TIME_LIMIT_EXCEEDED",
                problem = Problem(1000, "B", "Problem B", 900)
            )
        )

        val result = BattleLogCalculator.problemsToCatchUp(
            mySubmissions = mySubmissions,
            rivalSubmissions = rivalSubmissions
        )

        assertEquals(0, result.size)
    }

    @Test
    fun `no catch up problems returns empty list`() {

        val mySubmissions = listOf(
            Submission(
                id = 1,
                verdict = "OK",
                problem = Problem(1000, "A", "Problem A", 800)
            )
        )

        val rivalSubmissions = listOf(
            Submission(
                id = 2,
                verdict = "OK",
                problem = Problem(1000, "A", "Problem A", 800)
            )
        )

        val result = BattleLogCalculator.problemsToCatchUp(
            mySubmissions = mySubmissions,
            rivalSubmissions = rivalSubmissions
        )

        assertEquals(0, result.size)
    }

    @Test
    fun `empty submissions return empty list`() {

        val result = BattleLogCalculator.problemsToCatchUp(
            mySubmissions = emptyList(),
            rivalSubmissions = emptyList()
        )

        assertEquals(0, result.size)
    }
}