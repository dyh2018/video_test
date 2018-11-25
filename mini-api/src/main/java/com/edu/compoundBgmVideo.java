package com.edu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//合成bgm和视频
public class compoundBgmVideo {

    private  String ffmpegEXE;

    public compoundBgmVideo(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }
    //ffmpeg.exe -i 111.mp4 -i 222.mp3 -t 7 -y 333.mp4
    //ffmpeg.exe -i 222.mp4 -i 111.mp3 -t 7 -y 333.mp4
    //如果无法合成，那么可以更换一下两个文件的输入位置
    public void convertor(String videoinputPath, String mp3inputPath,double time,String videooutPath) throws IOException {
        List<String> command=new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(mp3inputPath);

        command.add("-i");
        command.add(videoinputPath);

        command.add("-t");
        command.add(String.valueOf(time));

        command.add("-y");
        command.add(videooutPath);
//        for(String c :command){
//            System.out.print(c);
//        }

        ProcessBuilder builder=new ProcessBuilder(command);
        Process process=builder.start();
        //释放内存
        InputStream inputStream=process.getErrorStream();
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        String line="";
        while((line=bufferedReader.readLine())!=null){
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
        compoundBgmVideo compoundBgmVideo =new compoundBgmVideo("D:\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe");
        compoundBgmVideo.convertor("D:\\ffmpeg\\ffmpeg\\bin\\111.mp4",
                "D:\\ffmpeg\\ffmpeg\\bin\\222.mp3",7,
                "D:\\ffmpeg\\ffmpeg\\bin\\test2.mp4");
    }
}
