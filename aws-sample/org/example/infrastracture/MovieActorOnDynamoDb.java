package org.example.infrastracture;

import org.example.domain.MovieActor;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.List;

public class MovieActorOnDynamoDb {
    private final DynamoDbClient dbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<MovieActor> table;

    public MovieActorOnDynamoDb(Builder b){
        this.dbClient = b.dbClient;
        this.enhancedClient = b.enhancedClient;
        this.table = b.table;
    }

    public static Builder builder(){ return new Builder(); }

    public static final class Builder {
        private DynamoDbClient dbClient;
        private DynamoDbEnhancedClient enhancedClient;
        private DynamoDbTable<MovieActor> table;

        public Builder dbClient(DynamoDbClient dbClient){ this.dbClient= dbClient; return this;}
        public Builder enhancedClient(DynamoDbEnhancedClient enhancedClient){ this.enhancedClient = enhancedClient; return this; }
        public Builder table(DynamoDbTable<MovieActor> table){ this.table = table; return this; }

        public MovieActorOnDynamoDb build(){ return new MovieActorOnDynamoDb(this); }
    }

    public void createTable(){
        table.createTable();
        try(DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dbClient).build()){
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("MovieActor").build())
                    .matched();

            response.response().orElseThrow();
        }
    }

    public List<MovieActor> scan(){
        return table.scan().items().stream().sorted().toList();
    }

    public void putItem(MovieActor item){
        table.putItem(item);
    }

    public List<MovieActor> batchGetItem(String movieName, String actorName){
        ReadBatch readBatch = ReadBatch.builder(MovieActor.class)
                .mappedTableResource(table)
                .addGetItem(builder -> builder
                        .key(key -> key
                                .partitionValue(movieName)
                                .sortValue(actorName)
                        )
                )
                .build();
        BatchGetResultPageIterable resultPages = enhancedClient
                .batchGetItem(builder -> builder.readBatches(readBatch));
        return resultPages.resultsForTable(table).stream().toList();
    }

    public void batchWriteItem(List<MovieActor> items){
        List<WriteBatch> batches = items
                .stream()
                .map(v -> WriteBatch.builder(MovieActor.class)
                        .mappedTableResource(table)
                        .addPutItem(v)
                        .build()
                ).toList();
        BatchWriteResult result = enhancedClient.batchWriteItem(builder -> builder.writeBatches(batches));
        if (!result.unprocessedPutItemsForTable(table).isEmpty()) {
            System.out.println(result.unprocessedPutItemsForTable(table).stream().map(MovieActor::toString));
        }
    }

}
