package org.kss.example.jooq

import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.kotlin.mapping
import org.junit.jupiter.api.Test
import org.kss.example.jooq.helper.BaseTest
import org.kss.example.tables.references.PROFILE
import org.kss.example.tables.references.PROFILE_INTEREST
import org.kss.example.tables.references.PROFILE_PICTURE
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.util.UUID

data class ProfilePicture(
	val url: String,
	val uploadedAt: Instant,
)

data class Interest(
	val name: String,
)

data class Profile(
	val id: UUID,
	val name: String,
	val description: String,
	val birthDate: LocalDate,
	val profilePictures: List<ProfilePicture>,
	val interests: List<Interest>,
)

class Example3Multiset : BaseTest() {

	/**
	 * ``` sql
	 *	select "public"."profile"."id",
	 *	        "public"."profile"."name",
	 *	        "public"."profile"."description",
	 *	        "public"."profile"."birth_date",
	 *
	 *	        (select coalesce(jsonb_agg(jsonb_build_array("v0", "v1")), jsonb_build_array())
	 *	         from (select "public"."profile_picture"."file_url" as "v0", "public"."profile_picture"."uploaded_at" as "v1"
	 *	               from "public"."profile_picture"
	 *	               where "public"."profile_picture"."profile_id" = "public"."profile"."id"
	 *	               order by "public"."profile_picture"."uploaded_at") as t),
	 *
	 *	        (select coalesce(jsonb_agg(jsonb_build_array("v0")), jsonb_build_array())
	 *	         from (select "alias_67372582"."name" as "v0"
	 *	               from ("public"."profile_interest" join "public"."interest" as "alias_67372582"
	 *	                     on "public"."profile_interest"."interest_id" = "alias_67372582"."id")
	 *	               where "public"."profile_interest"."profile_id" = "public"."profile"."id") as t)
	 *	 from "public"."profile"
	 *
	 * ```
	 */
	@Test
	fun `jooq with multiset`() {
		// given
		val food = testHelper.getInterest("Food")
		val traveling = testHelper.getInterest("Traveling")
		val programming = testHelper.getInterest("Programming")

		val peter = testHelper.getProfile(
			name = "Peter", description = "Hello Im cool match me", birthDate = LocalDate.of(1990, Month.APRIL, 20)
		)

		testHelper.getProfilePicture(peter.id, "peter pic 1")
		testHelper.getProfilePicture(peter.id, "peter pic 2")
		testHelper.getProfilePicture(peter.id, "peter pic 3")
		testHelper.getProfilePicture(peter.id, "peter pic 4")
		testHelper.getProfilePicture(peter.id, "peter pic 5")
		testHelper.getProfilePicture(peter.id, "peter pic 6")

		testHelper.getProfileInterest(peter.id, food.id)
		testHelper.getProfileInterest(peter.id, traveling.id)

		val adam = testHelper.getProfile(
			name = "Adam", description = "Im cool", birthDate = LocalDate.of(2001, Month.NOVEMBER, 20)
		)

		testHelper.getProfilePicture(adam.id, "adam pic 1")
		testHelper.getProfilePicture(adam.id, "adam pic 2")
		testHelper.getProfilePicture(adam.id, "adam pic 3")

		testHelper.getProfileInterest(adam.id, programming.id)

		// when
		val profilePictures = DSL.multiset(
			DSL.select(
				PROFILE_PICTURE.FILE_URL.notNullable(),
				PROFILE_PICTURE.UPLOADED_AT.notNullable()
			)
				.from(PROFILE_PICTURE)
				.where(PROFILE_PICTURE.PROFILE_ID.eq(PROFILE.ID))
				.orderBy(PROFILE_PICTURE.UPLOADED_AT)
		).mapping(::ProfilePicture)

		val interests = DSL.multiset(
			DSL.select(
				PROFILE_INTEREST.interest.NAME.notNullable()
			)
				.from(PROFILE_INTEREST)
				.where(PROFILE_INTEREST.PROFILE_ID.eq(PROFILE.ID))
		).mapping(::Interest)

		val result = dslContext
			.select(
				PROFILE.ID,
				PROFILE.NAME,
				PROFILE.DESCRIPTION,
				PROFILE.BIRTH_DATE,
				profilePictures,
				interests,
			)
			.from(PROFILE)
			.fetch()
			.map {
				Profile(
					id = it[PROFILE.ID]!!,
					name = it[PROFILE.NAME]!!,
					description = it[PROFILE.DESCRIPTION]!!,
					birthDate = it[PROFILE.BIRTH_DATE]!!,
					profilePictures = it[profilePictures],
					interests = it[interests]
				)
			}

		// then
		result.forEach(::println)
	}

	private fun <T> TableField<Record, T?>.notNullable() = this.convertFrom { it!! }
}