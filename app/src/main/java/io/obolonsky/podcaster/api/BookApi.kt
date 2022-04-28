package io.obolonsky.podcaster.api

import io.obolonsky.podcaster.data.responses.BookDetailsResponse
import io.obolonsky.podcaster.data.responses.BookPagingResponse
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookApi {

    @GET("bookInfo/bookDetails/{$BOOK_ID}/{$PERSON_ID}")
    suspend fun getBookDetails(
        @Path(BOOK_ID) bookId: String,
        @Path(PERSON_ID) personId: String,
    ): Response<BookDetailsResponse>

    @GET("bookInfo/books/{$OFFSET}/{$LIMIT}")
    suspend fun getBookRange(
        @Path(OFFSET) offset: Int,
        @Path(LIMIT) limit: Int,
    ): Response<List<BookPagingResponse>>

    @POST("bookInfo/book-progress")
    suspend fun postProgress(@Body bookProgressRequest: BookProgressRequest)
}

private const val BOOK_ID = "bookId"
private const val PERSON_ID = "personId"
private const val OFFSET = "offset"
private const val LIMIT = "limit"