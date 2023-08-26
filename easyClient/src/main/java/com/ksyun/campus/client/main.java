package com.ksyun.campus.client;

import com.ksyun.campus.client.domain.ClusterInfo;
import com.ksyun.campus.client.util.ZKUtil;

import java.io.FileOutputStream;
import java.io.IOException;

public class main {
    public static void main(String arg[]) throws IOException, InterruptedException {
        EFileSystem ef = new EFileSystem();
        FSOutputStream aa = ef.create("A/B/C/1.txt");
        byte[] b ={ 1, 2, 3, 4, 5,15,5,15,5,5,};
        byte[] c ={ 1, 2, 3, 4, 5,15,5,15,5,5,};
        aa.write(b);
        aa.write(c);
        aa.close();
        System.out.println(123);
        Thread.sleep(10000);
        FSInputStream bb = ef.open("A/B/C/1.txt");
        byte[] out = new byte[20];
        bb.read(out);
        FileOutputStream fs = new FileOutputStream("out.bin");
        fs.write(out);
        fs.close();
//        ef.mkdir("A/B/C/D");
//
//        System.out.println(ef.delete("A/B/C/D"));
//        System.out.println(ef.delete("A/B/C/1.txt"));
    }
    }
