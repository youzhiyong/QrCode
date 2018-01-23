package com.yzy.task;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Random;

public class Main {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 定义二维码的样式 高宽
    private static final int QRCODE_SIZE = 300;
    // LOGO的宽度
    private static final int WIDTH = 60;
    // LOGO的长度
    private static final int HEIGHT = 60;

    /**
     * 生成二维码
     * @param content
     * @param imgPath
     * @param needCompress
     * @return
     * @throws Exception
     */
    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  //设置二维码的容错等级
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);   //字符编码
        hints.put(EncodeHintType.MARGIN, 1); //边距
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        //将图片插入二维码中
        insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println(imgPath+"  图片不存在");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // ������С���ͼ
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    public static void main(String[] args) throws Exception {

        //链接
        String content = "http://www.helperok.com/";
        //需要插入的图片路径
        String imgPath = "D:\\logo.jpg";
        //二维码的输出路径
        String destPath = "D:\\";
        //是否对插入的图片进行压缩
        boolean needCompress = true;

        //调用API生成二维码图片
        BufferedImage image = createImage(content, imgPath,
                needCompress);


        File filePath =new File(destPath);
        //检测二维码保存路径，若路径不存在或者路径不是文件夹，则创建一个文件夹
        if (!filePath.exists() || !filePath.isDirectory()) {
            filePath.mkdirs();
        }

        //随机生成二维码文件名
        String file = new Random().nextInt(99999999)+".jpg";

        //将二维码写入文件
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));

    }
}
