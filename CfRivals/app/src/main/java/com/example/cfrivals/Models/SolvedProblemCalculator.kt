package com.example.cfrivals.Models

object SolvedProblemCalculator {

    fun uniqueSolvedProblems(
        submissions: List<Submission>
    ): Set<String> {
        return submissions
            .asSequence()
            .filter { it.verdict == "OK" }
            .map {
                "${it.problem.contestId}:${it.problem.index}"
            }
            .toSet()
    }

    fun countUniqueSolvedProblems(
        submissions: List<Submission>
    ): Int {
        return uniqueSolvedProblems(submissions).size
    }
}