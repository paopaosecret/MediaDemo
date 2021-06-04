package com.example.media.audio;

import android.media.AudioFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavUtils {

    /**
     * 将PCM文件转换为WAV格式
     *
     * @param pcmFilePath
     * @param wavFilePath
     * @param sampleRate        采样率
     * @param channelConfig     音频通道配置：MONO 单声道
     * @param audioFormat       每个采样率的位数：16bit、8bit
     */
    public static void pcmToWav(String pcmFilePath, String wavFilePath, int sampleRate, int channelConfig, int audioFormat){
        File wavFile = new File(wavFilePath);
        if(wavFile.exists()){
            wavFile.delete();
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream =null;
        try {
            wavFile.createNewFile();
            fileInputStream = new FileInputStream(pcmFilePath);
            fileOutputStream = new FileOutputStream(wavFilePath);

            long pcmByteLength = fileInputStream.getChannel().size();
            long wavByteLength = pcmByteLength + 44;

            //TODO 1、WAV文件写入WAV头格式
            addWavHeader(fileOutputStream, pcmByteLength, wavByteLength, sampleRate, channelConfig, audioFormat);

            //TODO 2、WAV文件写入PCM数据
            byte[] buffer = new byte[10240];
            while(fileInputStream.read(buffer) != -1){
                fileOutputStream.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * WAV 文件格式头
     *
     * 偏移地址  字节数    数据类型       字段名称	        字段说明
     * 00H	    4	    字符	        文档标识	        大写字符串"RIFF",标明该文件为有效的 RIFF 格式文档。
     * 04H	    4	    长整型数	    文件数据长度	    从下一个字段首地址开始到文件末尾的总字节数。该字段的数值加 8 为当前文件的实际长度。
     * 08H	    4	    字符	        文件格式类型	    所有 WAV 格式的文件此处为字符串"WAVE",标明该文件是 WAV 格式文件。
     * 0CH	    4	    字符	        格式块标识	    小写字符串,"fmt "。
     * 10H	    4	    长整型数	    格式块长度。	    其数值不确定,取决于编码格式。可以是 16、 18 、20、40 等。(见表 2)
     * 14H	    2	    整型数	    编码格式代码。	常见的 WAV 文件使用 PCM 脉冲编码调制格式,该数值通常为 1。(见表 3)
     * 16H	    2	    整型数	    声道个数	        单声道为 1,立体声或双声道为 2
     * 18H	    4	    长整型数	    采样频率	        每个声道单位时间采样次数。常用的采样频率有 11025, 22050 和 44100 kHz。
     * 1CH	    4	    长整型数	    数据传输速率,	    该数值为:声道数×采样频率×每样本的数据位数/8。播放软件利用此值可以估计缓冲区的大小。
     * 20H	    2	    整型数	    数据块对齐单位	采样帧大小。该数值为:声道数×位数/8。播放软件需要一次处理多个该值大小的字节数据,用该数值调整缓冲区。
     * 22H	    2	    整型数	    采样位数	        存储每个采样值所用的二进制数位数。常见的位数有 4、8、12、16、24、32
     * 24H	 	 	 	对基本格式块的扩充部分(详见扩展格式块,格式块的扩充)
     *
     *
     * @param fileOutputStream
     * @param pcmByteLength
     * @param wavByteLength
     * @param sampleRate        采样率
     * @param channelConfig     音频通道配置：MONO 单声道
     * @param audioFormat       每个采样率的位数：16bit、8bit
     */
    private static void addWavHeader(FileOutputStream fileOutputStream, long pcmByteLength, long wavByteLength, int sampleRate, int channelConfig, int audioFormat) {
        byte[] header = new byte[44];

        //TODO RIFF/WAVE header chunk RIFF + WAV文件长度
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (wavByteLength & 0xff);
        header[5] = (byte) ((wavByteLength >> 8) & 0xff);
        header[6] = (byte) ((wavByteLength >> 16) & 0xff);
        header[7] = (byte) ((wavByteLength >> 24) & 0xff);

        //TODO WAVE
        header[8]  = 'W';
        header[9]  = 'A';
        header[10] = 'V';
        header[11] = 'E';

        //TODO 'f m t'
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        //TODO fmt的大小
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        //TODO 编码格式
        header[20] = 1;
        header[21] = 0;

        //TODO 声道数目
        int channelSize = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        header[22] = (byte)channelSize;
        header[23] = 0;

        //TODO 采样频率
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);

        //TODO 每秒传输速度
        long byteRate = audioFormat * sampleRate * channelSize;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);

        //TODO 采样帧大小 该数值为:声道数×位数/8
        header[32] = (byte)(channelConfig * audioFormat / 8);
        header[33] = 0;

        //TODO 每个采样值的存储位数
        header[34] = (byte)audioFormat;
        header[35] = 0;

        //TODO data chunk
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        //TODO pcm字节数
        header[40] = (byte) (pcmByteLength & 0xff);
        header[41] = (byte) ((pcmByteLength >> 8) & 0xff);
        header[42] = (byte) ((pcmByteLength >> 16) & 0xff);
        header[43] = (byte) ((pcmByteLength >> 24) & 0xff);

        try {
            fileOutputStream.write(header,0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
