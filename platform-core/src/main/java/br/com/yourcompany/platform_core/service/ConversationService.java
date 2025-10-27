package br.com.yourcompany.platform_core.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.yourcompany.platform_core.dominio.Conversation.Conversation;
import br.com.yourcompany.platform_core.dominio.Conversation.ConversationParticipant;
import br.com.yourcompany.platform_core.dominio.Conversation.ConversationType;
import br.com.yourcompany.platform_core.dto.ConversationResponse;
import br.com.yourcompany.platform_core.dto.CreateConversationRequest;
import br.com.yourcompany.platform_core.repository.ConversationParticipantRepository;
import br.com.yourcompany.platform_core.repository.UserRepository;
import br.com.yourcompany.platform_core.repository.ConversationRepository;
import br.com.yourcompany.platform_core.dominio.user.User;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationParticipantRepository participantRepository;

    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        // 1. Criar a entidade
        Conversation newConversation = new Conversation(
            ConversationType.valueOf(request.getType().toUpperCase()),
            request.getGroupName()
        );
        
        // 2. Salvar E CAPTURAR A RESPOSTA
        // "savedConversation" é o objeto que contém o ID gerado pelo banco
        Conversation savedConversation = conversationRepository.save(newConversation);

        // 3. Buscar os usuários
        List<User> users = userRepository.findAllById(request.getParticipantIds());
        
        // TODO: Adicionar checagem se todos os usuários foram encontrados

        // 4. Iterar e usar o objeto CORRETO
        for (User user : users) {
            // Usamos "savedConversation" (que tem o ID)
            ConversationParticipant participant = new ConversationParticipant(savedConversation, user);
            participantRepository.save(participant);
        }

        // 5. Retornar a resposta usando a entidade salva
        return ConversationResponse.fromEntity(savedConversation);
    }
}