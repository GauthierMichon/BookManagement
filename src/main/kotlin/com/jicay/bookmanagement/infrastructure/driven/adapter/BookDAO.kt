package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    reserved = rs.getBoolean("reserved")
                )
            }
    }

    override fun createBook(book: Book) {
        namedParameterJdbcTemplate
            .update("INSERT INTO BOOK (title, author) values (:title, :author)", mapOf(
                "title" to book.name,
                "author" to book.author
            ))
    }

    override fun reserveBook(bookId: Long): Boolean {
        val reserved = isBookReserved(bookId)
        if (!reserved) {
            namedParameterJdbcTemplate.update(
                    "UPDATE BOOK SET reserved = true WHERE id = :id",
                    mapOf("id" to bookId)
            )
        }
        return !reserved
    }

    override fun isBookReserved(bookId: Long): Boolean {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT reserved FROM BOOK WHERE id = :id",
                mapOf("id" to bookId),
                Boolean::class.java
        ) ?: false
    }
}