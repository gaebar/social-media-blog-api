package Controller;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import Service.ServiceException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;
    private static final String MESSAGE_API_PATH = "/messages/{message_id}";

    private static final String MESSAGE_ID_PARAM = "message_id";

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccount);
        app.post("/login", this::loginAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get(getMessageApiPath(), this::getMessageById);
        app.delete(getMessageApiPath(), this::deleteMessageById);
        app.patch(getMessageApiPath(), this::updateMessageById);
        app.get("/accounts/{account_id}/messages",
                this::getMessagesByAccountId);

        return app;

    }

    // Followed the SonarLint suggestions to customized getMessageApiPath() method,
    // which returns the MESSAGE_API_PATH variable value. By using the
    // getMessageApiPath() method
    // instead of directly referencing the MESSAGE_API_PATH variable in the endpoint
    // definitions,
    // we ensure that the API path is consistent across the codebase and can be
    // easily modified if required.
    private static final String getMessageApiPath() {
        return MESSAGE_API_PATH;
    }

    // Deserializes the request body to an Account object and registers the account
    private void registerAccount(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        try {
            Account registeredAccount = accountService.createAccount(account);
            ctx.json(mapper.writeValueAsString(registeredAccount));
        } catch (ServiceException e) {
            ctx.status(400);
        }
    }

    // Logs in an account by validating the credentials and setting session
    // attributes
    private void loginAccount(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); // it calls a default no-arg constructor from Model.Account - REQUIRED
                                                  // for Jackson ObjectMapper
        Account account = mapper.readValue(ctx.body(), Account.class);

        try {
            Optional<Account> loggedInAccount = accountService
                    .validateLogin(account);
            if (loggedInAccount.isPresent()) {
                ctx.json(mapper.writeValueAsString(loggedInAccount));
                ctx.sessionAttribute("logged_in_account",
                        loggedInAccount.get());
                ctx.json(loggedInAccount.get());
            } else {
                ctx.status(401);
            }
        } catch (ServiceException e) {
            ctx.status(401);
        }
    }

    // Creates a new message by deserializing the request body and associating it
    // with an account
    private void createMessage(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(ctx.body(), Message.class);
        try {
            Optional<Account> account = accountService
                    .getAccountById(mappedMessage.getPosted_by());
            Message message = messageService.createMessage(mappedMessage,
                    account);
            ctx.json(message);
        } catch (ServiceException e) {
            ctx.status(400);
        }
    }

    // Retrieves all messages from the message service and sends them as a response
    private void getAllMessages(Context ctx) {

        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    // Retrieves a specific message by its ID from the message service and sends it
    // as a response
    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam(MESSAGE_ID_PARAM));
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                ctx.json(message.get());
            } else {
                ctx.status(200);
            }
        } catch (NumberFormatException e) {
            ctx.status(400);
        } catch (ServiceException e) {
            ctx.status(200);
        }
    }

    // Deletes a specific message by its ID from the message service
    private void deleteMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam(MESSAGE_ID_PARAM));
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                messageService.deleteMessage(message.get());
                ctx.status(200);
                ctx.json(message.get());
            } else {
                ctx.status(200);
            }
        } catch (ServiceException e) {
            ctx.status(200);
        }
    }

    // Update a specific message by its ID with new content
    private void updateMessageById(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message mappedMessage = mapper.readValue(ctx.body(), Message.class);
        try {
            int id = Integer.parseInt(ctx.pathParam(MESSAGE_ID_PARAM));
            mappedMessage.setMessage_id(id);

            Message messageUpdated = messageService
                    .updateMessage(mappedMessage);
            ctx.json(messageUpdated);

        } catch (ServiceException e) {
            ctx.status(400);
        }
    }

    // Retrieve all messages associated with a specific account ID and sends them as
    // a response
    private void getMessagesByAccountId(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageService
                    .getMessagesByAccountId(accountId);
            if (!messages.isEmpty()) {
                ctx.json(messages);
            } else { // if no messages
                ctx.json(messages);
                ctx.status(200);
            }
        } catch (ServiceException e) {
            ctx.status(400);
        }
    }
}
