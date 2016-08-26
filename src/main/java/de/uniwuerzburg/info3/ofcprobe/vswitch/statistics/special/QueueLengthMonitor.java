/* 
 * Copyright 2016 christopher.metter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uniwuerzburg.info3.ofcprobe.vswitch.statistics.special;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import de.uniwuerzburg.info3.ofcprobe.vswitch.main.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special Statistics Module: Monitors the QueueLength, special calling, so not
 * possible via IStatistics Module
 *
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class QueueLengthMonitor {

    private static final Logger logger = LoggerFactory.getLogger(QueueLengthMonitor.class);

    /**
     * A list to save the QueueLengths
     */
    List<Integer> queueLengths;

    private String file;

    private String dpid;

    /**
     * Constructor, nothing special here
     *
     * @param config
     */
    public QueueLengthMonitor(Config config) {
        NumberFormat intervalFormatter = new DecimalFormat("#000");
        this.queueLengths = new ArrayList<>();
        this.dpid = intervalFormatter.format(config.getSwitchConfig().getDpid());
    }

    /**
     * add new QueueLength
     *
     * @param queueLength
     */
    public void newQueueLength(int queueLength) {
        logger.trace("[Switch#{}]: new queueLength: {}", queueLength);
        this.queueLengths.add(queueLength);
    }

    /**
     * Do the reportin'
     */
    public void report() {
        double queueLengthMean = meanGetter(this.queueLengths);
        logger.info("[Switch#{}]: QueueLength Mean in this Session: {}", this.dpid, queueLengthMean);
        writeToFile(queueLengthMean);
    }

    /**
     * Do the calculatin'
     */
    public void evaluate() {

    }

    private double meanGetter(List<Integer> list) {
        double mean = 0;
        for (Integer uni : list) {
            mean += uni;
        }
        mean = mean / list.size();
        if (Double.isNaN(mean)) {
            mean = 0.0;
        }
        return mean;
    }

    private void writeToFile(double queueLengthMean) {
        try {
            File filou = new File(this.file);
            if (!filou.getParentFile().exists()) {
                filou.getParentFile().mkdirs();
            }
            PrintWriter out = new PrintWriter(this.file);

            if (!this.queueLengths.isEmpty()) {
                for (Integer queueLength : this.queueLengths) {
                    out.print(queueLength + ";");
                }
            } else {
                out.print("0;");
            }
            out.print("\n");

            out.print(queueLengthMean);

            out.close();
        } catch (FileNotFoundException e) {
            logger.debug("[Switch#{}]: {}", this.dpid, e);
        }
    }

    public void setReportFile(String file) {
        this.file = file;

    }
}
