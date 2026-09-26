package com.example.cfrivals.Models

object CFSessionCache {

    var myUser: User? = null
    var rivalUser: User? = null

    var mySubmissions: List<Submission>? = null
    var rivalSubmissions: List<Submission>? = null

    var cachedMyHandle: String? = null
    var cachedRivalHandle: String? = null

    fun isValidFor(
        myHandle: String,
        rivalHandle: String
    ): Boolean {
        return cachedMyHandle.equals(myHandle, ignoreCase = true) &&
                cachedRivalHandle.equals(rivalHandle, ignoreCase = true) &&
                myUser != null &&
                rivalUser != null &&
                mySubmissions != null &&
                rivalSubmissions != null
    }

    fun clear() {
        myUser = null
        rivalUser = null
        mySubmissions = null
        rivalSubmissions = null
        cachedMyHandle = null
        cachedRivalHandle = null
    }
}