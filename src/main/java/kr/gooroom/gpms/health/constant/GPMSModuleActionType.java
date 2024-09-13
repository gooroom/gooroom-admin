package kr.gooroom.gpms.health.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GPMSModuleActionType {
    private GPMSModuleActionType() {
    }

    public static final String START = "start";
    public static final String SHUTDOWN = "shutdown";
    public static final String RESTART = "restart";

    public static final List<String> GPMS_ACTION_LIST = Collections
            .unmodifiableList(new ArrayList<>(Arrays.asList(START, SHUTDOWN, RESTART)));
}