package quo.vadis.megasys.utils.arangodb

import com.arangodb.ArangoCollection
import com.arangodb.ArangoDatabase
import com.arangodb.ArangoGraph
import com.arangodb.entity.CollectionEntity
import com.arangodb.entity.EdgeDefinition
import com.arangodb.entity.GraphEntity

fun ArangoDatabase.forceCreateCollection(name: String): Pair<ArangoCollection, CollectionEntity> {
  val collection = this.collection(name)
  if (collection.exists()) {
    collection.drop()
  }
  val entity = collection.create()
  return Pair(collection, entity)
}

fun ArangoDatabase.forceCreateGraph(name: String, edgeDefinitions: Collection<EdgeDefinition> ) : Pair<ArangoGraph, GraphEntity> {
  val graph = this.graph(name)
  if (graph.exists()) {
    graph.drop()
  }
  val entity = graph.create(edgeDefinitions)
  return Pair(graph, entity)
}