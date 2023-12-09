package com.jicay.bookmanagement.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.messageContains
import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BookDTOUseCaseTest {

    @InjectMockKs
    private lateinit var bookUseCase: BookUseCase

    @MockK
    private lateinit var bookPort: BookPort

    @Test
    fun `get all books should returns all books sorted by name`() {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo", false),
            Book("Hamlet", "William Shakespeare", false)
        )

        val res = bookUseCase.getAllBooks()

        assertThat(res).containsExactly(
            Book("Hamlet", "William Shakespeare", false),
            Book("Les Misérables", "Victor Hugo", false)
        )
    }

    @Test
    fun `add book`() {
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo", false)

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    @Test
    fun `reserve book`() {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo", false),
            Book("Hamlet", "William Shakespeare", false)
        )
        val bookName = "Les Misérables"

        bookUseCase.reserveBook(bookName)

        val res = bookUseCase.getAllBooks()
        assertThat(res).containsExactly(
                Book("Hamlet", "William Shakespeare", false),
                Book("Les Misérables", "Victor Hugo", true)
        )

    }

    @Test
    fun `reserve book when already reserved should throw BookAlreadyReservedException`() {
        every { bookPort.getAllBooks() } returns listOf(
                Book("Les Misérables", "Victor Hugo", false),
                Book("Hamlet", "William Shakespeare", false)
        )
        val bookName = "Les Misérables"

        bookUseCase.reserveBook(bookName)

        assertThat {
            bookUseCase.reserveBook(bookName)
        }.isFailure().messageContains("The book '$bookName' is already reserved.")
    }

    @Test
    fun `reserve book when book is not found should throw BookNotFoundException`() {
        every { bookPort.getAllBooks() } returns listOf(
                Book("Les Misérables", "Victor Hugo", false),
                Book("Hamlet", "William Shakespeare", false)
        )
        val bookName = "Harry Potter"

        assertThat {
            bookUseCase.reserveBook(bookName)
        }.isFailure().messageContains("Book with name '$bookName' not found.")
    }
}