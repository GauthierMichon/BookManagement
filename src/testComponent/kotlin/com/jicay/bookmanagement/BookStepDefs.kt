package com.jicay.bookmanagement

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java.Before
import io.cucumber.java.Scenario
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

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}
