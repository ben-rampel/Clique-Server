package com.fordexplorer.clique;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fordexplorer.clique.data.Group;
import com.fordexplorer.clique.data.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {
    private SimpMessageSendingOperations messagingTemplate;
    private ObjectMapper mapper;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate){
        mapper = new ObjectMapper();
    }

    @MessageMapping("chat/{id}/sendMessage")
    public void acceptMessage(@RequestBody Message message){
        //Add message to list
        message.getAuthor().getCurrentGroup().addMessage(message);
        //Push message to group channel
        try {
            sendNewMessage(message, message.getAuthor().getCurrentGroup());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/chat/{id}/messages")
    public void getMessages(@RequestParam Long id){
        //return GroupCRUD.getGroupbyID(id).getMessages()
    }

    private void sendNewMessage(Message message, Group group) throws JsonProcessingException {
        messagingTemplate.convertAndSend(chatURL(group), mapper.writeValueAsString(message));
    }

    private String chatURL(Group g){
        return String.format("/chat/%d",g.getId());
    }


}
