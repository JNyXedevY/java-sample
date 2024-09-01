package org.example.domain;


import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.Objects;

@DynamoDbImmutable(builder = MovieActor.Builder.class)
public class MovieActor {
    private final String movieName;
    private final String actorName;
    private final String actingAward;
    private final Integer actingYear;
    private final String actingSchoolName;

    private MovieActor(Builder b){
        this.movieName = b.movieName;
        this.actorName = b.actorName;
        this.actingAward = b.actingAward;
        this.actingYear = b.actingYear;
        this.actingSchoolName = b.actingSchoolName;
    }

    public static Builder builder() { return new Builder(); }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("movie")
    public String getMovieName(){ return movieName; }

    @DynamoDbSortKey
    @DynamoDbAttribute("actor")
    public String getActorName(){ return actorName; }

    @DynamoDbSecondaryPartitionKey(indexNames = "acting_award_year")
    @DynamoDbAttribute("actingaward")
    public String getActingAward(){ return actingAward; }

    @DynamoDbSecondarySortKey(indexNames = {"acting_award_year", "movie_year"})
    @DynamoDbAttribute("actingyear")
    public Integer getActingYear(){ return actingYear; }

    @DynamoDbAttribute("actingschoolname")
    public String getActingSchoolName(){ return actingSchoolName; }

    public static final class Builder {
        private String movieName;
        private String actorName;
        private String actingAward;
        private Integer actingYear;
        private String actingSchoolName;

        private Builder() {}

        public Builder movieName(String movieName){ this.movieName = movieName; return this; }
        public Builder actorName(String actorName){ this.actorName = actorName; return this; }
        public Builder actingAward(String actingAward){ this.actingAward = actingAward; return this; }
        public Builder actingYear(Integer actingYear){ this.actingYear = actingYear; return this; }
        public Builder actingSchoolName(String actingSchoolName){ this.actingSchoolName = actingSchoolName; return this;}

        public MovieActor build(){ return new MovieActor(this); }
    }

    @Override
    public String toString() {
        return """
               MovieActor{
                 movieName = '%s'
                 actorName = '%s'
                 actingAward = '%s'
                 actingYear = '%s'
                 actingSchoolName = '%s'
               }
               """.formatted(movieName, actorName, actingAward, actingYear, actingSchoolName);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieActor that = (MovieActor) o;
        return Objects.equals(movieName, that.movieName)
                && Objects.equals(actorName, that.actorName)
                && Objects.equals(actingAward, that.actingAward)
                && Objects.equals(actingYear, that.actingYear)
                && Objects.equals(actingSchoolName, that.actingSchoolName);
    }
}
