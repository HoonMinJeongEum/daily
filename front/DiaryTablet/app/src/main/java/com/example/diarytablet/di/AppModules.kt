package com.example.diarytablet.di

import android.app.Application
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.repository.*
import com.example.diarytablet.domain.service.*
import com.example.diarytablet.domain.repository.DiaryRepository
import com.example.diarytablet.domain.repository.DiaryRepositoryImpl
import com.example.diarytablet.domain.repository.MainScreenRepository
import com.example.diarytablet.domain.repository.MainScreenRepositoryImpl
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.domain.repository.ProfileListRepositoryImpl
import com.example.diarytablet.domain.repository.QuizRepository
import com.example.diarytablet.domain.repository.QuizRepositoryImpl
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.domain.repository.UserRepositoryImpl
import com.example.diarytablet.domain.service.DiaryService
import com.example.diarytablet.domain.repository.WordRepository
import com.example.diarytablet.domain.repository.WordRepositoryImpl
import com.example.diarytablet.domain.service.MainScreenService
import com.example.diarytablet.domain.service.AlarmService
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.domain.service.QuizService
import com.example.diarytablet.domain.service.UserService
import com.example.diarytablet.domain.service.WordService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Singleton
    fun provideUserStore(application: Application): UserStore {
        return UserStore(application)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitClient.getInstance()

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService,
        userStore: UserStore
    ): UserRepository = UserRepositoryImpl(userService, userStore)

    @Provides
    @Singleton
    fun provideProfileListService(retrofit: Retrofit): ProfileListService {
        return retrofit.create(ProfileListService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileListRepository(
        profileListService: ProfileListService
    ): ProfileListRepository = ProfileListRepositoryImpl(profileListService)

    @Provides
    @Singleton
    fun provideQuizService(retrofit: Retrofit): QuizService {
        return retrofit.create(QuizService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        quizService: QuizService
    ): QuizRepository = QuizRepositoryImpl(quizService)

    @Provides
    @Singleton
    fun provideDiaryService(retrofit: Retrofit): DiaryService {
        return retrofit.create(DiaryService::class.java)
    }


    @Provides
    @Singleton
    fun provideDiaryRepository(
        diaryService: DiaryService
    ): DiaryRepository = DiaryRepositoryImpl(diaryService)

    @Provides
    @Singleton
    fun provideAlarmService(retrofit: Retrofit): AlarmService {
        return retrofit.create(AlarmService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(
        alarmService: AlarmService
    ): AlarmRepository = AlarmRepositoryImpl(alarmService)

    @Provides
    @Singleton
    fun provideWordService(retrofit: Retrofit): WordService {
        return retrofit.create(WordService::class.java)
    }

    @Provides
    @Singleton
    fun provideWordRepository(
        wordService: WordService
    ): WordRepository = WordRepositoryImpl(wordService)

    @Provides
    @Singleton
    fun provideShopStockService(retrofit: Retrofit): ShopStockService {
        return retrofit.create(ShopStockService::class.java)
    }

    @Provides
    @Singleton
    fun provideShopStockRepository(
        shopStockService: ShopStockService
    ): ShopStockRepository = ShopStockRepository(shopStockService)
    @Provides
    @Singleton
    fun provideMainScreenRepository(
        mainScreenService: MainScreenService
    ): MainScreenRepository = MainScreenRepositoryImpl(mainScreenService)

    @Provides
    @Singleton
    fun provideMainScreenService(
        retrofit: Retrofit
    ):MainScreenService = retrofit.create((MainScreenService::class.java))


    @Provides
    @Singleton
    fun provideQuestService(retrofit: Retrofit): QuestService {
        return retrofit.create(QuestService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestRepository(
        questService: QuestService
    ): QuestRepository = QuestRepositoryImpl(questService)

}
