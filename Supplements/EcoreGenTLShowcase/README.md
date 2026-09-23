# Introduction

This folder contains the binary of EcoreGenTL and a showcase (a simple library management system).

The binary of EcoreGenTL is placed at the root of this folder (`EcoreGenTL.jar`).

The file `Prompt_Templates.md` demonstrates the LLM prompts used in EcoreGenTL.

The class diagram of this system is stored in `model/Library.ecore`.

The requirement is stored as an EAnnotation of the EPackage in Library.ecore. For reviewer's convenience, the requirement is also enclosed below.

```
**System Requirements – Library Lending System (Simplified)**

The system manages book inventory, member records, and loan transactions for a small library. Each book stores title, author, and available copy count. Each member has a name, a unique ID, and a list of active loans. Each loan links one book to one member, records the borrow date, a due date (14 days later), and an optional return date (null while active).

**Core operations with precondition checks:**

- **Borrow a book** – Succeeds only if (1) the book has at least one copy in stock, (2) the member has fewer than 5 active loans, and (3) the member does not already have an active loan for that same book. If all checks pass, a new loan is created, stock is decreased by one, and the operation returns true; otherwise false.

- **Return a book** – Locates the active loan (return date is null) for that member and book. If found, it sets the return date to today, increases stock by one, and automatically triggers fine calculation. Returns false if no matching active loan exists.

- **Fine calculation** – Computes the overdue fee based on the difference between the actual return date (or today if still outstanding) and the due date, at a fixed rate of $0.50 per day. Only positive differences incur a charge; result is a double.

- **Extend due date** – Allowed only if (1) the loan is not yet returned, (2) the loan is not already overdue, and (3) the loan has not been extended before (maximum one extension per loan). If permitted, the due date is increased by the requested number of days and true is returned; otherwise false.

**Supporting query methods** – Each loan can report whether it has been returned or is overdue. Members can query their current borrowed count and whether they are eligible to borrow more (respecting the 5‑item limit and any block due to excessive overdue items, e.g., more than 30 days overdue).

**Additional rules** – Stock adjustments are atomic. Members with a loan overdue beyond 30 days are automatically blocked from further borrowing until the item is returned (enforced via the eligibility check). All operations provide clear success/failure feedback and ensure consistent enforcement of library policies.
``` 

# Configuration

Before running this showcase, you must edit two configuration files:

- `src/conf.properties` for LLM. You must set the url, model name, and the API key for your LLM.

- `src/GenLibrary.mwe2` for paths. You must set `rootPath` and `ecoreFile`. For instance, if this folder is at `c:/a/b/c/EcoreGenTLShowcase`, set `rootPath` to `"c:/a/b/c"`. You must update `ecoreFile` accordingly.

# Run Showcase

When you finished the configuration, you may execute EcoreGenTL via console. First, you should be at the project folder, e.g., `c:/a/b/c/EcoreGenTLShowcase/`. Second, you should execute the following command:

```
> java -jar EcoreGenTL.jar -mwe "c:/a/b/c/EcoreGenTLShowcase/src/GenLibrary.mwe2"
```

Use the absolute path here.