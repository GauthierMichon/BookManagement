package com.jicay.bookmanagement.infrastructure.driver.web

import com.jicay.bookmanagement.domain.exception.BookNotFoundException
import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler

@ExtendWith(SpringExtension::class)
@WebMvcTest
class BookControllerIT {

    @MockkBean
    private lateinit var bookUseCase: BookUseCase

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `rest route get books`() {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(Book("A", "B", false))

        // WHEN
        mockMvc.get("/books")
        //THEN
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content { json(
                    // language=json
                    """
                        [
                          {
                            "name": "A",
                            "author": "B",
                            "reserved": false
                          }
                        ]
                    """.trimIndent()
                ) }
            }
    }

    @Test
    fun `rest route post book`() {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "name": "Les misérables",
                  "author": "Victor Hugo",
                  "reserved": false
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        val expected = Book(
            name = "Les misérables",
            author = "Victor Hugo",
            reserved = false
        )

        verify(exactly = 1) { bookUseCase.addBook(expected) }
    }

    @Test
    fun `rest route post book should return 400 when body is not good`() {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "title": "Les misérables",
                  "author": "Victor Hugo",
                  "reserved": false
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { bookUseCase.addBook(any()) }
    }

    @Test
    fun `rest route reserve book`() {
        // GIVEN
        val bookName = "Les Misérables"
        justRun { bookUseCase.reserveBook(bookName) }

        // WHEN
        mockMvc.post("/books/$bookName/reserve") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        // THEN
        verify(exactly = 1) { bookUseCase.reserveBook(bookName) }
    }

    /*@Test
    fun `rest route reserve book should return 404 when book not found`() {
        // GIVEN
        val bookName = "Nonexistent Book"
        every { bookUseCase.reserveBook(bookName) } throws BookNotFoundException("Book with name '$bookName' not found.")

        // WHEN
        mockMvc.post("/books/$bookName/reserve") {
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Book with name '$bookName' not found.") }
        }

        // THEN
        verify(exactly = 1) { bookUseCase.reserveBook(bookName) }
    }*/


}