package io.obolonsky.podcaster.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haroldadmin.cnradapter.invoke
import io.obolonsky.podcaster.api.BookApi
import io.obolonsky.podcaster.data.misc.BookPagingMapper
import io.obolonsky.podcaster.data.room.entities.Book
import java.lang.Exception

class BookSearchPagingSource(
    private val bookApi: BookApi,
    private val searchQuery: String,
) : PagingSource<Int, Book>() {

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: INITIAL_PAGE
        val offset = (page - INITIAL_PAGE) * params.loadSize

        return try {
            val response = bookApi.getSearchRange(
                searchQuery = searchQuery,
                offset = offset,
                limit = params.loadSize
            ).invoke() ?: emptyList()

            val books = response.map { BookPagingMapper.map(it) }
            toLoadResult(
                data = books,
                prevKey = getPrevKey(page),
                nextKey = getNextKey(page, books)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun getPrevKey(currentPage: Int): Int? {
        return if (currentPage == INITIAL_PAGE) null else currentPage - 1
    }

    private fun isEndOfPagination(data: List<*>) = data.isEmpty()

    private fun getNextKey(currentPage: Int, data: List<Book>): Int? {
        return if (isEndOfPagination(data)) null else currentPage + 1
    }

    private fun toLoadResult(
        data: List<Book>,
        prevKey: Int?,
        nextKey: Int?,
    ): LoadResult<Int, Book> {
        return LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = nextKey,
        )
    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}