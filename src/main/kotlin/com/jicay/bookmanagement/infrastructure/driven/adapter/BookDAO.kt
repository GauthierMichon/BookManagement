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

    override fun reserveBook(bookName: String): Boolean {
        val reserved = isBookReserved(bookName)
        if (!reserved) {
            val sql = "UPDATE BOOK SET reserved = true WHERE title = :title"
            val params = mapOf("title" to bookName)
            namedParameterJdbcTemplate.update(sql, params)
        }
        return !reserved
    }

    override fun isBookReserved(bookName: String): Boolean {
        val sql = "SELECT reserved FROM BOOK WHERE title = :title"
        val params = mapOf("title" to bookName)
        return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean::class.java) ?: false
    }

}