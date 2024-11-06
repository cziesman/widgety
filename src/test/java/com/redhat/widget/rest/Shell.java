/*****************************************************************************************
 * File: Shell.java
 *
 * Copyright (c) 2020 Omnitracs, LLC. All rights reserved. Confidential and Proprietary – Omnitracs,
 * Inc. This software may be subject to U.S. and international export, re-export, or transfer
 * ("export") laws. Diversion contrary to U.S. and international laws is strictly prohibited.
 ****************************************************************************************/

package com.redhat.widget.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cziesman
 */
public class Shell {

    private static final Logger LOG = LoggerFactory.getLogger(Shell.class);

    public static List<String> execute(final String cmd) throws InterruptedException, IOException {

        LOG.trace(cmd);
        ProcessBuilder builder = new ProcessBuilder()
                .command("sh", "-c", cmd)
                .directory(new File(System.getProperty("user.home")));

        Process process = builder.start();

        List<String> output = new ArrayList<>();

        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), output::add);
        Executors.newSingleThreadExecutor().submit(streamGobbler);

        boolean completed = process.waitFor(30, TimeUnit.SECONDS);
        assert completed;

        LOG.trace(output.toString());

        return output;
    }

    private static class StreamGobbler implements Runnable {

        private final InputStream inputStream;

        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {

            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {

            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }

    }

}
