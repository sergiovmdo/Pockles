package com.pes.pockles.data.api

import com.pes.pockles.data.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    companion object {
        const val AUTH_HEADER_NAME = "Authorization"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code() == 401) {
            tokenManager.refreshTokenSync().await()
        }
        return response
            .request()
            .newBuilder()
            .header(AUTH_HEADER_NAME, wrapToken())
            .build()
    }

    fun wrapToken(token: String? = tokenManager.token): String {
        return "Bearer $token"
    }

}
