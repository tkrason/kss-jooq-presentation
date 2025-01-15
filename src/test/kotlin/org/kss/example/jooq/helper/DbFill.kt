package org.kss.example.jooq.helper

import org.junit.jupiter.api.Test
import org.kss.example.jooq.entity.UUIDv7
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import kotlin.random.Random

class DbFill(
	@Autowired private val txService: TxService,
) : BaseTest() {

	@Test
	fun `fill db`() {

		val interests = (0..30).map {
			testHelper.getInterest("Interest n. $it").id
		}

		(0..100_000)
			.chunked(20)
			.forEach { chunk ->
				txService.inTx {
					chunk.forEach {
						val address = testHelper.getAddress(
							street = "common street",
							streetNumber = it,
							city = "common city"
						)
						val profile = testHelper.getProfile(
							name = "$it",
							description = "Hey im ${UUIDv7()} (random text)",
							birthDate = LocalDate.now(),
							addressId = address.id
						)

						val interestIds = interests
							.shuffled()
							.take(Random.nextInt(0, 5))

						interestIds.forEach {
							testHelper.getProfileInterest(
								profileId = profile.id,
								interestId = it
							)
						}

						(1..Random.nextInt(2,8)).forEach {
							testHelper.getProfilePicture(
								profileId = profile.id,
								fileUrl = "photo/$it",
								uploadedAt = Instant.now(),
							)
						}
					}
				}
			}
	}
}

@Service
class TxService {

	@Transactional
	fun inTx(block: () -> Unit) {
		block()
	}

}