# Revature Project: Social Media Blog API

## Background 

When building a full-stack application, we're typically concerned with both a front end, that displays information to the user and takes in input, and a backend, that manages persisted information.

This project will be a backend for a hypothetical social media app, where we must manage our users’ accounts as well as any messages that they submit to the application. The application will function as a micro-blogging or messaging app. In our hypothetical application, any user should be able to see all of the messages posted to the site, or they can see the messages posted by a particular user. In either case, we require a backend which is able to deliver the data needed to display this information as well as process actions like logins, registrations, message creations, message updates, and message deletions.

## Database Tables 

These will be provided in a sql script, and a ConnectionUtil class that will run the sql script is provided:

### Account
```
account_id INTEGER PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(255),
password VARCHAR(255)
```

### Message
```
message_id INTEGER PRIMARY KEY AUTO_INCREMENT,
posted_by INTEGER,
message_text VARCHAR(255),
time_posted_epoch LONG,
FOREIGN KEY (posted_by) REFERENCES Account(account_id)
```

# Requirements

## 1: API Registration Processing

As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. The body will contain a representation of a JSON Account, but will not contain an account_id.

- The registration will be successful if and only if the username is not blank, the password is at least 4 characters long, and an Account with that username does not already exist. If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. The response status should be 200 OK, which is the default. The new account should be persisted to the database.
- If the registration is not successful, the response status should be 400. (Client error)

## 2: API Login Processing

As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. The request body will contain a JSON representation of an Account, not containing an account_id. In the future, this action may generate a Session token to allow the user to securely use the site. We will not worry about this for now.

- The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database. If successful, the response body should contain a JSON of the account in the response body, including its account_id. The response status should be 200 OK, which is the default.
- If the login is not successful, the response status should be 401. (Unauthorized)


## 3: API Message Creation Processing

As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. The request body will contain a JSON representation of a message, which should be persisted to the database, but will not contain a message_id.

- The creation of the message will be successful if and only if the message_text is not blank, is under 255 characters, and posted_by refers to a real, existing user. If successful, the response body should contain a JSON of the message, including its message_id. The response status should be 200, which is the default. The new message should be persisted to the database.
- If the creation of the message is not successful, the response status should be 400. (Client error)

## 4: API Message Retrieval

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.

- The response body should contain a JSON representation of a list containing all messages retrieved from the database. It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default.

## 5: API Individual Message Retrieval

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.

- The response body should contain a JSON representation of the message identified by the message_id. It is expected for the response body to simply be empty if there is no such message. The response status should always be 200, which is the default.

## 6: API Message Deletion

As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.

- The deletion of an existing message should remove an existing message from the database. If the message existed, the response body should contain the now-deleted message. The response status should be 200, which is the default.
- If the message did not exist, the response status should be 200, but the response body should be empty. This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond with the same type of response.

## 7: API Message Update

As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}. The request body should contain a new message_text values to replace the message identified by message_id. The request body can not be guaranteed to contain any other information.

- The update of a message should be successful if and only if the message id already exists and the new message_text is not blank and is not over 255 characters. If the update is successful, the response body should contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status should be 200, which is the default. The message existing on the database should have the updated message_text.
- If the update of the message is not successful for any reason, the response status should be 400. (Client error)

## 8: API User Message Retrieval

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.

- The response body should contain a JSON representation of a list containing all messages posted by a particular user, which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default.

# Further Guidance

Some classes are already complete and SHOULD NOT BE CHANGED - Integration tests, Model classes for Account and Message, a ConnectionUtil class. Changing any of these classes will likely result in the test cases being impossible to pass.

The .sql script found in src/main/resources is already complete and SHOULD NOT BE CHANGED. Changing this file will likely result in the test cases being impossible to pass.

You SHOULD be changing the SocialMediaController class to add endpoints to the StartAPI method. A main method in Main.java is also provided to allow you to run the entire application and manually play or test with the app. Changing that class will not affect the test cases at all. You could use it to perform any manual unit testing on your other classes.

You SHOULD be creating and designing DAO and Service class to allow you to complete the project. In theory, you could design the project however you like, so long as the functionality works and you are somehow persisting data to the database - but a 3-layer architecture is a robust design pattern and following help you in the long run. You can refer to prior mini-projects and course material for help on designing your application in this way.

# Good Luck!

ted balashov
