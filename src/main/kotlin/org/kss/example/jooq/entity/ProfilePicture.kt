package org.kss.example.jooq.entity

import jakarta.persistence.Entity
import org.jooq.impl.DSL
import org.jooq.kotlin.intoMap
import org.kss.example.tables.references.PROFILE
import org.kss.example.tables.references.PROFILE_PICTURE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Entity
class ProfilePicture(
	private val profileId: UUID,
	private val fileUrl: String,
	private val uploadedAt: Instant,
) : BaseDomainEntity()

@Repository
interface ProfilePictureRepository : JpaRepository<ProfilePicture, UUID>
