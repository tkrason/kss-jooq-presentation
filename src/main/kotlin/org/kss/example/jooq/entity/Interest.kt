package org.kss.example.jooq.entity

import jakarta.persistence.Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
class Interest(
	val name: String,
) : BaseDomainEntity()

@Repository
interface InterestRepository : JpaRepository<Interest, UUID>
