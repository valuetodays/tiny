package billy.tinyant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    /**
     * File that we are using for configuration
     */
    private File buildFile = new File("build.xml");

    public static void main(String[] args) {
        try {
            // 构造方法主要是处理命令行参数
            Main main = new Main();
            // 核心
            main.runBuild();

            System.exit(0);
        } catch (Throwable exc) {
            System.err.println(exc.getMessage());
            exc.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Executes the build.
     */
    private void runBuild() throws BuildException {
        // track when we started
        LOG.info("Buildfile: " + buildFile.getAbsolutePath());
        Project project = new Project();

        try {
//            addBuildListeners(project);
//            project.fireBuildStarted();
            project.init();
            project.setUserProperty("ant.file", buildFile.getAbsolutePath());

            // first use the ProjectHelper to create the project object
            // from the given build file.
            try {
                Class.forName("javax.xml.parsers.SAXParserFactory");
            } catch (NoClassDefFoundError ncdfe) {
                throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", ncdfe);
            } catch (ClassNotFoundException cnfe) {
                throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", cnfe);
            } catch (NullPointerException npe) {
                throw new BuildException("No JAXP compliant XML parser found. See http://java.sun.com/xml for the\nreference implementation.", npe);
            }

            ProjectHelper.configureProject(project, buildFile);

            // actually do some work
            project.executeTarget(project.getDefaultTarget());
        } catch (RuntimeException exc) {
            throw exc;
        }

    }

}
