package com.jicay.bookmanagement.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
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

    /*@Test
    fun `reserve book should reserve the book and return true if not reserved before`() {
        val bookName = "Les Misérables"
        every { bookPort.isBookReserved(bookName) } returns false
        justRun { bookPort.reserveBook(bookName) }

        val result = bookUseCase.reserveBook(bookName)

        assertThat(result).isEqualTo(true)
        verify(exactly = 1) { bookPort.isBookReserved(bookName) }
        verify(exactly = 1) { bookPort.reserveBook(bookName) }
    }

    @Test
    fun `reserve book should not reserve the book and return false if already reserved`() {
        val bookName = "Les Misérables"
        every { bookPort.isBookReserved(bookName) } returns true

        val result = bookUseCase.reserveBook(bookName)

        assertThat(result).isEqualTo(false)
        verify(exactly = 1) { bookPort.isBookReserved(bookName) }
        verify(exactly = 0) { bookPort.reserveBook(any()) }
    }*/

}