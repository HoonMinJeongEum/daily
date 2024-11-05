package com.example.diarytablet.di


import android.app.Application
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.domain.repository.ProfileListRepositoryImpl
import com.example.diarytablet.domain.repository.QuizRepository
import com.example.diarytablet.domain.repository.QuizRepositoryImpl
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.domain.repository.UserRepositoryImpl
import com.example.diarytablet.domain.service.AlarmService
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.domain.service.QuizService
import com.example.diarytablet.domain.service.UserService
import com.ssafy.daily.alarm.repository.AlarmRepository
import com.ssafy.daily.alarm.repository.AlarmRepositoryImpl
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
    ): UserRepository = UserRepositoryImpl(userService,userStore,)

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

    @Provides
    fun provideQuizRepository(
        quizService: QuizService
    ): QuizRepository {
        return QuizRepositoryImpl(quizService)
    }

    @Provides
    fun provideQuizService(retrofit: Retrofit): QuizService {
        return retrofit.create(QuizService::class.java)
    }

    @Provides
    fun provideAlarmRepository(
        alarmService: AlarmService
    ): AlarmRepository {
        return AlarmRepositoryImpl(alarmService)
    }

    @Provides
    fun provideAlarmService(retrofit: Retrofit): AlarmService {
        return retrofit.create(AlarmService::class.java)
    }
}
