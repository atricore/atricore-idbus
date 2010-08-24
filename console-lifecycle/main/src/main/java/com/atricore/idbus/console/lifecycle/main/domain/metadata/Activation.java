package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class Activation extends Connection {
                                                                    
    private static final long serialVersionUID = 3889745220384784875L;

    private long id;

    private ExecutionEnvironment executionEnv;

    private ServiceProvider sp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ExecutionEnvironment getExecutionEnv() {
        return executionEnv;
    }

    public void setExecutionEnv(ExecutionEnvironment executionEnv) {
        this.executionEnv = executionEnv;
    }

    public ServiceProvider getSp() {
        return sp;
    }

    public void setSp(ServiceProvider sp) {
        this.sp = sp;
    }
}
