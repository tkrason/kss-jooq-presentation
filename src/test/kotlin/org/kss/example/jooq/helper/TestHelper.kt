package org.kss.example.jooq.helper

import org.kss.example.jooq.entity.Profile
import org.kss.example.jooq.entity.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TestHelper(
	@Autowired private val profileRepository: ProfileRepository,
) {

	fun getProfile(
		name: String,
		description: String,
		birthDate: LocalDate,
	) = profileRepository.save(
		Profile(
			name = name,
			description = description,
			birthDate = birthDate
		)
	)

}