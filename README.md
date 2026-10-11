Order Management System 

A robust Java desktop application built to manage clients, products, and complex ordering processes.
The project focuses on clean code and reusability, featuring a 3-tier Layered Architecture (GUI, 
Business Logic, Data Access) and heavy utilization of the Java Reflection API to eliminate boilerplate code.

Features:

Generic CRUD Operations: Implemented a generic DatabaseManager<T> that uses Reflection to automatically 
map Java objects to database tables, allowing seamless insert, update, delete, and find operations for 
any model (Client, Product, Order).

Dynamic UI Generation: Developed a JTableFactory that reads object properties via Reflection at runtime 
to dynamically generate and populate GUI tables, regardless of the data type.

Business Logic Validation: Dedicated BLL (Business Logic Layer) classes intercept database operations
to enforce business rules and validate user inputs before hitting the persistence layer.

Order & Billing Processing: Complete workflow for placing orders, updating product stock, handling 
insufficient stock scenarios, and automatically generating immutable Bill records for successful transactions.
