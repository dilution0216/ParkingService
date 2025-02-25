package org.dhicc.parkingserviceonboarding.logging;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggingRepository extends ElasticsearchRepository<LoggingDocument, String> {
}
