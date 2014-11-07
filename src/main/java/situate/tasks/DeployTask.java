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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.UrlResource;
import situate.common.Utility;
import situate.view.Command;
import situate.view.Deployment;
import situate.view.Resource;

import java.io.*;
import java.util.List;

/**
 * @author James Renfro
 */
public class DeployTask implements Tasklet {

    private static final Logger LOGGER = Logger.getLogger(DeployTask.class);

    private final Deployment deployment;

    public DeployTask(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Command> commands = deployment.getCommands();
        List<Resource> resources = deployment.getResources();

        for (Command command : commands) {
            String stopCommand = command.getStop();

            if (StringUtils.isNotEmpty(command.getPid())) {
                LOGGER.info("Stopping service " + command.getService() + " running as process " + command.getPid());

                Process stopProcess = Runtime.getRuntime().exec(stopCommand);
                stopProcess.waitFor();
                File pidFile = new File(command.getPidFile());
                if (pidFile.exists()) {
                    if (pidFile.delete())
                        LOGGER.info("Deleted pid file " + pidFile.getAbsolutePath());
                }
            }

            Runtime.getRuntime().exec(command.getStart());
            LOGGER.info("Starting service " + command.getService());
        }

        return RepeatStatus.FINISHED;
    }


}
