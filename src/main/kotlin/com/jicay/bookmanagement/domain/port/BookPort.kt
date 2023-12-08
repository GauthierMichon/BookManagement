package com.jicay.bookmanagement.domain.port

import com.jicay.bookmanagement.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
    fun reserveBook(bookName: String)
    fun isBookReserved(bookId: String): Boolean
}