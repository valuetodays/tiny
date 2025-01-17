package billy.tinyant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Central representation of an Ant project. This class defines a
 * Ant project with all of it's targets and tasks. It also provides
 * the mechanism to kick off a build using a particular target name.
 * <p>
 * This class also encapsulates methods which allow Files to be refered
 * to using abstract path names which are translated to native system
 * file paths at runtime as well as defining various project properties.
 *
 * @author duncan@x180.com
 */

public class Project {
    private static final Logger LOG = LoggerFactory.getLogger(Project.class);


    public static final int MSG_ERR = 0;
    public static final int MSG_WARN = 1;
    public static final int MSG_INFO = 2;
    public static final int MSG_VERBOSE = 3;

    // private set of constants to represent the state
    // of a DFS of the Target dependencies
    private static final String VISITING = "VISITING";
    private static final String VISITED = "VISITED";

    private String name;

    public static final String TOKEN_START = "@";
    public static final String TOKEN_END = "@";

    public static final String JAVA_1_1 = "1";

    private Hashtable properties = new Hashtable();
    private Hashtable userProperties = new Hashtable();
    private Hashtable references = new Hashtable();
    private String defaultTarget;
    private Hashtable taskClassDefinitions = new Hashtable();
    private Hashtable targets = new Hashtable();
    private File baseDir;

    /**
     * Initialise the project.
     * <p>
     * This involves setting the default task definitions and loading the
     * system properties.
     */
    public void init() throws BuildException {
//        detectJavaVersion();
        setProperty("ant.java.version", "1.3");

        String defs = "/defaults.properties";

        try {
            Properties props = new Properties();
            InputStream in = this.getClass().getResourceAsStream(defs);
            props.load(in);
            in.close();

            Enumeration enu = props.propertyNames();
            while (enu.hasMoreElements()) {
                String key = (String) enu.nextElement();
                String value = props.getProperty(key);
                try {
                    Class taskClass = Class.forName(value);
                    addTaskDefinition(key, taskClass);
                } catch (NoClassDefFoundError ncdfe) {
                    // ignore...
                } catch (ClassNotFoundException cnfe) {
                    // ignore...
                }
            }

            Properties systemP = System.getProperties();
            Enumeration e = systemP.keys();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                String value = (String) systemP.get(name);
                this.setProperty(name, value);
            }
        } catch (IOException ioe) {
            throw new BuildException("Can't load default task list");
        }
    }

    public void setProperty(String name, String value) {
        // command line properties take precedence
        if (null != userProperties.get(name))
            return;
        LOG.debug("Setting project property: " + name + " -> " +
                value, MSG_VERBOSE);
        properties.put(name, value);
    }

    public void setUserProperty(String name, String value) {
        LOG.debug("Setting ro project property: " + name + " -> " +
                value, MSG_VERBOSE);
        userProperties.put(name, value);
        properties.put(name, value);
    }

    public String getProperty(String name) {
        if (name == null) return null;
        String property = (String) properties.get(name);
        return property;
    }

    public String getUserProperty(String name) {
        if (name == null) return null;
        String property = (String) userProperties.get(name);
        return property;
    }

    public Hashtable getProperties() {
        return properties;
    }

    public void setDefaultTarget(String defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    // deprecated, use setDefault
    public String getDefaultTarget() {
        return defaultTarget;
    }

    // match basedir attribute in xml
    public void setBasedir(String baseD) throws BuildException {
        try {
            setBaseDir(new File(new File(baseD).getCanonicalPath()));
        } catch (IOException ioe) {
            String msg = "Can't set basedir " + baseDir + " due to " +
                    ioe.getMessage();
            throw new BuildException(msg);
        }
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        setProperty("basedir", baseDir.getAbsolutePath());
        String msg = "Project base dir set to: " + baseDir;
        LOG.debug(msg, MSG_VERBOSE);
    }


    public void addTaskDefinition(String taskName, Class taskClass) {
        String msg = " +User task: " + taskName + "     " + taskClass.getName();
        //LOG.info(msg);
        taskClassDefinitions.put(taskName, taskClass);
    }


    /**
     * This call expects to add a <em>new</em> Target.
     *
     * @param target     is the Target to be added to the current
     *                   Project.
     * @param targetName is the name to use for the Target
     * @throws BuildException if the Target already exists
     *                        in the project.
     * @see Project#addOrReplaceTarget to replace existing Targets.
     */
    public void addTarget(String targetName, Target target)
            throws BuildException {
        if (targets.get(targetName) != null) {
            throw new BuildException("Duplicate target: `" + targetName + "'");
        }
        addOrReplaceTarget(targetName, target);
    }


    /**
     * @param target     is the Target to be added/replaced in
     *                   the current Project.
     * @param targetName is the name to use for the Target
     */
    public void addOrReplaceTarget(String targetName, Target target) {
        String msg = " +Target: " + targetName;
        LOG.debug(msg, MSG_VERBOSE);
        target.setProject(this);
        targets.put(targetName, target);
    }

    public Hashtable getTargets() {
        return targets;
    }

    public Task createTask(String taskType) throws BuildException {
        Class c = (Class) taskClassDefinitions.get(taskType);

        if (c == null)
            throw new BuildException("Could not create task of type: " + taskType +
                    " because I can't find it in the list of task" +
                    " class definitions");
        try {
            Object o = c.newInstance();
            Task task = null;
            if (o instanceof Task) {
                task = (Task) o;
            } else {
                // "Generic" Bean - use the setter pattern
                // and an Adapter
                TaskAdapter taskA = new TaskAdapter();
                taskA.setProxy(o);
                task = taskA;
            }
            task.setProject(this);
            task.setTaskType(taskType);

            // set default value, can be changed by the user
            task.setTaskName(taskType);

            String msg = "   +Task: " + taskType;
            LOG.debug(msg, MSG_VERBOSE);
            return task;
        } catch (Exception e) {
            String msg = "Could not create task of type: "
                    + taskType + " due to " + e;
            throw new BuildException(msg);
        }
    }

    public void executeTarget(String targetName) throws BuildException {

        // sanity check ourselves, if we've been asked to build nothing
        // then we should complain

        if (targetName == null) {
            String msg = "No target specified";
            throw new BuildException(msg);
        }

        // Sort the dependency tree, and run everything from the
        // beginning until we hit our targetName.
        // Sorting checks if all the targets (and dependencies)
        // exist, and if there is any cycle in the dependency
        // graph.
        Vector sortedTargets = topoSort(targetName, targets);

        int curidx = 0;
        Target curtarget;

        do {
            curtarget = (Target) sortedTargets.elementAt(curidx++);
            runTarget(curtarget);
        } while (!curtarget.getName().equals(targetName));
    }

    public File resolveFile(String fileName) {
        // deal with absolute files
        if (fileName.startsWith("/")) return new File(fileName);
        if (fileName.startsWith(System.getProperty("file.separator")))
            return new File(fileName);

        // Eliminate consecutive slashes after the drive spec
        if (fileName.length() >= 2 &&
                Character.isLetter(fileName.charAt(0)) &&
                fileName.charAt(1) == ':') {
            char[] ca = fileName.replace('/', '\\').toCharArray();
            char c;
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < ca.length; i++) {
                if ((ca[i] != '\\') ||
                        (ca[i] == '\\' &&
                                i > 0 &&
                                ca[i - 1] != '\\')) {
                    if (i == 0 &&
                            Character.isLetter(ca[i]) &&
                            i < ca.length - 1 &&
                            ca[i + 1] == ':') {
                        c = Character.toUpperCase(ca[i]);
                    } else {
                        c = ca[i];
                    }

                    sb.append(c);
                }
            }

            return new File(sb.toString());
        }

        File file = new File(baseDir.getAbsolutePath());
        StringTokenizer tok = new StringTokenizer(fileName, "/", false);
        while (tok.hasMoreTokens()) {
            String part = tok.nextToken();
            if (part.equals("..")) {
                file = new File(file.getParent());
            } else if (part.equals(".")) {
                // Do nothing here
            } else {
                file = new File(file, part);
            }
        }

        try {
            return new File(file.getCanonicalPath());
        } catch (IOException e) {
            LOG.debug("IOException getting canonical path for " + file + ": " +
                    e.getMessage(), MSG_ERR);
            return new File(file.getAbsolutePath());
        }
    }

    /**
     * Does replacement on the given string using the given token table.
     *
     * @returns the string with the token replaced.
     */
    private String replace(String s, Hashtable tokens) {
        int index = s.indexOf(TOKEN_START);

        if (index > -1) {
            try {
                StringBuffer b = new StringBuffer();
                int i = 0;
                String token = null;
                String value = null;

                do {
                    token = s.substring(index + TOKEN_START.length(), s.indexOf(TOKEN_END, index + TOKEN_START.length() + 1));
                    b.append(s.substring(i, index));
                    if (tokens.containsKey(token)) {
                        value = (String) tokens.get(token);
                        LOG.debug("Replacing: " + TOKEN_START + token + TOKEN_END + " -> " + value, MSG_VERBOSE);
                        b.append(value);
                    } else {
                        b.append(TOKEN_START);
                        b.append(token);
                        b.append(TOKEN_END);
                    }
                    i = index + TOKEN_START.length() + token.length() + TOKEN_END.length();
                } while ((index = s.indexOf(TOKEN_START, i)) > -1);

                b.append(s.substring(i));
                return b.toString();
            } catch (StringIndexOutOfBoundsException e) {
                return s;
            }
        } else {
            return s;
        }
    }

    /**
     * returns the boolean equivalent of a string, which is considered true
     * if either "on", "true", or "yes" is found, ignoring case.
     */
    public static boolean toBoolean(String s) {
        return (s.equalsIgnoreCase("on") ||
                s.equalsIgnoreCase("true") ||
                s.equalsIgnoreCase("yes"));
    }

    // Given a string defining a target name, and a Hashtable
    // containing the "name to Target" mapping, pick out the
    // Target and execute it.
    public void runTarget(Target target)
            throws BuildException {

        try {
//            fireTargetStarted(target);
            target.execute();
//            fireTargetFinished(target, null);
        } catch (RuntimeException exc) {
//            fireTargetFinished(target, exc);
            throw exc;
        }
    }

    /**
     * Topologically sort a set of Targets.
     *
     * @param root    is the (String) name of the root Target. The sort is
     *                created in such a way that the sequence of Targets uptil the root
     *                target is the minimum possible such sequence.
     * @param targets is a Hashtable representing a "name to Target" mapping
     * @return a Vector of Strings with the names of the targets in
     * sorted order.
     * @throws BuildException if there is a cyclic dependency among the
     *                        Targets, or if a Target does not exist.
     */
    private final Vector topoSort(final String root, final Hashtable targets)
            throws BuildException {
        Vector ret = new Vector();
        Hashtable state = new Hashtable();
        Stack visiting = new Stack();

        // We first run a DFS based sort using the root as the starting node.
        // This creates the minimum sequence of Targets to the root node.
        // We then do a sort on any remaining unVISITED targets.
        // This is unnecessary for doing our build, but it catches
        // circular dependencies or missing Targets on the entire
        // dependency tree, not just on the Targets that depend on the
        // build Target.

        tsort(root, targets, state, visiting, ret);
        LOG.info("Build sequence for target `" + root + "` is " + ret);
        for (Enumeration en = targets.keys(); en.hasMoreElements(); ) {
            String curTarget = (String) (en.nextElement());
            String st = (String) state.get(curTarget);
            if (st == null) {
                tsort(curTarget, targets, state, visiting, ret);
            } else if (st == VISITING) {
                throw new RuntimeException("Unexpected node in visiting state: " + curTarget);
            }
        }
        LOG.info("Complete build sequence is " + ret);
        return ret;
    }

    // one step in a recursive DFS traversal of the Target dependency tree.
    // - The Hashtable "state" contains the state (VISITED or VISITING or null)
    // of all the target names.
    // - The Stack "visiting" contains a stack of target names that are
    // currently on the DFS stack. (NB: the target names in "visiting" are
    // exactly the target names in "state" that are in the VISITING state.)
    // 1. Set the current target to the VISITING state, and push it onto
    // the "visiting" stack.
    // 2. Throw a BuildException if any child of the current node is
    // in the VISITING state (implies there is a cycle.) It uses the
    // "visiting" Stack to construct the cycle.
    // 3. If any children have not been VISITED, tsort() the child.
    // 4. Add the current target to the Vector "ret" after the children
    //   have been visited. Move the current target to the VISITED state.
    //   "ret" now contains the sorted sequence of Targets upto the current
    //   Target.

    private final void tsort(String root, Hashtable targets,
                             Hashtable state, Stack visiting,
                             Vector ret)
            throws BuildException {
        state.put(root, VISITING);
        visiting.push(root);

        Target target = (Target) (targets.get(root));

        // Make sure we exist
        if (target == null) {
            StringBuffer sb = new StringBuffer("Target `");
            sb.append(root);
            sb.append("' does not exist in this project. ");
            visiting.pop();
            if (!visiting.empty()) {
                String parent = (String) visiting.peek();
                sb.append("It is used from target `");
                sb.append(parent);
                sb.append("'.");
            }

            throw new BuildException(new String(sb));
        }

        for (Enumeration en = target.getDependencies(); en.hasMoreElements(); ) {
            String cur = (String) en.nextElement();
            String m = (String) state.get(cur);
            if (m == null) {
                // Not been visited
                tsort(cur, targets, state, visiting, ret);
            } else if (m == VISITING) {
                // Currently visiting this node, so have a cycle
                throw makeCircularException(cur, visiting);
            }
        }

        String p = (String) visiting.pop();
        if (root != p) {
            throw new RuntimeException("Unexpected internal error: expected to pop " + root + " but got " + p);
        }
        state.put(root, VISITED);
        ret.addElement(target);
    }

    private static BuildException makeCircularException(String end, Stack stk) {
        StringBuffer sb = new StringBuffer("Circular dependency: ");
        sb.append(end);
        String c;
        do {
            c = (String) stk.pop();
            sb.append(" <- ");
            sb.append(c);
        } while (!c.equals(end));
        return new BuildException(new String(sb));
    }

    public void addReference(String name, Object value) {
        references.put(name, value);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
