import com.pardhasm.sieve.APIDefinition;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



public class APIDefinitionTest {

    @Test
    public void testBuilderPattern() throws URISyntaxException {
        APIDefinition build = new APIDefinition.Builder()
                .name("name")
                .pattern("pattern")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(new ArrayList<>()).build();
        assertTrue(build.getPattern().pattern().equals("/pattern"));
        assertNotNull(build.getPattern());
        assertNotNull(build.proxyHandler());
        assertNotNull(build.getTargets());
        assertNotNull(build.getGlobalRateLimit());
        assertNotNull(build.getGlobalRateLimitUnit());
        assertNotNull(build.getUserRateLimitUnit());
        assertNotNull(build.getUserRateLimit());
    }


    @Test
    public void testBuilderName() throws URISyntaxException {
        APIDefinition build = new APIDefinition.Builder()
                .name("name")
                .pattern("pattern")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(new ArrayList<>()).build();
        assertTrue(build.getName().equalsIgnoreCase("name"));
        assertNotNull(build.getName());
    }


    @Test
    public void testBuilderUL() throws URISyntaxException {
        APIDefinition build = new APIDefinition.Builder()
                .name("name")
                .pattern("pattern")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(new ArrayList<>()).build();
        assertTrue(build.getUserRateLimit() == 1l);
        assertNotNull(build.getUserRateLimit());
    }

    @Test
    public void testUtilPath() {
        assertTrue(APIDefinition.removeSlashesAtBothEnds("/path").equalsIgnoreCase("path"));
        assertTrue(APIDefinition.removeSlashesAtBothEnds("").equalsIgnoreCase(""));
        assertTrue(APIDefinition.HttpType.HTTP.getValue().equalsIgnoreCase("http"));
        assertTrue(APIDefinition.HttpType.HTTPS.getValue().equalsIgnoreCase("https"));
    }


    @Test
    public void testBuilderPrototype() throws URISyntaxException {
        APIDefinition base = new APIDefinition.Builder()
                .name("name")
                .pattern("pattern")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(new ArrayList<>()).build();
        APIDefinition build = new APIDefinition.Builder().fromPrototype(base).build();
        assertNotNull(build.getPattern());
        assertNotNull(build.proxyHandler());
        assertNotNull(build.getTargets());
        assertNotNull(build.getGlobalRateLimit());
        assertNotNull(build.getGlobalRateLimitUnit());
        assertNotNull(build.getUserRateLimitUnit());
        assertNotNull(build.getUserRateLimit());
    }

    @Test
    public void testException() {
        try {
            APIDefinition.checkNotNull(null);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

}
