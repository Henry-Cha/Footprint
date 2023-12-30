package com.meow.footprint.domain.guestbook.repository;

import com.meow.footprint.domain.guestbook.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
}
