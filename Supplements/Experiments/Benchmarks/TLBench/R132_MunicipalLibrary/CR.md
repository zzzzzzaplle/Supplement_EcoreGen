// ==version1==
1. Register a new member. The library registers a new member from a first name and a surname. The first name and surname must not be null, empty, or whitespace-only. The library rejects a registration when another member already has exactly the same full name. A successful registration creates the member with no borrowed books and returns true. Any rejected registration returns false.

2. Borrow a book and create a record in the system. A member borrows a document by title. The library accepts the request only when the title exists, the document is a book, the book is not already borrowed by any member, and the member currently holds fewer than three books. A successful borrowing creates a borrow record with today's date as the borrowing date and a due date exactly seven days later, then returns true. Any rejected borrowing returns false.

3. Extend a borrowed record's return due date. A member may extend the due date of one of their own borrowed books by title. When today is on or after the borrowing date and strictly before the current due date, the library moves the due date forward by seven days and returns the new due date. Otherwise, the due date remains unchanged and the original due date is returned.

4. Return a book. A member returns a borrowed book by title. If the member does not currently hold that title, the operation returns false. If the borrowed book is overdue, the return is rejected, the borrow record remains active, and the operation returns false. Otherwise the borrow record is removed and the operation returns true.

5. List all borrowed book titles for a member. A member can list the titles of all currently borrowed books. If the member has no active borrowed books, the operation returns an empty list.
// ==end==
