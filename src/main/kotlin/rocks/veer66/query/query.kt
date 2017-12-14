package rocks.veer66.query

import org.apache.jena.query.*
import org.apache.jena.query.spatial.EntityDefinition
import org.apache.jena.query.spatial.SpatialDatasetFactory
import org.apache.jena.riot.RDFDataMgr
import org.apache.lucene.store.FSDirectory
import java.io.File

fun initDataset(): Dataset {
    val entDef = EntityDefinition("entityField", "geoField")
    val idxFile = File("idx")
    val idxDir = FSDirectory.open(idxFile.toPath())
    val baseDataset = DatasetFactory.create();
    val dataset = SpatialDatasetFactory.createLucene(
            baseDataset,
            idxDir,
            entDef
    )
    if (dataset == null) {
        throw RuntimeException("Can init dataset")
    }
    return dataset
}

fun loadTtl(dataset: Dataset, ttlPath: String) {
    dataset.begin(ReadWrite.WRITE)
    val m = dataset.defaultModel
    RDFDataMgr.read(m, ttlPath)
    dataset.commit()
    dataset.end()
}

fun createDatasetFromTtl(ttlPath: String): Dataset {
    val dataset = initDataset()
    loadTtl(dataset, ttlPath)
    return dataset
}

fun query(dataset: Dataset, sparql: String): ResultSet? {
    dataset.begin(ReadWrite.READ)
    val q = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(q, dataset)
    val results = qExec.execSelect()
    dataset.end()
    return results
}
