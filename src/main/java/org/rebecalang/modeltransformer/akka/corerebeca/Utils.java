package org.rebecalang.modeltransformer.akka.corerebeca;

import com.squareup.javapoet.JavaFile;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static void writeToFile(File destination, JavaFile javaFile) {
        try {
            javaFile.writeTo(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String PACKAGE_NAME = "org.rebecalang.cakka";
}
