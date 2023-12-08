package com.jicay.bookmanagement

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @When("the user creates the book {string} written by {string} with reserved {boolean}")
    fun createBook(title: String, author: String, reserved: Boolean) {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(
                        """
                    {
                      "name": "$title",
                      "author": "$author",
                      "reserved": $reserved
                    }
                """.trimIndent()
                )
                .`when`()
                .post("/books")
                .then()
                .statusCode(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastBookResult = given()
                .`when`()
                .get("/books")
                .then()
                .statusCode(200)
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        val expectedResponse = payload.joinToString(separator = ",", prefix = "[", postfix = "]") { line ->
            """
                ${
                line.entries.joinToString(separator = ",", prefix = "{", postfix = "}") {
                    """"${it.key}": ${if (it.key == "reserved") it.value.toString() else "\"${it.value}\""}"""
                }
            }
            """.trimIndent()
        }
        assertThat(lastBookResult.extract().body().jsonPath().prettify())
                .isEqualTo(JsonPath(expectedResponse).prettify())
    }

    @Given("there is a book with name {string} written by {string} and not reserved")
    fun createUnreservedBook(title: String, author: String) {
        given()
                .contentType(ContentType.JSON)
                .and()
                .body(
                        """
                {
                  "name": "$title",
                  "author": "$author",
                  "reserved": false
                }
                """.trimIndent()
                )
                .`when`()
                .post("/books")
                .then()
                .statusCode(201)
    }

    @When("the user reserves the book with name {string}")
    fun reserveBook(bookName: String) {
        val response = given()
                .`when`()
                .post("/books/$bookName/reserve")
                .then()
                .statusCode(200)
                .extract().response().asString()
        println("Response from /books/$bookName/reserve: $response")
    }

    @Then("the book with name {string} should be reserved")
    fun checkBookReserved(bookName: String) {
        lastBookResult = given()
                .`when`()
                .get("/books")
                .then()
                .statusCode(200)

        val books = lastBookResult.extract().body().jsonPath().getList("", Map::class.java)

        // Find the book with the specified name
        val book = books.firstOrNull { it["name"] == bookName }

        // Assertions
        assertThat(book).isNotNull()
        assertThat(book!!["reserved"]).isEqualTo(true)
    }


    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}
