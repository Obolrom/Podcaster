package io.obolonsky.podcaster.data.misc

import io.obolonsky.podcaster.data.responses.BookResponse
import io.obolonsky.podcaster.data.room.entities.Book
import io.obolonsky.podcaster.data.room.entities.BookAuthor
import io.obolonsky.podcaster.data.room.entities.Chapter
import io.obolonsky.podcaster.data.room.entities.VoiceOverAuthor

interface Mapper<I : Any, O : Any> {

    fun map(input: I): O
}

object BookMapper : Mapper<BookResponse, Book> {

    override fun map(input: BookResponse): Book {
        return Book(
            id = input.id,
            bookTitle = input.bookTitle,
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

object BookAuthorMapper : Mapper<BookResponse.BookAuthorResponse, BookAuthor> {

    override fun map(input: BookResponse.BookAuthorResponse): BookAuthor {
        return BookAuthor(
            id = input.id,
            firstName = input.firstName,
            lastName = input.lastName,
            raiting = input.raiting,
        )
    }
}

object VoiceOverAuthorMapper : Mapper<BookResponse.VoiceOverAuthorResponse, VoiceOverAuthor> {

    override fun map(input: BookResponse.VoiceOverAuthorResponse): VoiceOverAuthor {
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

object ChapterMapper : Mapper<BookResponse.ChapterResponse, Chapter> {

    override fun map(input: BookResponse.ChapterResponse): Chapter {
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