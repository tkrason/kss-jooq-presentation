package org.kss.example.jooq.helper

import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class BaseTest {

	@Autowired
	private lateinit var jdbcTemplate: JdbcTemplate

	@Autowired
	internal lateinit var dslContext: DSLContext

	@Autowired
	internal lateinit var testHelper: TestHelper

	@BeforeEach
	fun beforeEach() {
		jdbcTemplate.execute(TRUNCATE_ALL_TABLES_SQL)
	}

}

private const val TRUNCATE_ALL_TABLES_SQL = """
DO
${'$'}do${'$'}
    BEGIN
        EXECUTE
            (SELECT 'TRUNCATE TABLE ' || string_agg(oid::regclass::text, ', ') || ' CASCADE'
             FROM   pg_class
             WHERE  relkind = 'r'  -- only tables
               AND    relnamespace = 'public'::regnamespace
			   AND relname NOT IN ('flyway_schema_history')
            );
    END
${'$'}do${'$'};
"""
