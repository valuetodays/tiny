package billy.tinyant;

/**
 * Signals an error condition during a build.
 */
public class BuildException extends RuntimeException {
    private Throwable cause;

    private Location location = Location.UNKNOWN_LOCATION;

     public BuildException(String msg) {
        super(msg);
    }

    public BuildException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public BuildException(String msg, Throwable cause, Location location) {
        this(msg, cause);
        this.location = location;
    }

    public BuildException(Throwable cause) {
        super(cause.toString());
        this.cause = cause;
    }

    public BuildException(String msg, Location location) {
        super(msg);
        this.location = location;
    }

    public String toString() {
        return location.toString() + getMessage();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
