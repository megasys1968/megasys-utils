package quo.vadis.megasys.utils.jooq

import org.apache.commons.lang3.StringUtils
import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GenerationTool
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.Definition
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import java.sql.Connection

fun generateCode(connection: Connection, packageName: String,
                 directory: String = "src/main/kotlin",
                 excludes: String = StringUtils.EMPTY,
                 appendForcedTypes: List<ForcedType>? = null,
                 schema: String = "public") {
  val config = Configuration()
    .withGenerator(
      Generator()
        .withStrategy(
          Strategy().withName(quo.vadis.megasys.utils.jooq.GeneratorStrategy::class.java.canonicalName)
        )

        .withTarget(
          Target()
            .withPackageName(packageName)
            .withDirectory(directory))
        .withDatabase(
          Database()
            .withInputSchema(schema)
            .withOutputSchemaToDefault(true)
            .withExcludes(excludes)
            .withForcedTypes(appendForcedTypes))
        .withGenerate(
          Generate()
            .withPojos(true)
            .withDaos(true)
            .withInterfaces(true)
            .withGeneratedAnnotation(false)
            .withJavaTimeTypes(true))
    )
  val generator = GenerationTool()
  generator.setConnection(connection)
  generator.run(config)
}

class GeneratorStrategy: DefaultGeneratorStrategy() {
  override fun getJavaClassName(definition: Definition?, mode: GeneratorStrategy.Mode?): String {
    return super.getJavaClassName(definition, mode)
      .let {
        when(mode) {
          GeneratorStrategy.Mode.DEFAULT -> "${it}Table"
          GeneratorStrategy.Mode.POJO -> "${it}Entity"
          else -> it
        }
      }
  }
}