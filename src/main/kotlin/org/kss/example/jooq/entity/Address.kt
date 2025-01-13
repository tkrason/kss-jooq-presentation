package org.kss.example.jooq.entity

import jakarta.persistence.Entity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
class Address(
	val street: String,
	val streetNumber: Int,
	val city: String,
) : BaseDomainEntity()

@Repository
interface AddressRepository : JpaRepository<Address, UUID>
