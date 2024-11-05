package com.example.diarytablet.di


import android.app.Application
import com.example.diarytablet.datastore.UserStore
import com.example.diarytablet.domain.RetrofitClient
import com.example.diarytablet.domain.repository.DiaryRepository
import com.example.diarytablet.domain.repository.DiaryRepositoryImpl
import com.example.diarytablet.domain.repository.ProfileListRepository
import com.example.diarytablet.domain.repository.ProfileListRepositoryImpl
import com.example.diarytablet.domain.repository.QuizRepository
import com.example.diarytablet.domain.repository.QuizRepositoryImpl
import com.example.diarytablet.domain.repository.UserRepository
import com.example.diarytablet.domain.repository.UserRepositoryImpl
<<<<<<< HEAD
import com.example.diarytablet.domain.service.DiaryService
=======
import com.example.diarytablet.domain.repository.WordRepository
import com.example.diarytablet.domain.repository.WordRepositoryImpl
>>>>>>> 1a83e2854da8034d5cecf68e5e995ad0baf1aee7
import com.example.diarytablet.domain.service.ProfileListService
import com.example.diarytablet.domain.service.QuizService
import com.example.diarytablet.domain.service.UserService
import com.example.diarytablet.domain.service.WordService
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
<<<<<<< HEAD

    @Provides
    fun provideDiaryRepository(
        diaryService: DiaryService,
    ): DiaryRepository {
        return DiaryRepositoryImpl(diaryService)
    }

    @Provides
    fun provideDiaryService(retrofit: Retrofit): DiaryService {
        return retrofit.create(DiaryService::class.java)
    }
=======
    @Provides
    fun provideWordService(
        retrofit: Retrofit
    ): WordService = retrofit.create(WordService::class.java)

    @Provides
    fun provideWordRepository(
        wordService: WordService
    ): WordRepository = WordRepositoryImpl(wordService)
>>>>>>> 1a83e2854da8034d5cecf68e5e995ad0baf1aee7
}
