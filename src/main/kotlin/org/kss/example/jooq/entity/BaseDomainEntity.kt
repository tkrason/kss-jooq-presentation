package org.kss.example.jooq.entity

import com.fasterxml.uuid.Generators
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.UUID

/**
 *  We are using Hibernate only to persist entities into database
 *
 *  Note: You can also include here fields as created_at, updated_at, version...
 *  Basically everything that should be shared across all entities
 */
@MappedSuperclass
open class BaseDomainEntity(
	// This will help us later ;)
	@Id val id: UUID = UUIDv7(),
)

@Suppress("FunctionName")
fun UUIDv7(): UUID = Generators.timeBasedEpochGenerator().generate()