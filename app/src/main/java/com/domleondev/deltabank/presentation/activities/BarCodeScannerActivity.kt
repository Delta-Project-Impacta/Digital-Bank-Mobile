package com.domleondev.deltabank.presentation.activities

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.deltabank.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarCodeScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableEdgeToEdge()
        setContentView(R.layout.activity_bar_code_scanner)

        previewView = findViewById(R.id.camera_container)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupUI()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.camera_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onPause() {
        super.onPause()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupUI() {
        findViewById<ImageView>(R.id.ic_close).setOnClickListener {
            finish()
        }

        findViewById<AppCompatButton>(R.id.btn_manual_code).setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(android.util.Size(1920, 1080))
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarCodeAnalyzer { barcode ->
                        if (isScanning) {
                            isScanning = false
                            onBarCodeDetected(barcode)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "Erro ao iniciar câmera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun onBarCodeDetected(result: String) {
        runOnUiThread {
            Log.d("BarCode", "Código lido: $result")
            val intent = Intent(this, PaymentReviewActivity::class.java).apply {
                putExtra("BARCODE_DATA", result)
            }
            startActivity(intent)
            finish()
        }
    }

    private class BarCodeAnalyzer(
        private val onBarCodeDetected: (String) -> Unit
    ) : ImageAnalysis.Analyzer {

        private val scanner = BarcodeScanning.getClient()

        @ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return
            }

            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) {
                        imageProxy.close()
                        return@addOnSuccessListener
                    }

                    for (barcode in barcodes) {

                        val allowedFormats = listOf(
                            Barcode.FORMAT_ITF,
                            Barcode.FORMAT_CODE_128,
                            Barcode.FORMAT_CODE_39
                        )

                        if (barcode.format !in allowedFormats) continue

                        val raw = barcode.rawValue ?: continue

                        if (raw.length < 30) continue

                        onBarCodeDetected(raw)
                        break
                    }
                }
                .addOnFailureListener {
                    Log.e("BarCodeScanner", "Falha ao analisar", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Permissão necessária para usar a câmera", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
