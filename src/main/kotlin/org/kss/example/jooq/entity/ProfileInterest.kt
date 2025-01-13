package org.kss.example.jooq.entity

import jakarta.persistence.Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
class ProfileInterest(
	val profileId: UUID,
	val interestId: UUID,
) : BaseDomainEntity()

@Repository
interface ProfileInterestRepository : JpaRepository<ProfileInterest, UUID>
