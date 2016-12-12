Vaadin demo report:-

Below are the points we worked-on in this poc:-
1. From technical point of view, we have used VAADIN with Hibernate-JPA. The project type is MAVEN. Though the client requirement was myBatis framework instead of Hibernate-JPA.
2. The database configured is ms-sql (as per client request). 
3. The demo application has all crud operation right from login, insert, update and delete.
4.  The accessing of application is role based. Currently only admin is permissible to access the application.
5. After login, it will redirected to the main page that contains menu and a dashboard and simple CRUD to the admin.
6. The admin is facilitated to generate report of all users in '.pdf' format.
7. While generating report, the user has to refresh the report page manually to get the latest changes.
8. The user can also download the generated pdf.
