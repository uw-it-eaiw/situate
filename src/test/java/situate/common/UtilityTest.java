package situate.common;

import com.google.common.collect.Lists;
import org.junit.Test;
import situate.enumeration.Permission;
import situate.view.Deployment;
import situate.view.Resource;
import situate.view.Template;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UtilityTest {

    @Test
    public void verifyTemplateCompile() throws IOException {
        List<String> permissions = Lists.newArrayList(Permission.OWNER_EXECUTE.name(), Permission.OWNER_READ.name());
        Resource resource = new Resource("", "/some/directory/${name}", "file", "${deployUser}", "${deployGroup}", permissions, false, false, false);
        Template template = new Template();
        template.setResources(Collections.singletonList(resource));

        Map<String, String> properties = new HashMap<>();
        properties.put("deployUser", "situate");
        properties.put("deployGroup", "deployment");

        Deployment base = new Deployment("example", null, properties, null);
        Deployment deployment = Utility.deployment(base, template);

        Resource deploymentResource = deployment.getResources().iterator().next();

        assertEquals("/some/directory/example", deploymentResource.getPath());
        assertEquals("situate", deploymentResource.getUser());
        assertEquals("deployment", deploymentResource.getGroup());
    }

    @Test
    public void verifyNormalResource() throws IOException {
        List<String> permissions = Lists.newArrayList(Permission.OWNER_EXECUTE.name(), Permission.OWNER_READ.name());
        Template template = new Template();

        Map<String, String> properties = new HashMap<>();
        properties.put("deployUser", "situate");
        properties.put("deployGroup", "deployment");

        Resource specificResource = new Resource("", "/some/test/path", "file", "testuser", "testgroup", permissions, false, false, false);

        Deployment base = new Deployment("example", null, properties, Collections.singletonList(specificResource));
        Deployment deployment = Utility.deployment(base, template);

        Resource deploymentResource = deployment.getResources().iterator().next();

        assertEquals("/some/test/path", deploymentResource.getPath());
        assertEquals("testuser", deploymentResource.getUser());
        assertEquals("testgroup", deploymentResource.getGroup());
    }

}