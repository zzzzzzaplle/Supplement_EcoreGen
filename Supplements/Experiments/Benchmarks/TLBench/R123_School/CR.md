// ==version1==
```
1. Add a course. The school can add a new course by providing a case-sensitive unique ID. The operation returns true on success; otherwise, false.

2. Assign a course to a sector. A course can belong to exactly one sector at a time. When assigning a course to a new sector, the system must first remove it from its current sector (if any) before adding it to the new one. Return true if successful; otherwise, false.

3. Manage the session schedule of a cours. For any course, the system can add or cancel sessions by providing a specific date (format: yyyy-MM-dd). When adding, ensure only one session per course per day.  The operation returns the course's up-to-date list of sessions. When canceling, only sessions without participants may be removed. The operation returns true on success; otherwise, it returns false.

4. Assign or replace a trainer for a session. Before the session's start date, the system can designate a trainer to lead a given session, or replace the trainer. The operation returns a boolean value indicating success.

5. Register a participant for a session. A participant may enroll in any session by providing a date and course ID. The call adds the participant to the specified session, ensuring no duplicate enrollments for the same participant. It returns true on success; otherwise, false.
```
// ==end==
