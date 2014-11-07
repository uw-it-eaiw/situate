/*
 *  Copyright 2014 University of Washington Licensed under the
 *	Educational Community License, Version 2.0 (the "License"); you may
 *	not use this file except in compliance with the License. You may
 *	obtain a copy of the License at
 *
 *  http://www.osedu.org/licenses/ECL-2.0
 *
 *	Unless required by applicable law or agreed to in writing,
 *	software distributed under the License is distributed on an "AS IS"
 *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *	or implied. See the License for the specific language governing
 *	permissions and limitations under the License.
 */
package situate.tasks;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;
import situate.common.Utility;
import situate.view.Deployment;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import situate.view.Resource;

import java.io.*;
import java.util.List;

/**
 * @author James Renfro
 */
public class PrepareFileSystemTask implements Tasklet {

    private static final Logger LOGGER = Logger.getLogger(PrepareFileSystemTask.class);

    private final Deployment deployment;
    private final String environment;

    public PrepareFileSystemTask(Deployment deployment, String environment) {
        this.deployment = deployment;
        this.environment = environment;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Resource> resources = deployment.getResources();

        for (Resource resource : resources) {
            // If this resource needs to be
            if (resource.isCreate()) {
                if (!Utility.createDirectory(resource))
                    continue;
            }

            if (resource.isDeploy()) {
                UrlResource external = new UrlResource(resource.getUrl());
                InputStream input = external.getInputStream();
                OutputStream output = new FileOutputStream(resource.getPath());

                try {
                    IOUtils.copy(input, output);
                } finally {
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                }

                Utility.configureResource(resource);
            }
        }

        return RepeatStatus.FINISHED;
    }

}
