package quo.vadis.megasys.utils.jooq.sql

import org.jooq.DSLContext
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import quo.vadis.megasys.utils.jooq.JooqRepository
import quo.vadis.megasys.utils.jooq.sql.tables.TestGroupTable
import quo.vadis.megasys.utils.jooq.sql.tables.TestUserGroupTable
import quo.vadis.megasys.utils.jooq.sql.tables.TestUserTable
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestGroupEntity
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestUserEntity
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestUserGroupEntity
import quo.vadis.megasys.utils.jooq.sql.tables.records.TestGroupRecord
import quo.vadis.megasys.utils.jooq.sql.tables.records.TestUserGroupRecord
import quo.vadis.megasys.utils.jooq.sql.tables.records.TestUserRecord

class TestUserRepository(dslContext: DSLContext): JooqRepository<TestUserRecord, TestUserTable, TestUserEntity, Long>(dslContext, Tables.TEST_USER, TestUserEntity::class.java)

class TestGroupRepository(dslContext: DSLContext): JooqRepository<TestGroupRecord, TestGroupTable, TestGroupEntity, Long>(dslContext, Tables.TEST_GROUP, TestGroupEntity::class.java)

class TestUserGroupRepository(dslContext: DSLContext): JooqRepository<TestUserGroupRecord, TestUserGroupTable, TestUserGroupEntity, Long>(dslContext, Tables.TEST_USER_GROUP, TestUserGroupEntity::class.java)

@TestConfiguration
class TestConfig {
  @Bean
  fun testUserRepository(dslContext: DSLContext) = TestUserRepository(dslContext)

  @Bean
  fun testGroupRepository(dslContext: DSLContext) = TestGroupRepository(dslContext)

  @Bean
  fun testUserGroupRepository(dslContext: DSLContext) = TestUserGroupRepository(dslContext)
}