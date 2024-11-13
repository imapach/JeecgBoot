package org.jeecgframework.boot.print.client.service;

import cn.hutool.core.collection.ListUtil;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ImageTemplate {

    private BackGround backGround;
    private List<ImagePosition> imagePositions;


    @Data
    public static class ImagePosition {
        private int width;
        private int height;
        private int x;
        private int y;

        public BufferedImage format(String image) throws IOException {
            // 创建新的图片
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            // 设置背景颜色为白色
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(new File(image));
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 计算缩放比例
            double scaleX = (double) width / originalWidth;
            double scaleY = (double) height / originalHeight;
            double scale = Math.min(scaleX, scaleY);

            int newWidth = (int) (scale * originalWidth);
            int newHeight = (int) (scale * originalHeight);

            // 计算居中绘制的位置
            int x = (width - newWidth) / 2;
            int y = (height - newHeight) / 2;

            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), x, y, null);
            g2d.dispose();

            return resizedImage;
        }
    }

    @Data
    public static class BackGround {
        private String name;
        private int width;
        private int height;
    }


    /**
     * 所有的图片按排版返回要打印的图片
     * @param images
     * @return
     * @throws IOException
     */
    public List<BufferedImage> formatImages(List<String> images) throws IOException {
        List<List<String>> split = ListUtil.split(images, imagePositions.size());

        List<BufferedImage> result = new ArrayList<>();


        for (List<String> strings : split) {

            BufferedImage backgroundImg = new BufferedImage(backGround.width, backGround.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = backgroundImg.createGraphics();
            // 设置背景颜色为白色
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, backGround.width, backGround.height);

            Iterator<ImagePosition> iterator = imagePositions.iterator();
            for (String string : strings) {
                ImagePosition imagePosition = iterator.next();
                BufferedImage bufferedImage = imagePosition.format(string);

                // 设置抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(bufferedImage, imagePosition.x, imagePosition.y, null);
            }
            g2d.dispose();
            result.add(backgroundImg);
        }
        return result;
    }


}
