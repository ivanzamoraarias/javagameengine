package com.component;

import java.time.temporal.Temporal;

public class TimeBoundedComponent implements Component {

    Temporal period;
    Temporal deadline;

    TimeBoundedComponent(Temporal t, Temporal d) {
        this.period = t;
        this.deadline = d;
    }

    @Override
    public void update() {



    }
}
