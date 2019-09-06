package quo.vadis.megasys.utils.graphql

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters
import graphql.language.OperationDefinition
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import quo.vadis.megasys.utils.logger
import java.util.concurrent.atomic.AtomicLong

class GraphqlTransactionInstrumentation(private val transactionManager: PlatformTransactionManager, private val onlyMutation: Boolean = false) : SimpleInstrumentation() {
  companion object {
    val log = logger()
    val counter = AtomicLong()
  }

  override fun beginExecuteOperation(parameters: InstrumentationExecuteOperationParameters): InstrumentationContext<ExecutionResult> {
    val tx = TransactionTemplate(this.transactionManager)
    val operation = parameters.executionContext.operationDefinition.operation
    if (OperationDefinition.Operation.MUTATION != operation) {
      if (onlyMutation) {
        return SimpleInstrumentationContext()
      }
      tx.isReadOnly = true
    }
    val count = counter.incrementAndGet()
    log.debug("bigin transaction #${count}")
    val status = this.transactionManager.getTransaction(tx)

    return SimpleInstrumentationContext.whenDispatched { codeToRun  ->
      codeToRun.join()

      if (codeToRun.isCompletedExceptionally || codeToRun.get().errors.isNotEmpty()) {
        this.transactionManager.rollback(status)
        log.debug("rollback transaction #${count}")
      } else {
        this.transactionManager.commit(status)
        log.debug("commit transaction #${count}")
      }
    }
  }
}
