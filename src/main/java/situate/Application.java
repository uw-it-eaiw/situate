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
package situate;

import org.springframework.util.StringUtils;
import situate.common.Utility;
import situate.tasks.DeployTask;
import situate.tasks.PrepareFileSystemTask;
import situate.tasks.PrepareUserPrincipalsTask;
import situate.tasks.VerifyFileSystemTask;
import situate.view.Deployment;
import situate.view.Template;
import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author James Renfro
 */
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan
public class Application implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory steps;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Running deployment");

        String environment = "workstation";

        String deploymentUrl = null;
        if (args != null && args.length > 0) {
            deploymentUrl = args[0];

            if (args.length > 1)
                environment = args[1];
        }

        if (StringUtils.isEmpty(deploymentUrl)) {
            System.err.println("Usage: situate [url]");
            System.exit(1);
        }

        LOGGER.info("Deployment url is " + deploymentUrl);

        Deployment deployment = Utility.deployment(deploymentUrl);

        LOGGER.info("Template url is " + deployment.getTemplateUrl());

        Template template = Utility.template(deployment.getTemplateUrl());

        deployment = Utility.deployment(deployment, template);

        Step verifyFileSystemStep = steps.get("verifyFileSystem")
                .tasklet(new VerifyFileSystemTask(deployment))
                .build();

        Step prepareUserPrincipalsStep = steps.get("prepareFileSystem")
                .tasklet(new PrepareUserPrincipalsTask(deployment))
                .build();

        Step prepareFileSystemStep = steps.get("prepareFileSystem")
                    .tasklet(new PrepareFileSystemTask(deployment, environment))
                    .build();

        Step deployStep = steps.get("deploy")
                    .tasklet(new DeployTask(deployment))
                    .build();

        Job job = jobBuilderFactory.get("deploy")
                .incrementer(new RunIdIncrementer())
                .start(verifyFileSystemStep)
                .next(prepareUserPrincipalsStep)
                .next(prepareFileSystemStep)
                .next(deployStep)
                .build();

        Map<String,JobParameter> parameters = new HashMap<String, JobParameter>();

        jobLauncher.run(job, new JobParameters(parameters));

        LOGGER.info("Deployment completed");
    }

    public static void main(String[] args) throws Exception {
        System.exit(SpringApplication.exit(SpringApplication.run(
                Application.class, args)));
    }

}
