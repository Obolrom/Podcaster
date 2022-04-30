package io.obolonsky.podcaster.data.misc

import io.obolonsky.podcaster.data.responses.BookDetailsResponse
import io.obolonsky.podcaster.data.responses.BookPagingResponse
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.BookAuthor
import io.obolonsky.podcaster.data.room.entities.Chapter
import io.obolonsky.podcaster.data.room.entities.VoiceOverAuthor

interface Mapper<I : Any, O : Any> {

    fun map(input: I): O
}

object BookMapper : Mapper<BookDetailsResponse, Book> {

    override fun map(input: BookDetailsResponse): Book {
        return Book(
            id = input.id,
            title = input.bookTitle,
            imageUrl = input.imageUrl,
            category = input.category,
            duration = input.duration,
            bookAuthor = BookAuthorMapper.map(input.bookAuthor),
            voiceOverAuthor = VoiceOverAuthorMapper.map(input.voiceOverAuthor),
            raiting = input.raiting,
            description = input.description,
            auditionsCount = input.auditionsCount,
            lastChapter = input.lastChapter?.let { ChapterMapper.map(it) },
            chapters = input.chapters.map { ChapterMapper.map(it) }
        )
    }
}

object BookPagingMapper : Mapper<BookPagingResponse, Book> {

    override fun map(input: BookPagingResponse): Book {
        return Book(
            id = input.id,
            title = input.title,
            imageUrl = input.imageUrl,
            raiting = input.raiting,
            description = input.description,
            chapters = emptyList(),
            category = null,
            duration = null,
            bookAuthor = null,
            voiceOverAuthor = null,
            auditionsCount = null,
            lastChapter = null
        )
    }
}

object BookAuthorMapper : Mapper<BookDetailsResponse.BookAuthorResponse, BookAuthor> {

    override fun map(input: BookDetailsResponse.BookAuthorResponse): BookAuthor {
        return BookAuthor(
            id = input.id,
            firstName = input.firstName,
            lastName = input.lastName,
            raiting = input.raiting,
        )
    }
}

object VoiceOverAuthorMapper : Mapper<BookDetailsResponse.VoiceOverAuthorResponse, VoiceOverAuthor> {

    override fun map(input: BookDetailsResponse.VoiceOverAuthorResponse): VoiceOverAuthor {
        return VoiceOverAuthor(
            id = input.id,
            firstName = input.firstName,
            lastName = input.lastName,
            userName = input.userName,
            email = input.email,
            raiting = input.raiting,
        )
    }
}

object ChapterMapper : Mapper<BookDetailsResponse.ChapterResponse, Chapter> {

    override fun map(input: BookDetailsResponse.ChapterResponse): Chapter {
        return Chapter(
            id = input.id,
            bookId = input.bookId,
            title = input.chapterName,
            imageUrl = input.imageUrl,
            mediaUrl = input.mediaUrl,
            duration = input.duration,
            lastTimeStamp = input.lastTimeStamp,
        )
    }
}