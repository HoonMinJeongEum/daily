package com.example.diaryApp.di


import android.app.Application
import com.example.diaryApp.datastore.UserStore
import com.example.diaryApp.domain.RetrofitClient
import com.example.diaryApp.domain.repository.alarm.AlarmRepository
import com.example.diaryApp.domain.repository.alarm.AlarmRepositoryImpl
import com.example.diaryApp.domain.repository.coupon.CouponRepository
import com.example.diaryApp.domain.repository.coupon.CouponRepositoryImpl
import com.example.diaryApp.domain.repository.diary.DiaryRepository
import com.example.diaryApp.domain.repository.diary.DiaryRepositoryImpl
import com.example.diaryApp.domain.repository.profile.ProfileListRepository
import com.example.diaryApp.domain.repository.profile.ProfileListRepositoryImpl
import com.example.diaryApp.domain.repository.quiz.QuizRepository
import com.example.diaryApp.domain.repository.quiz.QuizRepositoryImpl
import com.example.diaryApp.domain.repository.user.UserRepository
import com.example.diaryApp.domain.repository.user.UserRepositoryImpl
import com.example.diaryApp.domain.services.AlarmService
import com.example.diaryApp.domain.services.CouponService
import com.example.diaryApp.domain.services.DiaryService
import com.example.diaryApp.domain.services.ProfileListService
import com.example.diaryApp.domain.services.QuizService
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

    @Provides
    fun provideCouponRepository(
        couponService : CouponService
    ) : CouponRepository = CouponRepositoryImpl(couponService)

    @Provides
    fun provideCouponService(retrofit: Retrofit): CouponService {
        return retrofit.create(CouponService::class.java)
    }

    @Provides
    fun provideDiaryRepository(
        diaryService: DiaryService
    ) : DiaryRepository = DiaryRepositoryImpl(diaryService)

    @Provides
    fun provideDiaryService(retrofit: Retrofit) : DiaryService {
        return retrofit.create(DiaryService::class.java)

    }
    @Provides
    fun provideQuizRepository(quizService: QuizService): QuizRepository {
        return QuizRepositoryImpl(quizService)
    }

    @Provides
    fun provideQuizService(retrofit: Retrofit): QuizService {
        return retrofit.create(QuizService::class.java)
    }

    @Provides
    fun provideAlarmRepository(alarmService: AlarmService): AlarmRepository {
        return AlarmRepositoryImpl(alarmService)
    }

    @Provides
    fun provideAlarmService(retrofit: Retrofit): AlarmService {
        return retrofit.create(AlarmService::class.java)
    }

}
