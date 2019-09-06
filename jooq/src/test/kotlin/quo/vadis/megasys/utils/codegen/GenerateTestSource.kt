package quo.vadis.megasys.utils.codegen

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import quo.vadis.megasys.utils.jooq.JooqRepositoryTest
import quo.vadis.megasys.utils.jooq.generateCode
import javax.sql.DataSource

@ExtendWith(SpringExtension::class)
@SpringBootApplication
@SpringBootTest
@AutoConfigureEmbeddedDatabase
class GenerateTestSource {
  @Autowired
  lateinit var flywayProperties: FlywayProperties
  @Autowired
  lateinit var dataSource: DataSource

  @Test
  fun genarate() {
    generateCode(connection = dataSource.connection,
      packageName = "${JooqRepositoryTest::class.java.packageName}.sql",
      directory = "src/test/kotlin",
      excludes = flywayProperties.table)
  }
}
