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

import situate.view.Deployment;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

/**
 * @author James Renfro
 */
public class PrepareUserPrincipalsTask implements Tasklet {

    private final Deployment deployment;

    public PrepareUserPrincipalsTask(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();

        // FIXME: Should actually create the users if they don't exist

//        UserPrincipal runUser = lookupService.lookupPrincipalByName(deployment.getRunUser());
//        GroupPrincipal runGroup = lookupService.lookupPrincipalByGroupName(deployment.getRunGroup());
//        UserPrincipal deployUser = lookupService.lookupPrincipalByName(deployment.getDeployUser());
//        GroupPrincipal deployGroup = lookupService.lookupPrincipalByGroupName(deployment.getDeployGroup());

        return RepeatStatus.FINISHED;
    }



}
