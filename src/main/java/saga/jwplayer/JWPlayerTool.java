package saga.jwplayer;

import java.io.*;
import saga.Tool;
import saga.util.ArgList;

import static java.util.Arrays.asList;
import static saga.util.SystemOut.println;
import static saga.util.Equal.equal;
import static saga.util.ExceptionUtils.exception;
import static saga.util.SystemOut.print;
import static saga.util.TextUtils.alignRight;
import static saga.util.TextUtils.joinUsing;

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
        println("    print <video-ts-fragment-url>                  - print URL of whole chunklist");
        println("    curl  <video-ts-fragment-url>  <output-folder> - download chunklist and create bash scripts for video download");
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

        if (args.isEmpty()) {
            throw exception("Expected <output-folder>.");
        }
        String outputFolderName = args.removeHead();

        return doDownloadChunklist(urls, outputFolderName);
    }

    private int doDownloadChunklist(VideoUrls urls, String outputFolderName) throws Exception {

        createDirectory(outputFolderName);
        String dir = outputFolderName + "/";
        String videoName = outputFolderName;

        String chunklistFileName = videoName + ".m3u8";
        executeProcess("curl", urls.chunklistUrl, "-o", dir + chunklistFileName);
        println("");
        println("   See chunklist in file: " + chunklistFileName);

        String downloadScriptFileName = videoName + "-download.sh";
        String concatScriptFileName = videoName + "-concat.sh";
        String ffmpegScriptFileName = videoName + "-ffmpeg.cmd"; // no ffmpeg on my unix, only windows
        String outputVideoFileName = videoName + ".ts";
        int chunkCount = 0;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(dir + chunklistFileName));
                Writer downloadScript = new FileWriter(dir + downloadScriptFileName);
                Writer concatScript = new FileWriter(dir + concatScriptFileName);
                Writer ffmpegScript = new FileWriter(dir + ffmpegScriptFileName);
                ) {
            downloadScript.write("#!/bin/bash -e\n");
            concatScript.write("#!/bin/bash -e\n");
            concatScript.write("cat \\\n");
            ffmpegScript.write("ffmpeg -i \"concat");
            String ffmpegDelimiter = ":";
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("media_")) {
                    chunkCount += 1;
                    String fragmentUrl = "\"" + urls.directoryUrl + "/" + line + "\"";
                    String outputFileName = line;
                    downloadScript.write("curl " + fragmentUrl + " -o " + outputFileName + "\n");
                    concatScript.write(outputFileName + " \\\n");
                    ffmpegScript.write(ffmpegDelimiter + outputFileName);
                    ffmpegDelimiter = "|";
                }
            }
            concatScript.write("> \"" + outputVideoFileName + "\" \n");
            ffmpegScript.write("\" -c copy " + outputVideoFileName + "\n");
        }
        println("   Run download script:   " + downloadScriptFileName);

        String doAllScriptName = videoName + "-all.sh";
        try (Writer doAllScript = new FileWriter(dir + doAllScriptName)) {
            doAllScript.write("#!/bin/bash -e\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("echo .     chunk count: " + chunkCount + "\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("bash ./" + downloadScriptFileName + "\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("echo .     merging " + chunkCount + " chunks into \"" + outputVideoFileName + "\"\n");
            doAllScript.write("echo ............................\n");
            doAllScript.write("bash ./" + concatScriptFileName + "\n");
            doAllScript.write("#rm -f media_" + urls.chunklistId + "_*.ts \n");
        }
        println("   Or do all with:        bash " + doAllScriptName);
        println("");

        return 0;
    }

    private File createDirectory(String folderName) throws RuntimeException {
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!folder.exists() || !folder.isDirectory()) {
            throw exception("Failed to create directory: " + folderName);
        }
        return folder;
    }

    private void executeProcess(String... args) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String line = "";
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            print(line + "\n");
        }
        int rc = process.waitFor();
        if (rc != 0) {
            throw exception("Failed to execute: " + joinUsing(" ", args));
        }
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
