package com.example.diarytablet.datastore

interface UserStore {
    fun login(username: String, password: String): Boolean
}

class UserStoreImpl : UserStore {
    override fun login(username: String, password: String): Boolean {
        // 실제 로그인 로직 (예: 서버와 통신)
        return username == "test" && password == "password"
    }
}
