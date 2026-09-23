// ==version1==
```plantuml
class School {
  - String name
  - List<Course> courses
  - List<Sector> sectors
  + School(String name)
  + String getName()
  + void setName(String name)
  + List<Course> getCourses()
  + List<Sector> getSectors()
  + boolean addCourse(String id)
  + boolean assignCourseToSector(String courseId, String sectorId)
  + Sector addSector(String id)
}
class Course {
  - String id
  - List<Document> documents
  - Sector sector
  - List<Session> sessions
  + Course(String id)
  + String getId()
  + void setId(String id)
  + List<Document> getDocuments()
  + void setDocuments(List<Document> docs)
  + Sector getSector()
  + void setSector(Sector s)
  + List<Session> getSessions()
  + List<Session> addSession(LocalDate date)
  + boolean cancelSession(LocalDate date)
  + boolean addDocument(Document doc)
  + boolean removeDocument(Document doc)
}
class Session {
  - LocalDate date
  - List<Participant> registeredParticipants
  - Trainer trainer
  + Session(LocalDate date)
  + LocalDate getDate()
  + void setDate(LocalDate date)
  + Trainer getTrainer()
  + List<Participant> getParticipants()
  + boolean assignTrainer(Trainer t, LocalDate today)
  + boolean replaceTrainer(Trainer newTrainer, LocalDate today)
  + boolean registerParticipant(Participant p, LocalDate today)
}

class Sector {
  - String id
  - List<Course> courses
  + Sector(String id)
  + String getId()
  + void setId(String id)
  + List<Course> getCourses()
  ~ void addCourse(Course course)
  ~ void removeCourse(Course course)
}
class Document {
  - String name
  + Document(String name)
  + String getName()
  + void setName(String name)
  + boolean equals(Object o)
  + int hashCode()
}
abstract class Person {
    - String id
    + Person(String id)
    + String getId()
    + void setId(String id)
}

class Participant extends Person {
    + Participant(String id)
}
class Trainer extends Person {
    - boolean contractor
    + Trainer(String id, boolean contractor)
    + boolean isContractor()
    + void setContractor(boolean contractor)
}

School *-- "*" Course : courses
School *-- "*" Sector : sectors
Course *-- "*" Session : sessions
Course --> "1" Sector : sector
Sector --> "*" Course : courses
Course --> "*" Document : documents
Session -- "1" Trainer : trainer
Session --> "*" Participant : registeredParticipants
```
// ==end==
