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
package situate.enumeration;

import java.nio.file.attribute.PosixFilePermission;

/**
 * Maps external permission to system file permission
 *
 * @author James Renfro
 */
public enum Permission {
    OWNER_READ(PosixFilePermission.OWNER_READ),
    OWNER_WRITE(PosixFilePermission.OWNER_WRITE),
    OWNER_EXECUTE(PosixFilePermission.OWNER_EXECUTE),
    GROUP_READ(PosixFilePermission.GROUP_READ),
    GROUP_WRITE(PosixFilePermission.GROUP_WRITE),
    GROUP_EXECUTE(PosixFilePermission.GROUP_EXECUTE),
    OTHERS_READ(PosixFilePermission.OTHERS_READ),
    OTHERS_WRITE(PosixFilePermission.OTHERS_WRITE),
    OTHERS_EXECUTE(PosixFilePermission.OTHERS_EXECUTE);

    private PosixFilePermission posixFilePermission;

    private Permission(PosixFilePermission posixFilePermission) {
        this.posixFilePermission = posixFilePermission;
    }

    public PosixFilePermission getPosixFilePermission() {
        return posixFilePermission;
    }

}
