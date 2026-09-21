package org.vaulture.project.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.vaulture.project.core.network.ClimateService
import org.vaulture.project.core.network.MarketPriceService
import org.vaulture.project.core.util.KmpAudioPlayer
import org.vaulture.project.core.util.createAudioPlayer
import org.vaulture.project.data.remote.GeminiService
import org.vaulture.project.features.auth.data.AuthServiceImpl
import org.vaulture.project.features.auth.domain.AuthService
import org.vaulture.project.features.auth.presentation.LoginViewModel
import org.vaulture.project.features.home.data.repository.AgronomicActionRepositoryImpl
import org.vaulture.project.features.home.data.repository.AnalyticsRepositoryImpl
import org.vaulture.project.features.home.data.repository.CheckInRepositoryImpl
import org.vaulture.project.features.home.data.repository.RhythmRepositoryImpl
import org.vaulture.project.features.home.domain.repository.AgronomicActionRepository
import org.vaulture.project.features.home.domain.repository.AnalyticsRepository
import org.vaulture.project.features.home.domain.repository.CheckInRepository
import org.vaulture.project.features.home.domain.repository.RhythmRepository
import org.vaulture.project.features.home.domain.usecase.AnalyzeCheckInUseCase
import org.vaulture.project.features.home.domain.usecase.SubmitAgronomicActionUseCase
import org.vaulture.project.features.home.presentation.viewmodel.AnalyticsViewModel
import org.vaulture.project.features.home.presentation.viewmodel.CBTViewModel
import org.vaulture.project.features.home.presentation.viewmodel.CheckInViewModel
import org.vaulture.project.features.home.presentation.viewmodel.RhythmViewModel
import org.vaulture.project.features.space.data.repository.SpaceRepositoryImpl
import org.vaulture.project.features.space.domain.repository.SpaceRepository
import org.vaulture.project.features.space.domain.usecase.SpaceUseCases
import org.vaulture.project.features.space.presentation.viewmodel.SpaceViewModel
import org.vaulture.project.features.wellness.data.repository.WellnessRepositoryImpl
import org.vaulture.project.features.wellness.domain.repository.WellnessRepository
import org.vaulture.project.features.wellness.presentation.viewmodel.WellnessViewModel

val firebaseModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
}

val networkModule = module {
    single { GeminiService() }
    single { ClimateService() }
    single { MarketPriceService() }
    single<KmpAudioPlayer> { createAudioPlayer() }
}

val repositoryModule = module {
    single<AuthService> { AuthServiceImpl(get()) }
    single<SpaceRepository> { SpaceRepositoryImpl(get(), get(), get()) }
    single<CheckInRepository> { CheckInRepositoryImpl(get(), get()) }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl(get(), get(), get()) }
    single<AgronomicActionRepository> { AgronomicActionRepositoryImpl(get(), get()) }
    single<RhythmRepository> { RhythmRepositoryImpl(get()) }
    single<WellnessRepository> { WellnessRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    single { SpaceUseCases(get()) }
    single { AnalyzeCheckInUseCase(get(), get()) }
    single { SubmitAgronomicActionUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SpaceViewModel)
    viewModelOf(::CheckInViewModel)
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::CBTViewModel)
    viewModelOf(::RhythmViewModel)
    viewModelOf(::WellnessViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        firebaseModule,
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}
