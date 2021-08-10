package org.firebirdsql.testcontainers;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.example.cayenne.persistent.Artist;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.LocalDate;

public class CayenneContextFunctionalTest {
    @ClassRule
    public static FirebirdContainer<?> firebirdContainer = new FirebirdContainer<>("jacobalberty/firebird");

    private ServerRuntime cayenneRuntime;

    @Before
    public void initRuntime(){
        cayenneRuntime = ServerRuntime.builder()
                .url(firebirdContainer.getJdbcUrl())
                .user(firebirdContainer.getUsername())
                .password(firebirdContainer.getPassword())
                .jdbcDriver(firebirdContainer.getDriverClassName())
                .addConfig("cayenne-project.xml")
                .build();
    }

    @Test
    public void whenSelectQueryExecuted_thenResulstsReturned() {
        ObjectContext context = cayenneRuntime.newContext();
        Artist artist = context.newObject(Artist.class);
        artist.setName("name");
        artist.setDateOfBirth(LocalDate.now());
        context.commitChanges();
    }
}
