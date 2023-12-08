package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.exception.BookAlreadyReservedException
import com.jicay.bookmanagement.domain.exception.BookNotFoundException
import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBook(bookName: String) {
        val books = bookPort.getAllBooks()

        for (book in books) {
            if (book.name == bookName) {
                if (book.reserved) {
                    throw BookAlreadyReservedException("The book '$bookName' is already reserved.")
                } else {
                    book.reserved = true// Suppose que le port de réservation prend un ID plutôt qu'un nom
                    return
                }
            }
        }

        throw BookNotFoundException("Book with name '$bookName' not found.")
    }
}