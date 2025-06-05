package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/scanner")
public class BarcodeScannerController {

    @GetMapping
    public String scannerPage() {
        return "scanner/scanner";
    }

    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity<?> processImage(@RequestBody Map<String, String> request) {
        try {
            // Decodificar la imagen base64
            String imageData = request.get("imageData").split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            // Configurar el lector de códigos
            MultiFormatReader reader = new MultiFormatReader();
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, 
                java.util.Arrays.asList(
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.EAN_8,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.UPC_E,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.CODE_39
                )
            );

            // Procesar la imagen
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(
                    new BufferedImageLuminanceSource(image)
                )
            );

            Result result = reader.decode(binaryBitmap, hints);
            
            Map<String, String> response = new HashMap<>();
            response.put("code", result.getText());
            return ResponseEntity.ok(response);

        } catch (NotFoundException e) {
            // No se encontró ningún código
            return ResponseEntity.ok(Map.of("code", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 