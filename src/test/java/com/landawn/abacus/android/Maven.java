package com.landawn.abacus.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.StringUtil;

public class Maven {

    public static void main(String[] args) throws Exception {
        N.println(new File(".").getAbsolutePath());

        final String sourceVersion = "0.0.1-SNAPSHOT";
        String targetVersion = null;

        for (String line : IOUtil.readAllLines(new File("./pom.xml"))) {
            if (line.indexOf("<version>") > 0 && line.indexOf("</version>") > 0) {
                targetVersion = StringUtil.substringBetween(line, "<version>", line.indexOf("</version>"));
                break;
            }
        }

        final String commonMavenPath = "./maven/";
        final String sourcePath = commonMavenPath + sourceVersion;
        final String targetPath = commonMavenPath + targetVersion;
        final File sourceDir = new File(sourcePath);
        final File targetDir = new File(targetPath);

        IOUtil.deleteAllIfExists(targetDir);

        targetDir.mkdir();

        IOUtil.copy(sourceDir, targetDir);

        for (File f : IOUtil.listFiles(new File("./target/"))) {
            if (f.getName().startsWith("abacus-android") && f.getName().endsWith(".jar")) {
                IOUtil.copy(f, targetDir);
            }
        }

        for (File file : IOUtil.listFiles(targetDir)) {
            IOUtil.renameTo(file, file.getName().replace(sourceVersion, targetVersion));
        }

        for (File file : IOUtil.listFiles(targetDir)) {
            if (file.getName().endsWith(".pom") || file.getName().endsWith(".xml") || file.getName().endsWith(".txt")) {
                final List<String> lines = IOUtil.readAllLines(file);
                final List<String> newLines = new ArrayList<>(lines.size());

                for (String line : lines) {
                    newLines.add(line.replaceAll(sourceVersion, targetVersion));
                }

                IOUtil.writeLines(file, newLines);
            }
        }

        System.exit(0);
    }

}
