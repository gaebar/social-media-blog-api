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

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */

    private final AccountService accountService;
    private final MessageService messageService;

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
        app.get("/messages/{id}", this::getMessageById);
        app.delete("/messages/{id}", this::deleteMessageById);
        app.patch("/messages/{id}", this::updateMessageById);
        app.get("/accounts/{id}/messages", this::getMessagesByAccountId);

        return app;

    }

    /**
     * This is an example handler for an example endpoint.
     *
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */

    private void registerAccount(Context context) {
        Account account = context.bodyAsClass(Account.class);
        try {
            Account registeredAccount = accountService.createAccount(account);
            context.json(registeredAccount);
        } catch (ServiceException e) {
            context.status(400).result("Registration failed");
        }
    }

    private void loginAccount(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(); // it calls a default no-arg constructor from Model.Account - REQUIRED for Jackson ObjectMapper
        Account account = mapper.readValue(ctx.body(), Account.class);
        Optional<Account> loggedInAccount = accountService.validateLogin(account);
        try{
            if (loggedInAccount.isPresent()) {
                ctx.json(mapper.writeValueAsString(loggedInAccount));
                ctx.sessionAttribute("logged_in_account", loggedInAccount.get());
                ctx.json(loggedInAccount.get());
            } else {
                ctx.status(401).result("Invalid credential");
            }
        } catch (ServiceException e) {
            ctx.status(401).result("Login failed");
        }

    private void createMessage(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            Account account = context.sessionAttribute("logged_in_account");
            if (account != null) {
                message = messageService.createMessage(message, account);
                context.json(message);
            } else {
                context.status(401).result("User not logged in");
            }
        } catch (ServiceException e) {
            context.status(400).result("Failed to create message");
        }
    }

    private void getAllMessages(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    private void getMessageById(Context context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            Optional<Message> message = messageService.getMessageById(id);
            if (message.isPresent()) {
                context.json(message.get());
            } else {
                context.status(404);
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message id format");
        }
    }

    private void deleteMessageById(Context context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            Optional<Message> message = messageService.getMessageById(id);
            Account account = context.sessionAttribute("logged_in_account");
            if (message.isPresent() && account != null) {
                messageService.deleteMessage(message.get(), account);
                context.status(204);
            } else {
                context.status(404).result("No messages found");
            }
        } catch (NumberFormatException e) {
            context.status(400).result("Invalid message id format");
        } catch (ServiceException e) {
            context.status(400).result("Failed to retrieve message");
        }
    }

    private void updateMessageById(Context context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            Message message = context.bodyAsClass(Message.class);
            message.setMessage_id(id);
            Account account = context.sessionAttribute("logged_in_account");
            if (account != null) {
                message = messageService.updateMessage(id, message, account);
                context.json(message);
            } else {
                context.status(401).result("User not logged in");
            }

        } catch (ServiceException e) {
            context.status(400).result("Failed to update message");
        }
    }

    private void getMessagesByAccountId(Context context) {
        try {
            int accountId = Integer.parseInt(context.pathParam("id"));
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
            if (!messages.isEmpty()) {
                context.json(messages);
            } else {
                context.status(404).result("No messages found");
            }
        } catch (ServiceException e) {
            context.status(400).result("Failed to retrieve messages");
        }
    }
}
