package com.meow.footprint.domain.footprint.repository;

import com.meow.footprint.domain.footprint.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
}
