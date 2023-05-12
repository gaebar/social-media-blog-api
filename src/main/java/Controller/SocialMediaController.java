package Controller;

import Service.AccountService;
import Service.MessageService;
import Model.Account;
import Model.Message;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccount);
        app.post("/login", this::loginAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/:id", this::getMessageById);
        app.delete("/messages/:id", this::deleteMessageById);
        app.patch("/messages/:id", this::updateMessageById);
        app.get("/accounts/:id/messages", this::getMessagesByAccountId);

        app.start(8080);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

    private void registerAccount(Context context){
        Account account = context.bodyAsClass(Account.class);
        Optional<Account> registeredAccount = accountService.register(account);
        if(registerAccount.isPresent()){
            context.json(registeredAccount.get());
        } else {
            context.status(400).result("Registration failed");
        }
    }

    private void loginAccount(Context context){
        Account account = context.bodyAsClass(Account.class);
        Optional<Account> loggedInAccount = accountService.login(account);
        if(loggedInAccount.isPresent()){
            context.json(loggedInAccount.get());
        } else {
            context.status(401).result("Invalid credenntial");
        }
    }

    private void createMessage(Context context){
        Message message = context.bodyAsClass(Message.class);
        message = messageService.createMessage(message);
        context.json(message);
    }

    private void getAllMessages(Context context){
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
    }

    private void getMessageById(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        Optional<Message> message = messageService.getMessageById(id);
        if(message.isPresent()){
            context.json(message.get());
        } else {
            context.status(404);
        }
    }

    private void deleteMessageById(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        Optional<Message> message = messageService.getMessageById(id);
        if(message.isPresent()){
            messageService.deleteMessage(message.get());
            context.status(204);
        } else {
            context.status(404);
        }

    private void updateMessageById(Context context){
        int id = Integer.parseInt(context.pathParam("id"));
        Message message = context.bodyAsClass(Message.class);
        message.setMessageId(id);
        messageService.updateMessage(message);
        context.json(message);
    }

    private void getMessagesByAccountId(Context context){
        int accountId = Integer.parseInt(context.pathParam("id"));
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        if(!messages.isEmpty()){
            context.json(messages);
        } else{
        context.json(messages);
        }
    }

}