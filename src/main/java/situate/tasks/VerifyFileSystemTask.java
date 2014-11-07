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

import situate.common.Utility;
import situate.view.Deployment;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import situate.view.Resource;

import java.util.List;

/**
 * @author James Renfro
 */
public class VerifyFileSystemTask implements Tasklet {

    private final Deployment deployment;

    public VerifyFileSystemTask(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Resource> resources = deployment.getResources();

        if (resources != null) {
            for (Resource resource : resources) {
                if (!resource.isDeploy() && !resource.isCreate())
                    Utility.verifyResource(resource);
            }
        }

        return RepeatStatus.FINISHED;
    }

}
