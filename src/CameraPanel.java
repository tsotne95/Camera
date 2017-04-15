import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Created by tsotne on 4/14/17.
 */
public class CameraPanel extends JPanel implements Runnable,ActionListener {
    BufferedImage image;
    VideoCapture capture;
    JButton screenshot;
    CascadeClassifier faceDetector;
    MatOfRect faceDetections;
    CameraPanel(){
        faceDetector=new CascadeClassifier(CameraPanel.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
        faceDetections=new MatOfRect();
        screenshot=new JButton("Screenshot");
        screenshot.addActionListener(this);
        add(screenshot);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        File out=new File("screenShot0.png");
        int i=0;
        while (out.exists()){
            out=new File("screenShot"+i+".png");
        }
        try {
            ImageIO.write(image,"png",out);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        capture=new VideoCapture(0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Mat webcam_image=new Mat();
        if(capture.isOpened()){
            while (true){
                capture.read(webcam_image);
                if(!webcam_image.empty()){
                    JFrame topFframe=(JFrame)SwingUtilities.getWindowAncestor(this);
                    topFframe.setSize(webcam_image.width()+40,webcam_image.height()+110);
                    matToBufferedImage(webcam_image);
                    faceDetector.detectMultiScale(webcam_image,faceDetections);
                    repaint();
                }
            }
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(image==null)return;
        g.drawImage(image,10,40,image.getWidth(),image.getHeight(),null);
        g.setColor(Color.RED);
        for(Rect rect:faceDetections.toArray()){
            g.drawRect(rect.x=10,rect.y+40,rect.width,rect.height);
        }
    }
    public void matToBufferedImage(Mat m){
        int width=m.width();
        int height=m.height();
        int channels=m.channels();
        byte source[]=new byte[width*height*channels];
        image=new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
        final byte target[]=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source,0,target,0,source.length);
    }
    public void switchCamera(int x){
        capture=new VideoCapture(x);
    }
}
