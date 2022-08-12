package io.obolonsky.podcaster.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.podcaster.data.responses.BookDetailsResponse
import io.obolonsky.podcaster.data.responses.BookPagingResponse
import io.obolonsky.podcaster.data.responses.BookProgressRequest
import io.obolonsky.podcaster.data.responses.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookApi {

    @GET("bookInfo/bookDetails/{$BOOK_ID}")
    suspend fun getBookDetails(
        @Path(BOOK_ID) bookId: String,
    ): NetworkResponse<BookDetailsResponse, Unit>

    @GET("bookInfo/books/{$OFFSET}/{$LIMIT}")
    suspend fun getBookRange(
        @Path(OFFSET) offset: Int,
        @Path(LIMIT) limit: Int,
    ): NetworkResponse<List<BookPagingResponse>, Unit>

    @GET("search-filter/global/{$SEARCH_QUERY}/{$OFFSET}/{$LIMIT}")
    suspend fun getSearchRange(
        @Path(SEARCH_QUERY) searchQuery: String,
        @Path(OFFSET) offset: Int,
        @Path(LIMIT) limit: Int,
    ): NetworkResponse<List<BookPagingResponse>, Unit>

    @GET("user-book/user-book-library")
    suspend fun getUserBookLibrary(): NetworkResponse<List<BookPagingResponse>, Unit>

    @GET("user/get-user-info")
    suspend fun getUserProfile(): NetworkResponse<UserProfileResponse, Unit>

    @POST("user-book/book-progress")
    suspend fun postProgress(
        @Body bookProgressRequest: BookProgressRequest,
    ): NetworkResponse<Unit, Unit>
}

private const val BOOK_ID = "bookId"
private const val OFFSET = "offset"
private const val LIMIT = "limit"
private const val SEARCH_QUERY = "search_query"