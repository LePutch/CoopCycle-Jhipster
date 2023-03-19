package jerem.coopcycle.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import jerem.coopcycle.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
