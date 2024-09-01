package org.example.infrastracture;

import org.example.domain.MovieActor;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.List;

public class MovieActorOnDynamoDb {
    private final String tableName = "MovieActor";
    private final DynamoDbClient dbClient;
    private final DynamoDbTable<MovieActor> table;

    public MovieActorOnDynamoDb(Builder b){
        this.dbClient = b.client;
        this.table = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(b.client)
                .build()
                .table(tableName, TableSchema.fromImmutableClass(MovieActor.class));
    }

    public static Builder builder(){ return new Builder(); }

    public static final class Builder {
        private DynamoDbClient client;

        public Builder client(DynamoDbClient client){ this.client = client; return this;}

        public MovieActorOnDynamoDb build(){ return new MovieActorOnDynamoDb(this); }
    }

    public void createTable(){
        table.createTable();
        try(DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dbClient).build()){
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName(tableName).build())
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

}
