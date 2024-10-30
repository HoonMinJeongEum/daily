package com.example.diarytablet.di


import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.domain.repository.ProfileListRepositoryImpl
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.domain.repository.UserRepositoryImpl
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.domain.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)  // 필요에 맞는 컴포넌트로 설치
class AppModules {

    @Provides
    fun provideRetrofit(): Retrofit = RetrofitClient.getInstance()

    @Provides
    fun provideUserRepository(
        userService: UserService
    ): UserRepository = UserRepositoryImpl(userService)

    @Provides
    fun provideProfileListRepository(
        profileListService: ProfileListService
    ) :ProfileListRepository = ProfileListRepositoryImpl(profileListService)
    @Provides
    fun provideUserService(
        retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun provideProfileListService(retrofit: Retrofit): ProfileListService {
        return retrofit.create(ProfileListService::class.java)
    }

}
