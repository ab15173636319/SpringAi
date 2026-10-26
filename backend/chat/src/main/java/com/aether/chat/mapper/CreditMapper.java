package com.aether.chat.mapper;

import com.aether.security.pojo.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CreditMapper extends MongoRepository<UserAccount, String> {
}
