import com.pardhasm.sieve.core.model.ApiDefinition;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ApiDefinitionTest {

    @Test
    public void testBuilderPattern() throws URISyntaxException {
        ArrayList<ApiDefinition.Target> targets = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("host", "127.0.0.1");
        map.put("port", 8080);
        map.put("httpType", "http");
        targets.add(new ApiDefinition.Target(map));

        ApiDefinition build = new ApiDefinition.Builder()
                .name("name")
                .pattern("/pattern/:a")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(targets).build();
        assertTrue(build.getPattern().pattern().equals("/pattern/([^/]+)"));
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
        ApiDefinition build = new ApiDefinition.Builder()
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
        ApiDefinition build = new ApiDefinition.Builder()
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
    public void testUtil() {
        assertTrue(ApiDefinition.removeSlashesAtBothEnds("/path").equalsIgnoreCase("path"));
        assertTrue(ApiDefinition.removeSlashesAtBothEnds("").equalsIgnoreCase(""));
        assertTrue(ApiDefinition.HttpType.HTTP.getValue().equalsIgnoreCase("http"));
        assertTrue(ApiDefinition.HttpType.HTTPS.getValue().equalsIgnoreCase("https"));
        assertTrue(ApiDefinition.removeSlashesAtBothEnds("/").equalsIgnoreCase(""));
        assertTrue(ApiDefinition.removeSlashesAtBothEnds("/a/").equalsIgnoreCase("a"));
    }


    @Test
    public void testBuilderPrototype() throws URISyntaxException {
        ApiDefinition base = new ApiDefinition.Builder()
                .name("name")
                .pattern("pattern")
                .globalRateLimit(1)
                .globalRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .userRateLimit(1)
                .userRateLimitUnit(TimeUnit.valueOf("SECONDS"))
                .targets(new ArrayList<>()).build();
        ApiDefinition build = new ApiDefinition.Builder().fromPrototype(base).build();
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
            ApiDefinition.checkNotNull(null);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

}
