package situate.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class DeploymentTest {

    @Test
    public void serializeDeployment() throws Exception {
        Deployment deployment = new Deployment();
        deployment.setName("example");
        ObjectMapper mapper = new ObjectMapper();
        File tmp = File.createTempFile("deployment", ".json");
        tmp.deleteOnExit();
        mapper.writerWithDefaultPrettyPrinter().writeValue(tmp, deployment);

        Deployment deserialized = mapper.reader(Deployment.class).readValue(tmp);
        assertEquals("example", deserialized.getName());
    }

}