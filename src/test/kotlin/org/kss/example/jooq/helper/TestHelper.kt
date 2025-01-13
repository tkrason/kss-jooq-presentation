package org.kss.example.jooq.helper

import org.kss.example.jooq.entity.Address
import org.kss.example.jooq.entity.AddressRepository
import org.kss.example.jooq.entity.Interest
import org.kss.example.jooq.entity.InterestRepository
import org.kss.example.jooq.entity.Profile
import org.kss.example.jooq.entity.ProfileInterest
import org.kss.example.jooq.entity.ProfileInterestRepository
import org.kss.example.jooq.entity.ProfilePicture
import org.kss.example.jooq.entity.ProfilePictureRepository
import org.kss.example.jooq.entity.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class TestHelper(
	@Autowired private val profileRepository: ProfileRepository,
	@Autowired private val profilePictureRepository: ProfilePictureRepository,
	@Autowired private val interestRepository: InterestRepository,
	@Autowired private val profileInterestRepository: ProfileInterestRepository,
	@Autowired private val addressRepository: AddressRepository,
) {

	fun getProfile(
		name: String,
		description: String = "",
		birthDate: LocalDate = LocalDate.now(),
		addressId: UUID? = null,
	) = profileRepository.save(
		Profile(
			name = name,
			description = description,
			birthDate = birthDate,
			addressId = addressId ?: getAddress().id
		)
	)

	fun getAddress(
		street: String = "test street",
		streetNumber: Int = 42,
		city: String = "test city",
	) = addressRepository.save(Address(
		street = street,
		streetNumber = streetNumber,
		city = city
	))

	fun getProfilePicture(
		profileId: UUID,
		fileUrl: String,
		uploadedAt: Instant = Instant.now(),
	) = profilePictureRepository.save(
		ProfilePicture(
			profileId,
			fileUrl,
			uploadedAt
		)
	)

	fun getInterest(name: String) = interestRepository.save(Interest(name))

	fun getProfileInterest(profileId: UUID, interestId: UUID) = profileInterestRepository.save(
		ProfileInterest(
			profileId = profileId,
			interestId = interestId
		)
	)
}