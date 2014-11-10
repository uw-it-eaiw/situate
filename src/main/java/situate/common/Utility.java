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
package situate.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.resources.Deployment_es;
import org.apache.commons.io.IOUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import situate.enumeration.Permission;
import situate.view.Command;
import situate.view.Deployment;
import situate.view.Template;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * @author James Renfro
 */
public class Utility {

    private static Map<String, PosixFilePermission> PERMISSION_MAP;

    static {
        PERMISSION_MAP = new HashMap<>();
        for (Permission permission : Permission.values()) {
            PERMISSION_MAP.put(permission.name(), permission.getPosixFilePermission());
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Utility.class);

    public static situate.view.Command command(Deployment deployment, situate.view.Command command) throws IOException {
        ExpressionParser parser = new SpelExpressionParser();

        File pidFile = new File(command.getPidFile());
        String pid = null;
        if (pidFile.exists()) {
            Reader reader = new FileReader(pidFile);
            try {
                pid = IOUtils.toString(reader);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        StandardEvaluationContext context = context(deployment, pid);

        String start = parse(parser, command.getStart(), context);
        String stop = parse(parser, command.getStop(), context);

        return new Command(command.getService(), start, stop, pidFile.getAbsolutePath(), pid);
    }

    public static InputStream compile(Deployment deployment, InputStream input) throws IOException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = context(deployment, "");

        return IOUtils.toInputStream(parse(parser, input, context));
    }

    public static situate.view.Resource resource(situate.view.Resource resource, StandardEvaluationContext context) {
        ExpressionParser parser = new SpelExpressionParser();
        String url = parse(parser, resource.getUrl(), context);
        String path = parse(parser, resource.getPath(), context);
        String user = parse(parser, resource.getUser(), context);
        String group = parse(parser, resource.getGroup(), context);
        String type = resource.getType();
        List<String> permissions = resource.getPermissions();
        boolean deploy  = resource.isDeploy();
        boolean create = resource.isCreate();
        boolean compile = resource.isCompile();

        return new situate.view.Resource(url, path, type, user, group, permissions, deploy, create, compile);
    }

    public static Deployment deployment(String url) throws IOException {
        Resource resource = new UrlResource(url);
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = resource.getInputStream();
        try {
            return mapper.reader(Deployment.class).readValue(input);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static StandardEvaluationContext context(Deployment deployment, String pid) throws IOException {
        Map<String, String> map = new HashMap<>();

        if (StringUtils.isNotEmpty(deployment.getPropertiesUrl())) {
            Resource propertiesResource = new UrlResource(deployment.getPropertiesUrl());

            if (propertiesResource.exists()) {
                InputStream input = propertiesResource.getInputStream();
                Properties properties = new Properties();
                try {
                    properties.load(input);
                } finally {
                    IOUtils.closeQuietly(input);
                }

                for (String propertyName : properties.stringPropertyNames()) {
                    if (properties.get(propertyName) != null)
                        map.put(propertyName, properties.get(propertyName).toString());
                }
            }
        }

        if (deployment.getProperties() != null)
            map.putAll(deployment.getProperties());

        map.put("name", deployment.getName());
        map.put("pid", pid);

        StandardEvaluationContext context = new StandardEvaluationContext(map);
        context.addPropertyAccessor(new MapAccessor());
        return context;
    }

    public static Deployment deployment(Deployment base, Template template) throws IOException {
        StandardEvaluationContext context = context(base, "");

        Deployment deployment = new Deployment();
        deployment.setName(base.getName());
        deployment.setProperties(base.getProperties());

        List<Command> commands = new ArrayList<>();
        if (base.getCommands() != null && !base.getCommands().isEmpty())
            commands.addAll(base.getCommands());

        List<situate.view.Resource> resources = new ArrayList<situate.view.Resource>();
        if (base.getResources() != null && !base.getResources().isEmpty())
            resources.addAll(base.getResources());

        if (template.getCommands() != null) {
            for (Command command : template.getCommands()) {
                commands.add(Utility.command(deployment, command));
            }
        }

        if (template.getResources() != null) {
            for (situate.view.Resource resource : template.getResources()) {
                resources.add(Utility.resource(resource, context));
            }
        }

        deployment.setResources(resources);
        return deployment;
    }

    public static Template template(String url) throws IOException {
        Resource resource = new UrlResource(url);
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = resource.getInputStream();
        try {
            return mapper.reader(Template.class).readValue(input);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static String parse(ExpressionParser parser, String expression, StandardEvaluationContext context) {
        if (StringUtils.isEmpty(expression))
            return null;

        return parser.parseExpression(expression, new TemplateParserContext("${", "}")).getValue(context, String.class);
    }

    public static String parse(ExpressionParser parser, InputStream input, StandardEvaluationContext context) throws IOException {
        if (input == null)
            return null;
        try {
            return parser.parseExpression(IOUtils.toString(input), new TemplateParserContext("${", "}")).getValue(context, String.class);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static void setAccess(File file, String user, String group, Set<PosixFilePermission> filePermissions) throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();

        Path path = file.toPath();
        Files.setOwner(path, lookupService.lookupPrincipalByName(user));

        if (StringUtils.isNotEmpty(group))
            Files.getFileAttributeView(path, PosixFileAttributeView.class).setGroup(lookupService.lookupPrincipalByGroupName(group));

        Files.setPosixFilePermissions(path, filePermissions);
    }

    /*
     * Creates a directory and sets the necessary permissions -- if the resource is a file,
     * then the directory that is created is the parent of the file, otherwise, it is the
     */
    public static boolean createDirectory(situate.view.Resource resource) throws IOException {
        boolean isFile = resource.getType().equals("file");

        File directory;

        if (isFile) {
            File file = new File(resource.getPath());
            directory = file.getParentFile();
        } else {
            directory = new File(resource.getPath());
        }

        if (!directory.exists() && !directory.mkdirs()) {
            LOGGER.error("Unable to create directories for " + directory.getAbsolutePath());
            return false;
        }

        // Ensure that access is correct for the parent directory
        Set<PosixFilePermission> directoryPermissions = new HashSet<>();
        List<String> permissions = resource.getPermissions();
        if (permissions != null) {
            for (String permission : permissions) {
                PosixFilePermission filePermission = PERMISSION_MAP.get(permission);
                if (filePermission != null)
                    directoryPermissions.add(filePermission);
            }

            // Need to have at least user and group execute on parent directories
            if (!isFile) {
                directoryPermissions.add(PosixFilePermission.OWNER_EXECUTE);

                if (StringUtils.isNotEmpty(resource.getGroup()))
                    directoryPermissions.add(PosixFilePermission.GROUP_EXECUTE);
            }
        }

        setAccess(directory, resource.getUser(), resource.getGroup(), directoryPermissions);
        return true;
    }

    public static void configureResource(situate.view.Resource resource) throws IOException {
        // Don't bother to configure empty paths
        if (StringUtils.isEmpty(resource.getPath()))
            return;

        File file = new File(resource.getPath());
        Set<PosixFilePermission> filePermissions = new HashSet<>();
        List<String> permissions = resource.getPermissions();
        if (permissions != null) {
            for (String permission : permissions) {
                PosixFilePermission filePermission = PERMISSION_MAP.get(permission);
                if (filePermission != null)
                    filePermissions.add(filePermission);
            }
        }
        setAccess(file, resource.getUser(), resource.getGroup(), filePermissions);
    }

    public static void verifyResource(situate.view.Resource resource) throws IOException {
        // Don't bother to verify empty paths
        if (StringUtils.isEmpty(resource.getPath()))
            return;

        File file = new File(resource.getPath());
        Path path = file.toPath();

        Set<PosixFilePermission> filePermissions = Files.getPosixFilePermissions(path);

        List<String> permissions = resource.getPermissions();
        if (permissions != null) {
            for (String permission : permissions) {
                PosixFilePermission filePermission = PERMISSION_MAP.get(permission);
                if (filePermission != null && !filePermissions.contains(filePermission))
                    throw new FileSystemException("This path should have the permission " + permission + " : " + resource.getPath());
            }
        }

        if (!Files.getFileAttributeView(path, PosixFileAttributeView.class).getOwner().getName().equals(resource.getUser()))
            throw new FileSystemException("This file should be owned by " + resource.getUser() + " : " + resource.getPath());

        GroupPrincipal group = Files.readAttributes(file.toPath(), PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();

        if (group != null && resource.getGroup() != null && !group.getName().equals(resource.getGroup()))
            throw new FileSystemException("This file should have group " + resource.getGroup() + " : " + resource.getPath());

    }

}
