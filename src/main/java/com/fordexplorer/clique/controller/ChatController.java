package com.fordexplorer.clique.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fordexplorer.clique.data.Group;
import com.fordexplorer.clique.data.Message;
import com.fordexplorer.clique.db.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private SimpMessageSendingOperations messagingTemplate;
    private ObjectMapper mapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate){
        this.messagingTemplate = messagingTemplate;
        mapper = new ObjectMapper();
    }

    @MessageMapping("/sendMessage")
    public void acceptMessage(Message message) {
        logger.info("Received Message of Length {} from {}", message.getContent().length(), message.getAuthor());
        //Add message to list
        message.getAuthor().getCurrentGroup().addMessage(message);
        groupRepository.save(message.getAuthor().getCurrentGroup());

        //Push message to group channel
        try {
            sendMessage(message, message.getAuthor().getCurrentGroup());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Message message, Group group) throws JsonProcessingException {
        String messageJson = mapper.writeValueAsString(message);
        String chatUrl = chatURL(group);

        logger.info("Sending to {}, Message {}", chatUrl, messageJson);
        messagingTemplate.convertAndSend(chatUrl, messageJson);
    }

    private String chatURL(Group g){
        return String.format("/chat/%d",g.getId());
    }

}
