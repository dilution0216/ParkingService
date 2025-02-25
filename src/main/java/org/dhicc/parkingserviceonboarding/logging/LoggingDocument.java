package org.dhicc.parkingserviceonboarding.logging;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.time.Instant;
import java.util.Map;

@Document(indexName = "api-logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggingDocument {

    @Id
    private String id;
    private String method;
    private String uri;
    private Map<String, String> headers;
    private String requestBody;
    private int statusCode;
    private String responseBody;
    private long elapsedTime;
    private Instant timestamp;
}