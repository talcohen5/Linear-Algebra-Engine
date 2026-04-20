Linear Algebra Engine (LAE) 🧮

Project Overview 🚀

A high-performance, multi-threaded Linear Algebra Engine designed to compute complex mathematical expressions concurrently.

The system parses computation trees from JSON files and executes matrix operations over the field of real numbers.

Built to maximize thread utilization and prevent resource contention by using a custom thread pool with fatigue-based scheduling and fine-grained locking mechanisms.

System Architecture & Concurrency 🏗️

Language: Java 21

Build Tool: Maven

Core Components:

LinearAlgebraEngine (LAE): The orchestrator that parses the input JSON, builds the computation tree, loads operand matrices, and decomposes operations into actionable tasks.

TiredExecutor & TiredThread: A custom thread pool implementation using pure Java monitors (wait(), notifyAll()). Worker threads track their active and idle times to calculate a dynamic "fatigue" factor. The executor dynamically schedules tasks to the least-fatigued workers to ensure fair workload distribution.

Shared Memory (SharedMatrix & SharedVector): Matrices are stored as collections of shared vectors (rows or columns). Concurrency is managed at the vector level using ReentrantReadWriteLock, allowing simultaneous reads but ensuring exclusive access for in-place writes.

Supported Operations ➕

Matrix Addition (+)

Matrix Multiplication (*)

Transpose (T)

Negation (-)

Building and Running the Engine 🖥️

The project utilizes Maven for dependency management and building phases.

Compilation & Testing:

Compile the project: mvn compile

Run unit tests: mvn test

Package the application into a JAR: mvn package

Execution:

Run the packaged JAR file with the required arguments (number of threads, input file path, and output file path).

java -jar target/lga-1.0.jar <number_of_threads> <path/to/input.json> <path/to/output.json>

Error Handling & Output 📄

Operations with mismatched dimensions or illegal arguments halt gracefully and write an explicit error message to the output JSON file.

Successful computations write the final resultant matrix into the designated output JSON file.
