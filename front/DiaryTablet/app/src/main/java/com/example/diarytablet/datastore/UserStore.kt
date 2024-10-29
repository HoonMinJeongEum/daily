package com.example.diarytablet.datastore

interface UserStore {
    fun login(username: String, password: String): Boolean
}

class FakeUserStore : UserStore {
    override fun login(username: String, password: String): Boolean {
        return username == "test" && password == "1234"
    }
}