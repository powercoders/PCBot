package org.rsbot.script.task;

import java.util.concurrent.Future;

public interface Task extends Runnable {

    /**
     * Checks if the current task is still running.
     * @return
     */
    boolean isDone();

    /**
     * Stops the Task
     */
    void stop();

    /**
     * Waits for the task to finish
     */
    void join();

    /**
     * Provides the future for this Task
     * @param f The future
     */
    void init(Future<?> f);
}
