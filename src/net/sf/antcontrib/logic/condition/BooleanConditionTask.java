/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.logic.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * Wraps a ConditionBase so that the If task can use standard Ant Conditions as
 * its evaluated boolean expression. Wrapping like this means that future additions
 * to ConditionBase will automatically be picked up without modifying this class.
 *
 * <p>Developed for use with Antelope, migrated to ant-contrib Oct 2003.
 *
 * @author     Dale Anson, danson@germane-software.com
 * @version $Revision: 1.3 $
 */
public class BooleanConditionTask extends TaskAdapter implements Condition {

    private BooleanConditionBase cb = new BooleanConditionBase();

    private String property = null;
    private String value = "true";


    public Object getProxy() {
      return cb;  
    }
    
    public void setProxy(Object proxy) {
      if (proxy instanceof BooleanConditionBase)
         cb = (BooleanConditionBase)proxy;
    }
    
    /**
     * The name of the property to set. Optional.
     */
    public void setProperty( String p ) {
        property = p;
    }

    /**
     * The value for the property to set, if condition evaluates to true.
     * Defaults to "true".
     */
    public void setValue( String v ) {
        value = v;
    }

    /**
     * Override {@link org.apache.tools.ant.Task#maybeConfigure
     * maybeConfigure} in a way that leaves the nested tasks
     * unconfigured until they get executed.
     *
     * @since Ant 1.5
     */
    public void maybeConfigure() throws BuildException {
        if ( isInvalid() ) {
            super.maybeConfigure();
        }
        else {
            getRuntimeConfigurableWrapper().maybeConfigure( getProject(), true );
        }
    }
    
    /**
     * Forwards to eval().
     */
    public void execute() throws BuildException {
        eval();
    }
    
    /**
     * Evaluates the condition object.
     * @return true or false, depending on the evaluation of the condition. 
     */
    public boolean eval() {
        maybeConfigure();
        if ( cb.getConditionCount() > 1 ) {
            throw new BuildException( "You must not nest more than one condition.");
        }
        if ( cb.getConditionCount() < 1 ) {
            throw new BuildException( "You must nest one condition.");
        }

        boolean b = cb.getFirstCondition().eval();
        if ( b && property != null )
            getProject().setNewProperty( property, value );
        return b;
    }

}
