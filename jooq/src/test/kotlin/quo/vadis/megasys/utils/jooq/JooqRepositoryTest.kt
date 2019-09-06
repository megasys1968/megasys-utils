package quo.vadis.megasys.utils.jooq

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions
import org.jooq.DSLContext
import org.jooq.Table
import org.jooq.UpdatableRecord
import org.jooq.impl.DAOImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import quo.vadis.megasys.utils.jooq.sql.*
import quo.vadis.megasys.utils.jooq.sql.tables.TestGroupTable
import quo.vadis.megasys.utils.jooq.sql.tables.TestUserTable
import quo.vadis.megasys.utils.jooq.sql.tables.daos.TestGroupDao
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestGroup2Entity
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestGroupEntity
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestUserEntity
import quo.vadis.megasys.utils.jooq.sql.tables.pojos.TestUserGroupEntity
import quo.vadis.megasys.utils.jooq.sql.tables.records.TestUserRecord

@ExtendWith(SpringExtension::class)
@SpringBootApplication
@SpringBootTest
@AutoConfigureEmbeddedDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestConfig::class)
@ActiveProfiles("test")
class JooqRepositoryTest {
  companion object {
    const val USER1_ID: Long = 1
    const val USER2_ID: Long = 2
    const val USER3_ID: Long = 3

    const val USER1_NAME = "user1"
    const val USER2_NAME = "user2"
    const val USER3_NAME = "user3"

    const val GROUP1_ID: Long = 1
    const val GROUP2_ID: Long = 2
    const val GROUP1_NAME = "group1"
    const val GROUP2_NAME = "group2"
  }

  @Autowired
  lateinit var testUserRepo: TestUserRepository

  @Autowired
  lateinit var testGroupRepo: TestGroupRepository

  @Autowired
  lateinit var testUserGroupRepo: TestUserGroupRepository

  @Autowired
  lateinit var dslContext: DSLContext


  @Order(0)
  @Test
  fun truncate() {
    testUserRepo.truncate(true)
    testGroupRepo.truncate(true)
    testUserGroupRepo.truncate(true)
  }

  @Order(100)
  @Test
  fun insert() {
    testUserRepo.insert(TestUserEntity(USER1_ID, USER1_NAME, null))
    testUserRepo.insert(TestUserEntity(USER2_ID, USER2_NAME, null))
    testUserRepo.insert(TestUserEntity(USER3_ID, USER3_NAME, null))

    testGroupRepo.insert(TestGroupEntity(GROUP1_ID, GROUP1_NAME, null))
    testGroupRepo.insert(TestGroupEntity(GROUP2_ID, GROUP2_NAME, null))

    testUserGroupRepo.insert(TestUserGroupEntity(USER1_ID, GROUP1_ID, null))
    testUserGroupRepo.insert(TestUserGroupEntity(USER2_ID, GROUP1_ID, null))

    testUserGroupRepo.insert(TestUserGroupEntity(USER1_ID, GROUP2_ID, null))
    testUserGroupRepo.insert(TestUserGroupEntity(USER3_ID, GROUP2_ID, null))
  }

  @Order(150)
  @Test
  fun save() {
    testUserGroupRepo.save(TestUserGroupEntity(USER1_ID, GROUP1_ID, "メモメモ"))
  }

  @Order(200)
  @Test
  fun select() {
    var result = testUserRepo.select(pageable = PageRequest.of(1, 2, Sort.by(Sort.Order.asc("userName"))))
    Assertions.assertThat(result).size().isEqualTo(1)
    Assertions.assertThat(result[0].userName).isEqualTo(USER3_NAME)

    result = testUserRepo.select(pageable = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("userName"))))
    Assertions.assertThat(result).size().isEqualTo(1)
    Assertions.assertThat(result[0].userName).isEqualTo(USER1_NAME)
  }

  @Order(201)
  @Test
  fun selectAlias() {
    val groupTable = Tables.TEST_GROUP2.rename(Tables.TEST_GROUP.name)
    val entity = dslContext.selectFrom(groupTable).where(groupTable.GROUP_ID.equal(GROUP1_ID)).fetchInto(TestGroup2Entity::class.java)
    println(entity)
  }

}
