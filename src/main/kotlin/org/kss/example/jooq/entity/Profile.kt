package org.kss.example.jooq.entity

import jakarta.persistence.Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Entity
class Profile(
	private val name: String,
	private val description: String,
	private val birthDate: LocalDate,
) : BaseDomainEntity()

@Repository
interface ProfileRepository : JpaRepository<Profile, UUID>
