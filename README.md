# Receipt Processor

Build a webservice that fulfils the documented API. The API is described below. A formal definition is provided 
in the [api.yml](./api.yml) file, but the information in this README is sufficient for completion of this challenge. We will use the 
described API to test your solution.

Provide any instructions required to run your application.

Data does not need to persist when your application stops. It is sufficient to store information in memory. There are too many different database solutions, we will not be installing a database on our system when testing your application.

## How to Run The Application

### Prerequisites

To run this project, ensure you have the following installed:
- Docker
- Java 17 (Only required if running *without* Docker)
- Maven (Only required  if building manually *without* Docker)

### Cloning the Repository

1. **Clone the Repository**: 
   Use the following command to clone the repository to your local machine:
    ```bash
    git clone https://github.com/yuchengzang/receipt-processor-challenge.git
    ```

2. **Navigate to the Project Directory:**
   ```bash
   cd receipt-processor-challenge
   ```

### Running with Docker

1. **Build the Docker Image**:
   From the project root directory, build the Docker image using:
    ```bash
    docker build -t receipt-processor .
    ```

2. **Run the Docker Container:**
   Start the container by running:
    ```bash
    docker run -p 8080:8080 receipt-processor
    ```

3. **Access the Application:**
  
   The application will be available at http://localhost:8080.
   You can use tools like Postman or `curl` to interact with the API.
   To stop the application, use `Ctrl + C` in the terminal.

4. **Run The Unit Tests (Optional)** 
  
    To run the unit tests directly via Docker, use:
    ```bash
    docker run --rm -v "$(pwd)":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn clean test
    ```

    Alternatively, if you want to run the tests using Maven locally, use
    ```bash
    ./mvnw test
    ```

## Notes to the Grader

### Development Process
- This project was developed over multiple days. Progress was incremental due to schoolwork and illness, which occasionally limited time availability.
- Throughout the development, emphasis was placed on clean code, extensive testing, and adhering to the documented API specification.

### Key Implementation Details
1. **Unit Tests**:
   - A total of **124 unit tests** were written to ensure the correctness and robustness of the application.
   - To run the tests, use:
     ```bash
     ./mvnw test
     ```

2. **Concurrency**:
   - The application uses a `ConcurrentHashMap` for in-memory storage to handle potential concurrent access during receipt processing.

3. **Monetary Calculations**:
   - `BigDecimal` is used for monetary calculations to ensure precision and accuracy.

4. **Handling Total Price**:
   - The application does not validate whether the sum of all item prices equals the total amount in the receipt. This decision was made to accommodate scenarios involving **tips, taxes, or discounts**, which are typically not itemized.

5. **Date and Time**:
   - The application uses `LocalDate` and `LocalTime` for processing dates and times because the incoming JSON does not include timezone information. This design choice simplifies parsing but can be extended to include timezone support if needed.

6. **Logging**:
   - Extensive logging has been implemented to aid in debugging and provide clear feedback during runtime. Logs include details for validation errors, successful operations, and unexpected issues.

7. **Points Calculation**:
   - Points are **not stored** as a member of the `Receipt` class. This decision allows flexibility to update the points calculation rules without modifying the data structure.
   - While points are calculated on-demand, caching can be implemented in the future to improve performance.

### Tools and Assistance
- **AI Tools**:
   - Testers and parts of the code were generated with assistance from **GitHub Copilot** and **ChatGPT**.
   - These tools were used for brainstorming, generating test cases, and validating ideas. All outputs were manually reviewed and adapted as necessary.


## Language Selection

You can assume our engineers have Go and Docker installed to run your application. Go is our preferred language, but it is not a requirement for this exercise. If you are not using Go, include a Dockerized setup to run the code. You should also provide detailed instructions if your Docker file requires any additional configuration to run the application.

## Submitting Your Solution

Provide a link to a public repository, such as GitHub or BitBucket, that contains your code to the provided link through Greenhouse.

---
## Summary of API Specification

### Endpoint: Process Receipts

* Path: `/receipts/process`
* Method: `POST`
* Payload: Receipt JSON
* Response: JSON containing an id for the receipt.

Description:

Takes in a JSON receipt (see example in the example directory) and returns a JSON object with an ID generated by your code.

The ID returned is the ID that should be passed into `/receipts/{id}/points` to get the number of points the receipt
was awarded.

How many points should be earned are defined by the rules below.

Reminder: Data does not need to survive an application restart. This is to allow you to use in-memory solutions to track any data generated by this endpoint.

Example Response:
```json
{ "id": "7fb1377b-b223-49d9-a31a-5a02701dd310" }
```

## Endpoint: Get Points

* Path: `/receipts/{id}/points`
* Method: `GET`
* Response: A JSON object containing the number of points awarded.

A simple Getter endpoint that looks up the receipt by the ID and returns an object specifying the points awarded.

Example Response:
```json
{ "points": 32 }
```

---

# Rules

These rules collectively define how many points should be awarded to a receipt.

* One point for every alphanumeric character in the retailer name.
* 50 points if the total is a round dollar amount with no cents.
* 25 points if the total is a multiple of `0.25`.
* 5 points for every two items on the receipt.
* If the trimmed length of the item description is a multiple of 3, multiply the price by `0.2` and round up to the nearest integer. The result is the number of points earned.
* 6 points if the day in the purchase date is odd.
* 10 points if the time of purchase is after 2:00pm and before 4:00pm.


## Examples

```json
{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
    {
      "shortDescription": "Mountain Dew 12PK",
      "price": "6.49"
    },{
      "shortDescription": "Emils Cheese Pizza",
      "price": "12.25"
    },{
      "shortDescription": "Knorr Creamy Chicken",
      "price": "1.26"
    },{
      "shortDescription": "Doritos Nacho Cheese",
      "price": "3.35"
    },{
      "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
      "price": "12.00"
    }
  ],
  "total": "35.35"
}
```
```text
Total Points: 28
Breakdown:
     6 points - retailer name has 6 characters
    10 points - 5 items (2 pairs @ 5 points each)
     3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3)
                item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
     3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3)
                item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
     6 points - purchase day is odd
  + ---------
  = 28 points
```

----

```json
{
  "retailer": "M&M Corner Market",
  "purchaseDate": "2022-03-20",
  "purchaseTime": "14:33",
  "items": [
    {
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    },{
      "shortDescription": "Gatorade",
      "price": "2.25"
    }
  ],
  "total": "9.00"
}
```
```text
Total Points: 109
Breakdown:
    50 points - total is a round dollar amount
    25 points - total is a multiple of 0.25
    14 points - retailer name (M&M Corner Market) has 14 alphanumeric characters
                note: '&' is not alphanumeric
    10 points - 2:33pm is between 2:00pm and 4:00pm
    10 points - 4 items (2 pairs @ 5 points each)
  + ---------
  = 109 points
```

---

# FAQ

### How will this exercise be evaluated?
An engineer will review the code you submit. At a minimum they must be able to run the service and the service must provide the expected results. You
should provide any necessary documentation within the repository. While your solution does not need to be fully production ready, you are being evaluated so
put your best foot forward.

### I have questions about the problem statement
For any requirements not specified via an example, use your best judgment to determine the expected result.

### Can I provide a private repository?
If at all possible, we prefer a public repository because we do not know which engineer will be evaluating your submission. Providing a public repository
ensures a speedy review of your submission. If you are still uncomfortable providing a public repository, you can work with your recruiter to provide access to
the reviewing engineer.

### How long do I have to complete the exercise?
There is no time limit for the exercise. Out of respect for your time, we designed this exercise with the intent that it should take you a few hours. But, please
take as much time as you need to complete the work.
