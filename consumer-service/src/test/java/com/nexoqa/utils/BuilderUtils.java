package com.nexoqa.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class BuilderUtils {

    public static String generateRandomString(int stringLength) {
        return RandomStringUtils.randomAlphabetic(stringLength);
    }

    public static Integer generateRandomInteger(int to, int from) {
        return RandomUtils.nextInt(to, from);
    }

}
