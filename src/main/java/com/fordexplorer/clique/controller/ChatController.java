package com.fordexplorer.clique.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fordexplorer.clique.data.Group;
import com.fordexplorer.clique.data.Message;
import com.fordexplorer.clique.db.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {
    private SimpMessageSendingOperations messagingTemplate;
    private GroupRepository groupRepository;
    private ObjectMapper mapper;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, GroupRepository groupRepository){
        this.messagingTemplate = messagingTemplate;
        this.groupRepository = groupRepository;
        mapper = new ObjectMapper();
    }

    @MessageMapping("/sendMessage")
    public void acceptMessage(Message message) {
        //Add message to list
        message.getAuthor().getCurrentGroup().addMessage(message);
        //Push message to group channel
        try {
            sendMessage(message, message.getAuthor().getCurrentGroup());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/chat/{id}/messages")
    public List<Message> getMessages(@PathVariable Long id){
        Optional<Group> group = groupRepository.findById(id);
        return group.map(Group::getGroupMessages).orElse(null);
    }

    private void sendMessage(Message message, Group group) throws JsonProcessingException {
        messagingTemplate.convertAndSend(chatURL(group), mapper.writeValueAsString(message));
    }

    private String chatURL(Group g){
        return String.format("/chat/%d",g.getId());
    }

}
