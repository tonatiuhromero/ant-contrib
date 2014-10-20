/*
 * Copyright (c) 2001-2005 Ant-Contrib project.  All rights reserved.
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

import java.io.File;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.tools.ant.BuildFileTest;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

/**
 * BIG NOTE***************************************************
 * Always expect specific exceptions.  Most of these test cases when
 * first submitted were not and therefore were not testing what they said
 * they were testing.  Exceptions were being caused by other things and the
 * tests were still passing.  Now all tests expect a specific exception
 * so if any other is thrown we will fail the test case.
 * ************************************************************
 * 
 * Testcase for <propertycopy>.
 */
public class VerifyDesignTest extends BuildFileTest {

    private final static String REASON = "Should have failed design check with\nproper message and did not";
    private String baseDir = "test"+File.separator
        +"resources"+File.separator
        +"design"+File.separator;
    private String c = File.separator;
    
    public VerifyDesignTest(String name) {
        super(name);
    }
    
    public void setUp() {    
   
        configureProject("test/resources/design/verifydesign.xml");
        project.log("ASFDSADF", Project.MSG_INFO);
    }
    
    private static String s = "";
    
    public static void log(String msg) {
        s += msg+"\n";
    }
    
    public void tearDown() {
        executeTarget("cleanup");

        System.out.println("test log=\n"+s);
    }

    public void testArrayDepend() {
        String class1 = "mod.arraydepend.ClassDependsOnArray";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testArrayDepend", REASON, Design.getErrorMessage(class1, class2));
    }

    public void testArrayDepend2() {
        executeTarget("testArrayDepend2");
    }
    
    public void testArrayDepend3() {
        String class1 = "mod.arraydepend3.ClassDependsOnArray";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testArrayDepend3", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testCastDepend() {
        String class1 = "mod.castdepend.ClassDependsOnCast";
        String class2 = "mod.dummy.DummyInterface";
        expectSpecificBuildException("testCastDepend", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testCatchDepend() {
        String class1 = "mod.catchdepend.ClassDependsOnCatch";
        String class2 = "mod.dummy.DummyRuntimeException";
        expectSpecificBuildException("testCatchDepend", REASON, Design.getErrorMessage(class1, class2));
    }

    public void testDeclareJavaUtil() {
        executeTarget("testDeclareJavaUtil");
    }

    public void testDeclareJavaUtilFail() {
        String class1 = "mod.declarejavautil.ClassDependsOnJavaUtil";
        String class2 = "java.util.List";
        expectSpecificBuildException("testDeclareJavaUtilFail", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testDeclareJavax() {
        String class1 = "mod.declarejavax.ClassDependsOnJavax";
        String class2 = "javax.swing.JButton";
        expectSpecificBuildException("testDeclareJavax", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testDeclareJavaxPass() {
        executeTarget("testDeclareJavaxPass");
    }
    
    
    //tests to write

    //depend on java.util should pass by default
    //depend on java.util should fail after defining needDeclareTrue
    //depend on javax.swing should pass after needDeclareFalse
    
    //depend on dummy should pass after needDeclareFalse
    
    public void testFieldDepend() {
        String class1 = "mod.fielddepend.ClassDependsOnField";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testFieldDepend", REASON, Design.getErrorMessage(class1, class2));
    }

    public void testFieldRefDepend() {
        if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)
            || JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)
            || JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
            return;
        }
        
        String class1 = "mod.fieldrefdepend.ClassDependsOnReferenceInFieldDeclaration";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testFieldRefDepend", REASON, Design.getErrorMessage(class1, class2));
    }

    public void testInnerClassDepend() {
        String class1 = "mod.innerclassdepend.InnerClassDependsOnSuper$Inner";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testInnerClassDepend", REASON, Design.getErrorMessage(class1, class2));
    }

    public void testInstanceOfDepend() {
        String class1 = "mod.instanceofdepend.ClassDependsOnInstanceOf";
        String class2 = "mod.dummy.DummyInterface";
        expectSpecificBuildException("testInstanceOfDepend", REASON, Design.getErrorMessage(class1, class2));
    } 
    
    public void testInterfaceDepend() {
        String class1 = "mod.interfacedepend.ClassDependsOnInterfaceMod2";
        String class2 = "mod.dummy.DummyInterface";
        expectSpecificBuildException("testInterfaceDepend", REASON, Design.getErrorMessage(class1, class2));
    } 
    
    public void testLocalVarDepend() {
        String class1 = "mod.localvardepend.ClassDependsOnLocalVar";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testLocalVarDepend", REASON, Design.getErrorMessage(class1, class2));
    }   

    public void testLocalVarRefDepend() {
        if (JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_1)
            || JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_2)
            || JavaEnvUtils.isJavaVersion(JavaEnvUtils.JAVA_1_3)) {
            return;
        }
        String class1 = "mod.localvarrefdepend.ClassDependsOnLocalVariableReference";
        String class2 = "mod.dummy.DummyInterface";
        expectSpecificBuildException("testLocalVarRefDepend", REASON, Design.getErrorMessage(class1, class2));
    }  
    
    public void testNewDepend() {
        String class1 = "mod.newdepend.ClassDependsOnNew";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testNewDepend", REASON, Design.getErrorMessage(class1, class2));
    }  
    
    public void testNewDepend2() {
        String class1 = "mod.newdepend2.ClassDependsOnNewInField";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testNewDepend2", REASON, Design.getErrorMessage(class1, class2));
    }  
    
    public void testNoDebugOption() {
        String class1 = "mod.nodebugoption.ClassDependsOnLocalVar";
        expectSpecificBuildException("testNoDebugOption", REASON, VisitorImpl.getNoDebugMsg(class1));
    } 
    
    public void testParamDepend() {
        String class1 = "mod.paramdepend.ClassDependsOnParameter";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testParamDepend", REASON, Design.getErrorMessage(class1, class2));
    }  
    
    public void testPassLocalDepend() {
        executeTarget("testPassLocalDepend");
    }
    
    public void testPutStatic() {
    	executeTarget("testPutStatic");
    }
    
    public void testRecursion() {
        executeTarget("testRecursion");         
    }

    public void testRecursion2() {
        executeTarget("testRecursion2");        
    }

    public void testRecursion3() {
        executeTarget("testRecursion3");
    }
    
    public void testReturnValDepend() {
        String class1 = "mod.returnvaldepend.ClassDependsOnReturnValue";
        String class2 = "mod.dummy.DummyInterface";
        expectSpecificBuildException("testReturnValDepend", REASON, Design.getErrorMessage(class1, class2));
    }  
    
    public void testSignatureExceptionDepend() {
        String class1 = "mod.signatureexceptiondepend.ClassDependsOnExceptionInMethodSignature";
        String class2 = "mod.dummy.DummyException";
        expectSpecificBuildException("testSignatureExceptionDepend", REASON, Design.getErrorMessage(class1, class2));
    }  

    public void testStaticDepend() {
        String class1 = "mod.staticdepend.ClassDependsOnStatic";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testStaticDepend", REASON, Design.getErrorMessage(class1, class2));
    }    

    public void testStaticField2Depend() {
        String class1 = "mod.staticfield2depend.ClassDependsOnStaticField";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testStaticField2Depend", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testStaticFieldDepend() {       
        String class1 = "mod.staticfielddepend.ClassDependsOnStaticField";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testStaticFieldDepend", REASON, Design.getErrorMessage(class1, class2));
    }    

    public void testStaticFinalDepend() {
        //This is an impossible test since javac compiles the string constants into the code
        //losing any reference to the class that contains the constant...In this one instance,
        //verifydesign can't verify that constant imports don't violate the design!!!!
        //check out mod.staticfinaldepend.ClassDependsOnStaticField to see the code
        //that will pass the design even if it is violating it.         
        //      String class1 = "mod.staticfinaldepend.ClassDependsOnConstant";
        //      String class2 = "mod.dummy.DummyClass";
        //      expectSpecificBuildException("testStaticFinalDepend", REASON, Design.getErrorMessage(class1, class2));
    }
    
    public void testSuperDepend() {
        String s = File.separator;
        File f = new File("test"+s+"resources"+s+"design"+s+"build"+s+"jar"+s+"test.jar");
        
        //      executeTarget("testSuperDepend");
        String class1 = "mod.superdepend.ClassDependsOnSuperMod2";
        String class2 = "mod.dummy.DummyClass";
        expectSpecificBuildException("testSuperDepend", REASON, Design.getErrorMessage(class1, class2));
        
        //jar file should have been deleted
        assertTrue("jar file should not exist yet still does", !f.exists());
    }

    //    public void testMoreThanOneSrcDirInJavac() {
    //        executeTarget("");
    //    }
    
    public static void main(String[] args) {
        TestSuite suite = new TestSuite();
        suite.addTest(new VerifyDesignTest("testArrayDepend3"));
        TestRunner.run(suite);
    }
}
