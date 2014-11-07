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

/**
 * @author James Renfro
 */
public class Command {

    private String service;
    private String start;
    private String stop;
    private String pidFile;
    private String pid;

    public Command() {
        this(null, null, null, null, null);
    }

    public Command(String service, String start, String stop, String pidFile, String pid) {
        this.service = service;
        this.start = start;
        this.stop = stop;
        this.pidFile = pidFile;
        this.pid = pid;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getPidFile() {
        return pidFile;
    }

    public void setPidFile(String pidFile) {
        this.pidFile = pidFile;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
