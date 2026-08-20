package com.example.cfrivals.Models

data class CFResponse<T>(
    val status: String,
    val result: List<T>?= null,
    val comment: String?= null
)

data class User(
    val handle: String,
    val rating: Int,
    val rank: String,
    val titlePhoto: String
)

data class Submission(
    val id: Long,
    val verdict: String,
    val problem: Problem
)

data class Problem(
    val contestId: Int,
    val index: String,
    val name: String,
    val rating: Int?
)