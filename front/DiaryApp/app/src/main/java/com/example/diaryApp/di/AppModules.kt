package com.example.diaryApp.di


import android.app.Application
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.RetrofitClient
import com.example.diaryApp.domain.repository.ProfileListRepository
import com.example.diaryApp.domain.repository.ProfileListRepositoryImpl
import com.example.diaryApp.domain.repository.UserRepository
import com.example.diaryApp.domain.repository.UserRepositoryImpl
import com.example.diaryApp.domain.services.ProfileListService
import com.example.diaryApp.domain.services.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)  // 필요에 맞는 컴포넌트로 설치
class AppModules {

    @Provides
    fun provideUserStore(application: Application): UserStore {
        return UserStore(application)
    }

    @Provides
    fun provideRetrofit(): Retrofit = RetrofitClient.getInstance()

    @Provides
    fun provideUserRepository(
        userService: UserService,
        userStore: UserStore
    ): UserRepository = UserRepositoryImpl(userService, userStore)

    @Provides
    fun provideUserService(
        retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun provideProfileListRepository(
        profileListService: ProfileListService
    ) : ProfileListRepository = ProfileListRepositoryImpl(profileListService)

    @Provides
    fun provideProfileListService(retrofit: Retrofit): ProfileListService {
        return retrofit.create(ProfileListService::class.java)
    }

}
