package com.meow.footprint.domain.footprint.repository;

import com.meow.footprint.domain.footprint.entity.Footprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootprintRepository extends JpaRepository<Footprint,Long> {
}
