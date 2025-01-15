package org.kss.example.jooq

import org.junit.jupiter.api.Test
import org.kss.example.jooq.helper.BaseTest
import org.kss.example.tables.references.ADDRESS
import org.kss.example.tables.references.PROFILE
import java.util.UUID

class Example3Joins : BaseTest() {

	data class Result(
		val id: UUID,
		val name: String,
		val street: String,
		val streetNumber: Int,
		val city: String,
	)

	/**
	 * ``` sql
	 *select "public"."profile"."id",
	 *        "public"."profile"."name",
	 *        "alias_13440094"."street",
	 *        "alias_13440094"."street_number",
	 *        "alias_13440094"."city"
	 * from ("public"."profile" join "public"."address" as "alias_13440094"
	 *       on "public"."profile"."address_id" = "alias_13440094"."id")
	 *
	 * ```
	 *
	 */
	@Test
	fun `jooq with implicit joins`() {

		val prague = testHelper.getAddress("Tusarova", 1, "Praha")
		val brno = testHelper.getAddress("Národní", 20, "Brno")

		testHelper.getProfile(name = "Pan X", addressId = prague.id)
		testHelper.getProfile(name = "Pani Y", addressId = brno.id)

		val result = dslContext
			.select(
				PROFILE.ID,
				PROFILE.NAME,
				PROFILE.address.STREET,
				PROFILE.address.STREET_NUMBER,
				PROFILE.address.CITY
			)
			.from(PROFILE)
			.fetch()
			.map {
				Result(
					id = it[PROFILE.ID]!!,
					name = it[PROFILE.NAME]!!,
					street = it[ADDRESS.STREET]!!,
					streetNumber = it[ADDRESS.STREET_NUMBER]!!,
					city = it[ADDRESS.CITY]!!
				)
			}

		result.forEach {
			println("${it.name} lives at ${it.street} ${it.streetNumber}, ${it.city}")
		}
	}

}