package nl.tudelft.sem.springdatajpa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import nl.tudelft.sem.config.DatabaseTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@DataJpaTest
@ContextConfiguration(
    classes = {DatabaseTestConfig.class},
    loader = AnnotationConfigContextLoader.class
)
public class InMemoryDbTest {

    @Qualifier("dataSourceTest")
    @Autowired
    private transient DataSource dataSource;

    @Test
    void testIsDataSourceNotNull() {
        assertThat(dataSource).isNotNull();
    }

    @Test
    void testIsAnInMemoryDatabaseCreated() {
        assertThat(dataSource).isInstanceOfAny(EmbeddedDatabase.class);
    }
}
