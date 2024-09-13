package kr.gooroom.gpms.health.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GPMSModuleType {
    private GPMSModuleType() {
    }

    public static final String GKM = "gkm";
    public static final String GLM = "glm";
    public static final String GRM = "grm";

    public static final List<String> GPMS_MODULE_LIST = Collections
            .unmodifiableList(new ArrayList<>(Arrays.asList(GKM, GLM, GRM)));
}