package com.simple.meditrack.data.repositories

import com.simple.meditrack.DEFAULT_TRANSLATE
import com.simple.meditrack.data.api.Api
import com.simple.meditrack.data.dao.KeyTranslateDao
import com.simple.meditrack.domain.repositories.AppRepository
import com.simple.meditrack.entities.KeyTranslate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.util.Locale

class AppRepositoryImpl(
    private val api: Api,
    private val keyTranslateDao: KeyTranslateDao,
) : AppRepository {

    override suspend fun getLanguageAsync(): Flow<String> = channelFlow {

        trySend(Locale.getDefault().language)

        awaitClose {

        }
    }

    override suspend fun getKeyTranslate(langCode: String): List<KeyTranslate> {

        return api.syncTranslate(langCode).map {

            KeyTranslate(
                key = it.key,
                value = it.value,
                langCode = langCode
            )
        }
    }

    override suspend fun getKeyTranslateAsync(langCode: String): Flow<List<KeyTranslate>> {

        return keyTranslateDao.getAllAsync(langCode).map {

            it.map {
                KeyTranslate(it.key, it.value, it.langCode)
            }
        }
    }

    override suspend fun updateKeyTranslate(list: List<KeyTranslate>) {

        keyTranslateDao.insert(*list.toTypedArray())
    }

    override suspend fun getKeyTranslateDefault(): Map<String, String> {

        return DEFAULT_TRANSLATE
    }
}