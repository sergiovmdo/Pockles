package com.pes.pockles.data.api

import com.google.android.gms.maps.model.LatLng
import com.pes.pockles.model.*
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {

    /*
    To create a new API call, just put something like this:

    @METHOD("path")
    fun methodName(variables): Single<ReturnType>

    - METHOD        -> must be GET, PUT, POST, ...
    - path          -> path of the endpoint without the initial slash (/)
    - methodName    -> whatever you want
    - variables     -> whatever you need. If you need an endpoint with a variable on the path, put
                       a variable with @Path("namePath") variableName: typeVariable and in the path
                       put {namePath} where namePath could be whatever, they will link automatically.
                       Example: https://github.com/VictorBG/Raco/blob/master/app/src/main/java/com/victorbg/racofib/data/api/ApiService.java#L63-L65
    - ReturnType    -> The type of the object the API returns, it can be a List (List<ReturnType)

     */
    @GET("user/history")
    fun pocksHistory(): Single<List<Pock>>

    @POST("pock")
    fun newPock(@Body pock: NewPock): Single<Pock>

    @GET("pock")
    fun getNearPocks(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Single<List<Pock>>

    @GET("pock/{id}")
    fun viewPock(@Path("id") id: String): Single<Pock>

    @PATCH("pock/{id}")
    fun editPock(@Path("id") id: String, @Body pock: EditedPock): Single<Pock>

    @GET("user")
    fun getUser(): Single<User>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: String): Single<ViewUser>

    @GET("user/{id}/exists")
    fun userExists(@Path("id") id: String): Single<Boolean>

    @POST("user")
    fun createUser(@Body createUser: CreateUser): Single<User>

    @GET("pock/all/locations")
    fun pocksLocation(): Single<List<LatLng>>

    @POST("pock/{id}/like")
    fun like(@Path("id") id: String): Single<Pock>

    @DELETE("pock/{id}/like")
    fun undoLike(@Path("id") id: String): Single<Pock>

    @GET("user/likes")
    fun likedPocks(): Single<List<Pock>>

    @PUT("user/token")
    fun insertFCMToken(@Body insertToken: InsertToken): Single<Boolean>

    @POST("chat/message")
    fun newMessage(@Body message: NewMessage): Single<Message>

    @GET("chat")
    fun allChats(): Single<List<Chat>>

    @GET("chat/{id}")
    fun allMessageChat(@Path("id") id: String, @Query("fromPock") fromPock: Boolean = false): Single<List<Message>>

    @GET("notifications")
    fun notifications(): Single<List<Notification>>

    @PATCH("user")
    fun editProfile(@Body editProfile: EditedUser): Single<User>

    @POST("pock/{id}/report")
    fun report(@Path("id") id: String, @Body motive: ReportObject): Single<Pock>

    @GET("achievement")
    fun getAchievements(): Single<List<Achievement>>
}