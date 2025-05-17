package com.example.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class UsermanagementApplicationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UsermanagementApplication.class));

    @Test
    void contextLoads() {
        this.contextRunner
                .withPropertyValues(
                        "spring.datasource.url=jdbc:h2:mem:testdb",
                        "spring.datasource.username=sa",
                        "spring.datasource.password=password",
                        "spring.jpa.hibernate.ddl-auto=create-drop",
                        "spring.batch.job.enabled=false")
                .run(context -> {
                    // Context loads successfully if we get here
                });
    }
}
