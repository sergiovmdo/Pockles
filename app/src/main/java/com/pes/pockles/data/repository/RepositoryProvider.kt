package com.pes.pockles.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class RepositoryProvider @Inject constructor(
    val userRepository: UserRepository,
    val chatRepository: ChatRepository,
    val pockRepository: PockRepository
)