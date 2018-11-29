/*******************************************************************************
 * Copyright 2015 The Odysseus Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.uniol.inf.is.odysseus.test.component;

import de.uniol.inf.is.odysseus.test.StatusCode;

/**
 * @author Christian Kuka <christian@kuka.cc>
 * @version $Id$
 *
 */
public class TestReport {
    public String name;
    public StatusCode statusCode;
    public long time;
    public Throwable error;
    private Report[] reports;

    /**
     * Class constructor.
     *
     * @param name
     * @param size
     */
    public TestReport(String name, int size) {
        this.name = name;
        this.reports = new Report[size];
    }

    /**
     * @param time
     */
    public void setDuration(long time) {
        this.time = time;
    }

    /**
     * @param statusCode
     */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @param error
     */
    public void setError(Throwable error) {
        this.error = error;
    }

    /**
     * @param id
     * @param statusCode
     */
    public void setStatusCode(int id, StatusCode statusCode) {
        reports[id].statusCode = statusCode;
    }

    /**
     * @param id
     * @param error
     */
    public void setError(int id, Throwable error) {
        reports[id].error = error;
    }

    /**
     * @param id
     * @param time
     */
    public void setDuration(int id, long time) {
        reports[id].time = time;
    }

    public void set(int id, String name, String query) {
        Report report = new Report();
        this.reports[id] = report;
        this.reports[id].name = name;
        this.reports[id].query = query;
    }

    /**
     * @return
     */
    public Report[] getReports() {
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (error != null) {
            sb.append(String.format("%7s %62s %7dms %s", statusCode, name, time / 1000000, error.getMessage())).append("\n");
        }
        else {
            sb.append(String.format("%7s %62s %7dms", statusCode, name, time / 1000000)).append("\n");
        }
        for (int i = 0; i < 86; i++) {
            sb.append("-");
        }
        sb.append("\n");
        for (Report subReport : getReports()) {
            if (subReport != null) {
                if (subReport.error != null) {
                    sb.append(String.format("-%23s %45s %7dms %s", subReport.statusCode, subReport.name, subReport.time / 1000000, subReport.error.getMessage())).append("\n");
                }
                else {
                    sb.append(String.format("-%23s %45s %7dms", subReport.statusCode, subReport.name, subReport.time / 1000000)).append("\n");
                }
            }
        }
        for (int i = 0; i < 86; i++) {
            sb.append("-");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static class Report {
        public String name;
        public String query;
        public StatusCode statusCode;
        public long time;
        public Throwable error;
    }

}
