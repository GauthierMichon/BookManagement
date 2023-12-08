Feature: the user can create and retrieve the books, and reserve book
  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo" with reserved false
    And the user creates the book "L'avare" written by "Molière" with reserved false
    And the user get all books
    Then the list should contains the following books in the same order
      | name | author | reserved |
      | L'avare | Molière | false |
      | Les Misérables | Victor Hugo | false |


  Scenario: User can reserve a book
    Given there is a book with name "Harry Potter" written by "J.K. Rowling" and not reserved
    When the user reserves the book with name "Harry Potter"
    Then the book with name "Harry Potter" should be reserved