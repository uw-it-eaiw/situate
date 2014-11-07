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
package situate.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author James Renfro
 */
public class Deployment implements Serializable {

    private String name;
    private String templateUrl;
    private String propertiesUrl;
    private Map<String, String> properties;
    private List<Resource> resources;
    private List<Command> commands;

    public Deployment() {

    }

    public Deployment(String name, String templateUrl, Map<String, String> properties, List<Resource> resources) {
        this.name = name;
        this.templateUrl = templateUrl;
        this.properties = properties;
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getPropertiesUrl() {
        return propertiesUrl;
    }

    public void setPropertiesUrl(String propertiesUrl) {
        this.propertiesUrl = propertiesUrl;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
}
