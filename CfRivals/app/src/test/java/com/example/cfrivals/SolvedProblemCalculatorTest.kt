package com.example.cfrivals

import com.example.cfrivals.Models.Problem
import com.example.cfrivals.Models.SolvedProblemCalculator
import com.example.cfrivals.Models.Submission
import org.junit.Assert.assertEquals
import org.junit.Test

class SolvedProblemCalculatorTest {

    @Test
    fun duplicateAcceptedSubmissionsAreCountedOnce() {

        val submissions = listOf(
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
            ),
            Submission(
                id = 4,
                verdict = "WRONG_ANSWER",
                problem = Problem(1000, "C", "Problem C", 1000)
            ),
            Submission(
                id = 5,
                verdict = "OK",
                problem = Problem(1000, "C", "Problem C", 1000)
            )
        )

        val result =
            SolvedProblemCalculator.countUniqueSolvedProblems(submissions)

        assertEquals(3, result)
    }
}