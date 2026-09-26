package com.example.cfrivals.Models

object BattleLogCalculator {

    fun problemsToCatchUp(
        mySubmissions: List<Submission>,
        rivalSubmissions: List<Submission>
    ): List<Problem> {

        val mySolvedProblems =
            SolvedProblemCalculator.uniqueSolvedProblems(
                mySubmissions
            )

        return rivalSubmissions
            .asSequence()
            .filter { it.verdict == "OK" }
            .map { it.problem }
            .filter { problem ->
                val problemKey =
                    "${problem.contestId}:${problem.index}"

                problemKey !in mySolvedProblems
            }
            .distinctBy { problem ->
                "${problem.contestId}:${problem.index}"
            }
            .toList()
    }
}