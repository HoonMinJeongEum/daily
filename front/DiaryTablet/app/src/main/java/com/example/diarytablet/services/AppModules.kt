package com.example.diarytablet.services

import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.datastore.UserStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // 필요에 맞는 컴포넌트로 설치
object AppModules {

    @Provides
    @Singleton
    fun provideUserStore(): UserStore {
        return UserStoreImpl()
    }
}
