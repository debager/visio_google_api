package kz.bmc.controller;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class WebFlux {
    private static final String pngPath = "C:\\Users\\marat\\Downloads\\Telegram Desktop\\3\\test\\texas.png";

    @GetMapping("/vq")
    public String getClob() throws IOException {

        return parse();
    }

    public static String parse() throws IOException {

        File file = new File("C:\\Projects\\ReactiveClob\\src\\main\\resources\\3\\3\\");

        for (String filename : Objects.requireNonNull(file.list())) {

            String absoluteFileName = file.getPath() + "\\" + filename;
            log.info("absoluteFileName {}", absoluteFileName);
            FileInputStream fileInputStream = new FileInputStream(absoluteFileName);

            ImageAnnotatorClient client = ImageAnnotatorClient.create();

            String longtext = detectText(fileInputStream, client);
            fileInputStream.close();
            Path source = Paths.get(absoluteFileName);
            assert longtext != null;

            if (longtext.toLowerCase().contains("texas")) {
                Files.move(source, Paths.get("C:\\Projects\\ReactiveClob\\src\\main\\resources\\3\\texas\\" + filename), StandardCopyOption.REPLACE_EXISTING);
            } else if (longtext.toLowerCase().contains("california")) {
                Files.move(source, Paths.get("C:\\Projects\\ReactiveClob\\src\\main\\resources\\3\\california\\" + filename), StandardCopyOption.REPLACE_EXISTING);
            } else if (longtext.toLowerCase().contains("colorado")) {
                Files.move(source, Paths.get("C:\\Projects\\ReactiveClob\\src\\main\\resources\\3\\colorado\\" + filename), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(source, Paths.get("C:\\Projects\\ReactiveClob\\src\\main\\resources\\3\\other\\" + filename), StandardCopyOption.REPLACE_EXISTING);
            }
        }


        return "Done";
    }

    public static String detectText(FileInputStream filePath, ImageAnnotatorClient imageAnnotatorClient) throws IOException {


        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(filePath);

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);


        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        String result = "";

        BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                log.error("Error {}", res.getError().getMessage());
                return null;
            }

            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                result = result.concat(annotation.getDescription());
            }
        }
        return result;


    }

}
