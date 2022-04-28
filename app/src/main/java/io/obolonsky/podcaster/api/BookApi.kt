package io.obolonsky.podcaster.api

import io.obolonsky.podcaster.data.responses.BookResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BookApi {

    @GET("bookInfo/bookDetails/{bookId}/{personId}")
    suspend fun getBookDetails(
        @Path("bookId") bookId: String,
        @Path("personId") personId: String,
    ): BookResponse
}