/*
 * Copyright (c) 2004-2005 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.antcontrib.design;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

/**
 * @author dhiller
 */
public class VerifyDesign
        extends Task
        implements Log {

    private VerifyDesignDelegate delegate;

    public VerifyDesign() {
        delegate = new VerifyDesignDelegate(this);
    }

    public void setJar(File f) {
        delegate.setJar(f);
    }

    public void setDesign(File f) {
        delegate.setDesign(f);
    }

    public void setCircularDesign(boolean isCircularDesign) {
        delegate.setCircularDesign(isCircularDesign);
    }

    public void execute()
            throws BuildException {
        delegate.execute();
    }
}
