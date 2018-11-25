package com.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//得到视频封面
public class getVideoCover {

    private  String ffmpegEXE;

    public getVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }
    //ffmpeg.exe -ss 00:00:01 -y  -i 111.mp4  -vframes 1 new.jpg
    public void convertor(String videoInputPath,String coverOutPath) throws IOException {
        List<String> commands=new ArrayList<>();
        commands.add(ffmpegEXE);
        commands.add("-ss");
        commands.add("00:00:01");
        commands.add("-y");
        commands.add("-i");
        commands.add(videoInputPath);
        commands.add("-vframes");
        commands.add("1");
        commands.add(coverOutPath);

        ProcessBuilder builder=new ProcessBuilder(commands);
        Process process=builder.start();

        InputStream inputStream=process.getErrorStream();
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        String Line="";
        while((Line=bufferedReader.readLine())!=null){
        }
        if(bufferedReader!=null){
            bufferedReader.close();
        }
        if(inputStreamReader!=null){
            inputStreamReader.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }

    }

    public static void main(String[] args) throws IOException {
        getVideoCover test=new getVideoCover("D:\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe");
        test.convertor("D:\\ffmpeg\\ffmpeg\\bin\\111.mp4",
                "D:\\ffmpeg\\ffmpeg\\bin\\new2.jpg");

    }
}
