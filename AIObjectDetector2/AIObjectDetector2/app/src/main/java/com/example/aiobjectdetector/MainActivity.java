package com.example.aiobjectdetector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private PreviewView previewView;
    private TextView resultText;
    private ObjectDetector objectDetector;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        resultText = findViewById(R.id.textResult);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            startCamera();
        }

        // ✅ Cấu hình model EfficientDet-Lite2 với 1 thread
        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setScoreThreshold(0.5f) // 50% độ tin cậy trở lên mới nhận
                        .setMaxResults(5)        // Tối đa 5 đối tượng mỗi frame
                        .setBaseOptions(BaseOptions.builder().setNumThreads(1).build())
                        .build();

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(this, "model.tflite", options);
            Log.d("ObjectDetector", "EfficientDet model loaded thành công.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ObjectDetector", "Lỗi khi load model: " + e.getMessage());
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // ✅ Giảm độ phân giải xuống 320x240 tránh crash trên máy Vivo
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(320, 240))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy image) {
        if (objectDetector == null || image == null || image.getImage() == null) {
            image.close();
            return;
        }

        TensorImage tensorImage = TensorImage.fromBitmap(
                ImageUtils.imageProxyToBitmap(image));

        List<Detection> results = objectDetector.detect(tensorImage);

        // ✅ Hiển thị nhiều kết quả nhận diện (label + độ tin cậy)
        runOnUiThread(() -> {
            if (!results.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Detection detection : results) {
                    if (!detection.getCategories().isEmpty()) {
                        String label = detection.getCategories().get(0).getLabel();
                        float score = detection.getCategories().get(0).getScore();
                        builder.append(label)
                                .append(" (")
                                .append(String.format("%.1f", score * 100))
                                .append("%)\n");
                    }
                }
                resultText.setText(builder.toString());
            } else {
                resultText.setText("Không phát hiện.");
            }
        });

        image.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }
}
