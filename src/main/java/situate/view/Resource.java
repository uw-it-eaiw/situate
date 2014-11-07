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

import java.util.List;

/**
 * @author James Renfro
 */
public class Resource {

    private String url;
    private String label;
    private String path;
    private String type; // e.g. file or directory
    private String user;
    private String group;
    private List<String> permissions; // e.g. OWNER_READ, GROUP_EXECUTE, OTHERS_WRITE
    private boolean deploy;
    private boolean create;

    public Resource() {
        this(null, null, null, null, null, null, false, false);
    }

    public Resource(String url, String path, String type, String user, String group, List<String> permissions, boolean deploy, boolean create) {
        this.url = url;
        this.path = path;
        this.type = type;
        this.user = user;
        this.group = group;
        this.permissions = permissions;
        this.deploy = deploy;
        this.create = create;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

}
