package com.sai.service;

import com.sai.entity.Conversation;
import com.sai.entity.dto.ConversationDto;
import com.sai.exception.BusinessException;
import com.sai.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Conversation createConversation() {
        return conversationRepository.insert(
                Conversation.builder()
                        .title("新对话")
                        .top(0)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build()
        );

    }

    // 后面应该加入用户id，只能查询当前用户的id
    public List<Conversation> getConversationList() {
        // 排序规则：
        // 1. 置顶优先（top 降序：1 在前，0 在后）
        // 2. 其次按更新时间倒序（最近更新的排前面）
        Sort sort = Sort.by(
                Sort.Order.desc("top"),
                Sort.Order.desc("updateTime")
        );
        return conversationRepository.findAll(sort);
    }

    // 删除对话
    public void deleteConversation(String id) {
        try {
            conversationRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("删除对话失败");
        }
    }

    // 修改对话标题
    public Conversation modifyConversation(ConversationDto conversationDto) {
        try {
            Conversation conversation = conversationRepository.findById(conversationDto.getId()).orElseThrow(() -> new BusinessException("对话不存在"));
            conversation.setTitle(conversationDto.getTitle());
            conversation.setUpdateTime(LocalDateTime.now());
            return conversationRepository.save(conversation);
        } catch (Exception e) {
            throw new BusinessException("修改对话失败");
        }
    }

    // 置顶对话
    public Conversation topConversation(String id) {
        try {
            Conversation conversation = conversationRepository.findById(id).orElseThrow(() -> new BusinessException("对话不存在"));
            conversation.setTop(conversation.getTop() == 0 ? 1 : 0);
            conversation.setUpdateTime(LocalDateTime.now());
            return conversationRepository.save(conversation);
        } catch (Exception e) {
            throw new BusinessException("置顶对话失败");
        }
    }


}
