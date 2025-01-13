package com.simple.meditrack.domain.usecases

import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.domain.repositories.AppRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

class GetKeyTranslateAsyncUseCase(
    private val appRepository: AppRepository,
) {

    suspend fun execute(): Flow<Map<String, String>> = channelFlow {

        // call api để lấy bản dịch
        appRepository.getLanguageAsync().distinctUntilChanged().map {

            val list = appRepository.getKeyTranslate(it)

            appRepository.updateKeyTranslate(list)
        }.launchIn(this)


        // lấy bản dịch hiện có
        appRepository.getLanguageAsync().flatMapLatest {

            getKeyTranslateAsync(langCode = it)
        }.launchCollect(this) {

            trySend(it)
        }

        awaitClose {

        }
    }

    private suspend fun getKeyTranslateAsync(langCode: String) = appRepository.getKeyTranslateAsync(langCode).map { keyTranslates ->

        val map = KeyTranslateMap()

        if (keyTranslates.isNotEmpty()) keyTranslates.forEach {

            map[it.key] = it.value
        } else {

            map.putAll(appRepository.getKeyTranslateDefault())
        }

        map
    }

    private class KeyTranslateMap : HashMap<String, String>() {

        override fun get(key: String): String {
            return super.get(key) ?: key
        }
    }

    class Param()
}