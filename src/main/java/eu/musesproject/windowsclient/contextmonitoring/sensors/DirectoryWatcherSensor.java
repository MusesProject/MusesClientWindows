package eu.musesproject.windowsclient.contextmonitoring.sensors;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.contextmonitoring.ContextListener;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 * @author alirezaalizadeh
 * This class watches a directory (or tree) for changes to files.
 */

public class DirectoryWatcherSensor implements ISensor{

    private static final String TAG = AppSensor.class.getSimpleName();

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_DIRECTORY_WATCHER";

    // maximal number of how many background services are stored if a context event is fired
    private static final int MAX_SHOWN_BACKGROUND_SERVICES = 1;

    // time in milliseconds when the sensor polls information
    private static int OBSERVATION_INTERVALL = 100;

    // context property keys
    public static final String PROPERTY_KEY_ID 					= "id";
    public static final String PROPERTY_KEY_EVENT_NAME 			= "appname";
    public static final String PROPERTY_KEY_EVENT_PATH			= "appversion";

    private ContextListener listener;

    // stores all fired context events of this sensor
    private List<ContextEvent> contextEventHistory;


    // holds a value that indicates if the sensor is enabled or disabled
    private boolean sensorEnabled;

    private WatchService watcher;
    private Map<WatchKey,Path> keys;
    private boolean trace = false;
    private boolean recursive;
    private Path dir;

    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    public DirectoryWatcherSensor(Path dir, boolean recursive) {
        this.dir = dir;
        this.recursive = recursive;
        init();
    }

    public DirectoryWatcherSensor() {
        //default path should be set
        this.dir = Paths.get("C:/tmp/");
        init();
    }

    public void setDir(Path dir){
        this.dir = dir;
    }

    public void setRecursive(boolean recursive){
        this.recursive = recursive;
    }
    // initializes all necessary default values
    private void init() {
        sensorEnabled = false;
        contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
    }

    @Override
    public void enable() throws IOException {
        if (!sensorEnabled) {
            sensorEnabled = true;
            buildWatchService(this.dir, this.recursive);
            new EventObserver().backgroundProcess.run();
        }
    }

    @Override
    public void disable() {
        if(sensorEnabled) {
            sensorEnabled = false;
        }
    }


    /**
     * creates the context event for this sensor and saves it in the
     * context event history
     * @param eventName name of the currently active application
     * @param eventPath version of the currently active application
     */
    private void createContextEvent(String eventName, String eventPath) {
        // create the context event
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setType(TYPE);
        contextEvent.setTimestamp(System.currentTimeMillis());
        contextEvent.addProperty(PROPERTY_KEY_EVENT_NAME, eventName);
        contextEvent.addProperty(PROPERTY_KEY_EVENT_PATH, String.valueOf(eventPath));
        contextEvent.generateId();

        // add context event to the context event history
        contextEventHistory.add(contextEvent);
        if (contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
            contextEventHistory.remove(0);
        }
        if(listener != null) {
            listener.onEvent(contextEvent);
        }
    }

    /**
     * Register the given directory with the WatchService
     *
     * note: the proposed directory for watching should be
     * accessible by the current user, otherwise the AccessDenied
     * exception will be thrown
     *
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     *
     * note: the proposed directory and all its sub-directories for watching should be
     * accessible by the current user, otherwise the AccessDenied
     * exception will be thrown
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    private void buildWatchService (Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    private class EventObserver {

        public Thread backgroundProcess = new Thread(){
            public void run() {
                processEvents();
            }
        };

        /**
         * Process all events for keys queued to the watcher
         */
        private Void processEvents()  {
            while (sensorEnabled) {
                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return null;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    String eventName = event.kind().name().toString();
                    String eventPath = child.toString();
                    // print out event
                    System.out.format("%s: %s\n", eventName, eventPath);

                    // create a context event
                    createContextEvent(eventName, eventPath);

                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                            }
                        } catch (IOException x) {
                            // ignore
                        }
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
                try {
                    Thread.sleep(OBSERVATION_INTERVALL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void addContextListener(ContextListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeContextListener(ContextListener listener) {
        this.listener = listener;
    }

    @Override
    public ContextEvent getLastFiredContextEvent() {
        if(contextEventHistory.size() > 0) {
            return contextEventHistory.get(contextEventHistory.size() - 1);
        }
        else {
            return null;
        }
    }

    @Override
    public String getSensorType() {
        return TYPE;
    }
}
