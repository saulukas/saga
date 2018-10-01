package saga.jwplayer;

import java.io.*;
import saga.Tool;
import saga.util.ArgList;

import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
import static saga.util.SystemOut.print;

public class JWPlayerTool extends Tool {

    public JWPlayerTool() {
        super("jwp", "JW Player video download assistant");
    }

    @Override
    public int run(String[] argArray) throws Exception {
        ArgList args = ArgList.of(argArray);
        if (args.isEmpty()) {
            printUsage();
            return 0;
        }
        if (equal(args.head(), "print")) {
            args.removeHead();
            return printChunklistUrl(args);
        }
        if (equal(args.head(), "curl")) {
            args.removeHead();
            return downloadChunklistFile(args);
        }
        printUsage();
        return -1;
    }

    void printUsage() {
        println(name + " (c) saga 2018");
        println("");
        println("    " + oneLineDescription);
        println("");
        println("Parameters:");
        println("");
        println("    print <video-ts-fragment-url>   - print URL of whole chunklist");
        println("    curl  <video-ts-fragment-url>   - download and pring chunklist using unix curl");
        println("");
    }

    static class VideoUrls {
        String originalVideoFragmentUrl;
        String directoryUrl; // without trailing '/'
        String chunklistId;
        String chunklistUrl;
    }

    int printChunklistUrl(ArgList args) {
        if (args.isEmpty()) {
            throw exception("Expected <video-ts-fragment-url>.");
        }

        VideoUrls urls = extractVideoUrlsFrom(args.removeHead());

        println("curl \"" + urls.chunklistUrl + "\"");

        return 0;
    }

    int downloadChunklistFile(ArgList args) throws Exception {
        if (args.isEmpty()) {
            throw exception("Expected <video-ts-fragment-url>.");
        }

        VideoUrls urls = extractVideoUrlsFrom(args.removeHead());

        String chunklistFileName = "jwp-" + urls.chunklistId + "-chunkList.m3u8";
        int rc = download(urls.chunklistUrl, chunklistFileName);
        if (rc != 0) {
            throw exception("Failed to curl-download " + urls.chunklistUrl);
        }
        println("");
        println("   See chunklist in file: " + chunklistFileName);

        String downloadScriptFileName = "jwp-" + urls.chunklistId + "-download.sh";
        String concatScriptFileName = "jwp-" + urls.chunklistId + "-concat.sh";
        String outputVideoFileName = "jwp-" + urls.chunklistId + ".ts";
        int chunkCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(chunklistFileName));
                Writer downloadScript = new FileWriter(downloadScriptFileName);
                Writer concatScript = new FileWriter(concatScriptFileName);) {
            downloadScript.write("#!/bin/bash -e\n");
            concatScript.write("#!/bin/bash -e\n");
            concatScript.write("cat \\\n");
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("media_")) {
                    chunkCount += 1;
                    String fragmentUrl = "\"" + urls.directoryUrl + "/" + line + "\"";
                    String outputFileName = "\"" + line + "\"";
                    downloadScript.write("curl " + fragmentUrl + " -o " + outputFileName + "\n");
                    concatScript.write(outputFileName + " \\\n");
                }
            }
            concatScript.write("> \"" + outputVideoFileName + "\" \n");
        }
        println("   Run download script:   " + downloadScriptFileName);

        String doAllScriptName = "jwp-" + urls.chunklistId + "-all.sh";
        try (Writer doAllScript = new FileWriter(doAllScriptName)) {
            doAllScript.write("#!/bin/bash -e\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("echo .     chunk count: " + chunkCount + "\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("bash ./" + downloadScriptFileName + "\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("echo .     merging " + chunkCount + " chunks into \"" + outputVideoFileName + "\"\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("bash ./" + concatScriptFileName + "\n");
            doAllScript.write("rm -f media_" + urls.chunklistId + "_*.ts \n");
        }
        println("   Or do all with:        bash " + doAllScriptName);
        println("");

        return 0;
    }

    private int download(String inputUrl, String outputFileName) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("curl", inputUrl, "-o", outputFileName);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String line = "";
        print("\n");
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            print(line + "\n");
        }
        return process.waitFor();
    }

    static VideoUrls extractVideoUrlsFrom(String tsFragmentUrl) {
        // https://somehost.com/path/media_w12345_77.ts
        // https://somehost.com/path/chunklist_w12345.m3u8
        if (!tsFragmentUrl.endsWith(".ts")) {
            throw exception("Ivalid <video-ts-fragment-url>: expected '.ts' at the end");
        }
        int mediaPos = tsFragmentUrl.lastIndexOf("/media_");
        if (mediaPos == -1) {
            throw exception("Ivalid <video-ts-fragment-url>: expected '/media_'");
        }
        int chunklistIdPos = mediaPos + "/media_".length();
        int lastUnderscorePos = tsFragmentUrl.indexOf("_", chunklistIdPos);
        if (lastUnderscorePos == -1) {
            throw exception("Ivalid <video-ts-fragment-url>: expected '_xxx.ts' at the end");
        }
        String chunklistId = tsFragmentUrl.substring(chunklistIdPos, lastUnderscorePos);
        String chunklistUrl = tsFragmentUrl.substring(0, mediaPos)
                + "/chunklist_" + chunklistId + ".m3u8";

        VideoUrls videoUrls = new VideoUrls();
        videoUrls.originalVideoFragmentUrl = tsFragmentUrl;
        videoUrls.directoryUrl = tsFragmentUrl.substring(0, mediaPos);
        videoUrls.chunklistId = chunklistId;
        videoUrls.chunklistUrl = chunklistUrl;
        return videoUrls;
    }

}
