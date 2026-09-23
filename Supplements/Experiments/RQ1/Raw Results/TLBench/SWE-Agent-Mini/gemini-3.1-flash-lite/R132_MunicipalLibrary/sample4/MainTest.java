import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        Library library = new Library();
        library.setName("City Library");

        // 1. Register members
        boolean registered = library.registerMember("John", "Doe");
        System.out.println("Registered John: " + registered);
        boolean registeredDuplicate = library.registerMember("John", "Doe");
        System.out.println("Registered John again: " + registeredDuplicate);

        // Add a book
        Book book = new Book();
        book.setTitle("Java Programming");
        book.setBookId("B001");
        library.addDocument(book);

        // Find the member
        Member john = null;
        for (Member m : library.getMembers()) {
            if (m.getFirstName().equals("John")) john = m;
        }

        // 2. Borrow a book
        Date today = new Date();
        boolean borrowed = john.borrowBook("Java Programming", today);
        System.out.println("Borrowed book: " + borrowed);

        // List books
        List<String> titles = john.listBorrowedBookTitles();
        System.out.println("Borrowed titles: " + titles);

        // Attempt second borrow (only 3 allowed)
        Book book2 = new Book();
        book2.setTitle("Design Patterns");
        book2.setBookId("B002");
        library.addDocument(book2);
        boolean borrowed2 = john.borrowBook("Design Patterns", today);
        System.out.println("Borrowed second book: " + borrowed2);

        // 3. Extend due date
        Date newDue = john.extendReturnDueDate("Java Programming", today);
        System.out.println("Extension date: " + newDue);

        // 4. Return book
        boolean returned = john.returnBook("Java Programming", today);
        System.out.println("Returned book: " + returned);
        
        System.out.println("Titles after return: " + john.listBorrowedBookTitles());
    }
}
