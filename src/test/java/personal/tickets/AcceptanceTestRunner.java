package personal.tickets;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // feature 파일 경로
@ConfigurationParameter(
        key = "cucumber.glue",
        value = "personal.tickets.steps, personal.tickets.config" // Step Definition 및 Configuration 경로
)
public class AcceptanceTestRunner {
}
