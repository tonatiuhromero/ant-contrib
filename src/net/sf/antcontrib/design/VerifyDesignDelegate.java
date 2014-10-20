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



import org.apache.bcel.Constants;

import org.apache.bcel.classfile.*;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Project;

import org.apache.tools.ant.Task;

import org.apache.tools.ant.util.JAXPUtils;

import org.xml.sax.InputSource;

import org.xml.sax.SAXException;

import org.xml.sax.XMLReader;



import java.io.*;

import java.util.Enumeration;
import java.util.HashSet;

import java.util.jar.JarFile;

import java.util.zip.ZipEntry;



/** 

 *

 * @author dhiller

 */

public class VerifyDesignDelegate implements Log {



    private File designFile;

    private File jarFile;

    private boolean isCircularDesign = false;

    private Task task;



    private Design design;

    private HashSet primitives = new HashSet();

    public VerifyDesignDelegate(Task task) {

        this.task = task;
        primitives.add("B");
        primitives.add("C");
        primitives.add("D");
        primitives.add("F");
        primitives.add("I");
        primitives.add("J");
        primitives.add("S");
        primitives.add("Z");
    }

    

    public void setJar(File f) {

        jarFile = f;

    }

    

    public void setDesign(File f) {

        this.designFile = f;          

    }



    public void setCircularDesign(boolean isCircularDesign) {

        this.isCircularDesign = isCircularDesign;

    }

    

    public void execute() throws BuildException {

        if(!designFile.exists() || designFile.isDirectory())

            throw new BuildException("design attribute in verifydesign element specified an invalid file="+designFile);

        

        JarFile jar = null;

        try {

            XMLReader reader = JAXPUtils.getXMLReader();

            DesignFileHandler ch = new DesignFileHandler(this, designFile, isCircularDesign, task.getLocation());

            reader.setContentHandler(ch);

            //reader.setEntityResolver(ch);

            //reader.setErrorHandler(ch);

            //reader.setDTDHandler(ch);

        

            log("about to start parsing file='"+designFile+"'", Project.MSG_INFO);

            FileInputStream fileInput = new FileInputStream(designFile);

            InputSource src = new InputSource(fileInput);

            reader.parse(src);

            

            design = ch.getDesign();



            jar = new JarFile(jarFile);

            Enumeration en = jar.entries();

            while(en.hasMoreElements()) {

                ZipEntry entry = (ZipEntry)en.nextElement();

                if(entry.getName().endsWith(".class")) {

                    InputStream in = jar.getInputStream(entry);

                    verifyClassAdheresToDesign(design, in, entry.getName());

                }

            }

        } catch (SAXException e) {

            deleteJarFile(jar);

            if(e.getException()!= null && e.getException() instanceof RuntimeException)
            	throw (RuntimeException)e.getException();
            
            throw new BuildException("Problem parsing design file='"+designFile+"'. Reason:\n"+e, e);

        } catch (IOException e) {

            deleteJarFile(jar);

            throw new BuildException("IOException on design file='"+designFile+"'. attached:", e);

        } catch(RuntimeException e) {

            deleteJarFile(jar);

            throw e;

        } finally {

            try {

                if(jar != null)

                    jar.close();

            } catch(IOException e) {}           

        }

    }

    

    private void deleteJarFile(JarFile jar) {

        try {

            if(jar != null)

                jar.close();

        } catch(IOException e) {}

        

        log("Deleting jar file="+jarFile.getAbsolutePath()+" so you do not get tempted to use a jar that doesn't abide by the design", Project.MSG_INFO);

        boolean deleted = jarFile.delete();

        if(!deleted)

            jarFile.deleteOnExit();

    }

    

    

    private String className = "";

    private void verifyClassAdheresToDesign(Design d, InputStream in, String name) throws ClassFormatException, IOException {

        ClassParser parser = new ClassParser(in, name);

        JavaClass javaClass = parser.parse();

        className = javaClass.getClassName();



        d.setCurrentClass(className);

        

        ConstantPool pool = javaClass.getConstantPool();

        

        processConstantPool(pool);

        

        VisitorImpl visitor = new VisitorImpl(pool, this, d, task.getLocation());

        DescendingVisitor desc = new DescendingVisitor(javaClass, visitor);

        desc.visit();

    }



    private void processConstantPool(ConstantPool pool) {

        Constant[] constants = pool.getConstantPool();

        if(constants == null) {

            log("      constants=null", Project.MSG_VERBOSE);

            return;

        }

        

        log("      constants len="+constants.length, Project.MSG_VERBOSE);      

        for(int i = 0; i < constants.length; i++) {

            processConstant(pool, constants[i], i);

        }

    }

    

    private void processConstant(ConstantPool pool, Constant c, int i) {

        if(c == null) //don't know why, but constant[0] seems to be always null.

            return;



        log("      const["+i+"]="+pool.constantToString(c)+" inst="+c.getClass().getName(), Project.MSG_DEBUG); 

        byte tag = c.getTag();

        switch(tag) {

            //reverse engineered from ConstantPool.constantToString...

        case Constants.CONSTANT_Class:

            int ind   = ((ConstantClass)c).getNameIndex();

            c   = pool.getConstant(ind, Constants.CONSTANT_Utf8);

            String className = Utility.compactClassName(((ConstantUtf8)c).getBytes(), false);

            log("      classNamePre="+className, Project.MSG_DEBUG);

            if(className.startsWith("["))
                className = className.substring(1, className.length());

            String firstLetter = className.charAt(0)+"";
            if(primitives.contains(firstLetter))
            	return;

            log("      className="+className, Project.MSG_VERBOSE);

            design.checkClass(className);

            break;

        default:

                

        }

    }

    

    public static String getPackageName(String className) {

        String packageName = Package.DEFAULT;

        int index = className.lastIndexOf(".");

        if(index > 0)

            packageName = className.substring(0, index);

        //DEANDO: test the else scenario here(it is a corner case)...



        return packageName;

    }

    

    public void log(String msg, int level) {
    	//VerifyDesignTest.log(msg);
        task.log(msg, level);

    }

}

