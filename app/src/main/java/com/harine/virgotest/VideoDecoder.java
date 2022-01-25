package com.harine.virgotest;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder {
    public static final String TAG = "VideoDecoder";

    static final int BORDER_LINE = 4;
    static final int ALIGN_W = 8;
    static final int ALIGN_H = 4;
    public static final String MPEG2_MIME = "video/mp4v-es";
    public static final String H264_MIME = "video/avc";
    public static final String H265_MIME = "video/hevc";
    public static final String MJPEG_MINE = "video/mjpeg";

    private MediaCodec  mDecoder;
    private MediaFormat mFormat;
    private MediaCodec.BufferInfo mBufferInfo;
    private ByteBuffer[] mInputBuffers;
    private ByteBuffer[] mOutputBuffers;
    private boolean bDecoderStart;
    private long    timeMillls = 0;
    private int     fps = 0;

    public VideoDecoder() {
    }

    String buildCodecSettings(int mx, int my, int mw, int mh){
        int order = 0;
        int codecEnable = 1;
        
        int x = ((mx+ALIGN_W-1)/ALIGN_W)*ALIGN_W;
        int y = ((my+ALIGN_H-1)/ALIGN_H)*ALIGN_H;
        int ww = ((mw+ALIGN_W-1)/ALIGN_W)*ALIGN_W;
        int hh = ((mh+ALIGN_H-1)/ALIGN_H)*ALIGN_H;

        int dx = x + BORDER_LINE;
        int dy = y + BORDER_LINE;
        int dw = ww - BORDER_LINE*2;
        int dh = hh - BORDER_LINE*2;
            
        String codecStr = ""+order+":"+codecEnable+":"+dx+":"+dy+":"+dw+":"+dh;
        Log.d(TAG, "buildCodecSettings>>>codecStr="+codecStr);
        
        return codecStr;
    }

    public void createDecoder(int px, int py, int pw, int ph, 
            int width, int height, String mine, Surface surface) {
        try {
            mFormat = MediaFormat.createVideoFormat(mine, width, height);
            mDecoder = MediaCodec.createDecoderByType(mine);

            //add sdt
            //String codecStr = buildCodecSettings(px, py, pw, ph);
            //Log.d(TAG, "createDecoder>>add hr_codec_settings = "+codecStr);
            //mFormat.setString("hr_codec_settings", codecStr);
            //end
        
            mDecoder.configure(mFormat, surface, null, 0);
            mDecoder.start();
            mBufferInfo = new MediaCodec.BufferInfo();
            mInputBuffers = mDecoder.getInputBuffers();
            mOutputBuffers = mDecoder.getOutputBuffers();
            mDecoder.getOutputBuffers();
            bDecoderStart = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            bDecoderStart = false;
            release();
        } finally {

        }
    }
    
    public void createDecoder(int width, int height, String mine, Surface surface) {
		Log.i(TAG, "createDecoder.....");
        try {
            mFormat = MediaFormat.createVideoFormat(mine, width, height);
            mDecoder = MediaCodec.createDecoderByType(mine);
            mDecoder.configure(mFormat, surface, null, 0);
            mDecoder.start();
            mBufferInfo = new MediaCodec.BufferInfo();
            mInputBuffers = mDecoder.getInputBuffers();
            mOutputBuffers = mDecoder.getOutputBuffers();
            mDecoder.getOutputBuffers();
            bDecoderStart = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            bDecoderStart = false;
            release();
        } finally {

        }
    }
	
	public void createDecoder(int width, int height, String mine, Surface surface, boolean lowdelay) {
		Log.i(TAG, "createDecoder.....");
        try {
            mFormat = MediaFormat.createVideoFormat(mine, width, height);
            mDecoder = MediaCodec.createDecoderByType(mine);
			mFormat.setInteger("low-delay-enable", lowdelay?1:0);
			mFormat.setInteger("fast-output-mode", lowdelay?1:0);
            mDecoder.configure(mFormat, surface, null, 0);
            mDecoder.start();
            mBufferInfo = new MediaCodec.BufferInfo();
            mInputBuffers = mDecoder.getInputBuffers();
            mOutputBuffers = mDecoder.getOutputBuffers();
            mDecoder.getOutputBuffers();
            bDecoderStart = true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            bDecoderStart = false;
            release();
        } finally {

        }
    }

    public void decodeFrame(byte[] buf, int ptsMs) {
        synchronized (mDecoder) {
            int inputIndex = mDecoder.dequeueInputBuffer(0);
            if (inputIndex >= 0) {
                ByteBuffer inputBuffer;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    inputBuffer = mInputBuffers[inputIndex];
                } else {
                    inputBuffer = mDecoder.getInputBuffer(inputIndex);
                }

                if (inputBuffer != null) {
                    inputBuffer.clear();
                    inputBuffer.put(buf, 0, buf.length);
					if (0 == ptsMs) {
                    mDecoder.queueInputBuffer(inputIndex, 0, buf.length, mBufferInfo.presentationTimeUs, 0);
					}else {
						mDecoder.queueInputBuffer(inputIndex, 0, buf.length, ptsMs*1000, 0);
					}
                }else{
                	Log.e(TAG, "inputBuffer.....null");
				}
            } else {
				//Thread.currentThread().sleep(40);
				Log.e(TAG, "dequeueInputBuffer.....fail ");
			}
			
            int outIndex = 0;
            while (outIndex >= 0) {
                outIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, 500);
                if (outIndex >= 0) {
                    mDecoder.releaseOutputBuffer(outIndex, true);
                } else if (outIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    mOutputBuffers = mDecoder.getOutputBuffers();
                } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Subsequent data will conform to new format.
                    mFormat = mDecoder.getOutputFormat();
                }
            }
        }

    }

    public boolean getStart() {
        return bDecoderStart;
    }

    public void release() {
        if (mDecoder != null) {
            bDecoderStart = false;
            synchronized (mDecoder) {
                try {
                    mDecoder.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDecoder.release();
                    mDecoder = null;
                    Log.d(TAG, "decoder release!");
                }
            }
        }
    }
}

