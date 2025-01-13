package com.simple.meditrack.domain.repositories

import com.simple.meditrack.entities.KeyTranslate
import kotlinx.coroutines.flow.Flow

interface AppRepository {

    suspend fun getLanguageAsync(): Flow<String>

    suspend fun getKeyTranslate(langCode: String): List<KeyTranslate>

    suspend fun getKeyTranslateAsync(langCode: String): Flow<List<KeyTranslate>>

    suspend fun updateKeyTranslate(list: List<KeyTranslate>)

    suspend fun getKeyTranslateDefault(): Map<String, String>
}