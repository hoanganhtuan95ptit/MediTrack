package com.simple.meditrack.di

import com.simple.meditrack.data.repositories.AlarmRepositoryImpl
import com.simple.meditrack.domain.repositories.AlarmRepository
import org.koin.dsl.module

@JvmField
val repositoryModule = module {

    single<AlarmRepository> {
        AlarmRepositoryImpl()
    }
}