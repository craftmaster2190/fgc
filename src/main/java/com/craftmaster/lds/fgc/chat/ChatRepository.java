package com.craftmaster.lds.fgc.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Instant> {

}

